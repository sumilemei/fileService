package com.example.fileupload.service.impl;

import com.example.fileupload.config.MinioStorageProperties;
import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.exception.FileUploadException;
import com.example.fileupload.service.FileUploadService;
import com.example.fileupload.util.FileUtil;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 文件上传服务实现
 */
@Slf4j
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.max-file-size}")
    private Long maxFileSize;

    @Resource
    private MinioClient minioClient;

    @Resource
    private MinioStorageProperties minioStorageProperties;

    @PostConstruct
    public void init() {
        try {
            FileUtil.createDirectoryIfNotExists(uploadPath);
            log.info("文件上传目录初始化完成: {}", uploadPath);
        } catch (IOException e) {
            log.error("文件上传目录初始化失败", e);
            throw new FileUploadException("文件上传目录初始化失败");
        }
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        // 校验文件
        validateFile(file);

        try {
            // 生成唯一文件名
            String originalFileName = file.getOriginalFilename();
            String uniqueFileName = FileUtil.generateUniqueFileName(originalFileName);

            // 构建文件路径
            Path filePath = Paths.get(uploadPath, uniqueFileName);

            // 保存文件
            Files.copy(file.getInputStream(), filePath);

            log.info("文件上传成功: {} -> {}", originalFileName, uniqueFileName);

            // 构建响应
            return FileUploadResponse.builder()
                    .fileName(uniqueFileName)
                    .originalFileName(originalFileName)
                    .fileSize(file.getSize())
                    .contentType(file.getContentType())
                    .filePath(filePath.toString())
                    .uploadTime(LocalDateTime.now())
                    .fileUrl("/api/files/download/" + uniqueFileName)
                    .build();

        } catch (IOException e) {
            log.error("文件上传失败: {}", file.getOriginalFilename(), e);
            throw new FileUploadException("文件上传失败: " + e.getMessage());
        }
    }

    @Override
    public List<FileUploadResponse> uploadFiles(List<MultipartFile> files) {
        List<FileUploadResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                FileUploadResponse response = uploadFile(file);
                responses.add(response);
            } catch (Exception e) {
                log.error("批量上传中文件失败: {}", file.getOriginalFilename(), e);
                // 继续处理其他文件
            }
        }

        return responses;
    }

    @Override
    public byte[] downloadFile(String fileName) {
        try {
            Path filePath = Paths.get(uploadPath, fileName);

            if (!Files.exists(filePath)) {
                throw new FileUploadException(404, "文件不存在: " + fileName);
            }

            log.info("文件下载: {}", fileName);
            return Files.readAllBytes(filePath);

        } catch (IOException e) {
            log.error("文件下载失败: {}", fileName, e);
            throw new FileUploadException("文件下载失败: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteFile(String fileName) {
        Path filePath = Paths.get(uploadPath, fileName);

        if (!Files.exists(filePath)) {
            throw new FileUploadException(404, "文件不存在: " + fileName);
        }

        boolean deleted = FileUtil.deleteFile(filePath.toString());
        if (deleted) {
            log.info("文件删除成功: {}", fileName);
        } else {
            log.error("文件删除失败: {}", fileName);
        }

        return deleted;
    }

    @Override
    public FileUploadResponse getFileInfo(String fileName) {
        Path filePath = Paths.get(uploadPath, fileName);

        if (!Files.exists(filePath)) {
            throw new FileUploadException(404, "文件不存在: " + fileName);
        }

        try {
            long fileSize = Files.size(filePath);
            String contentType = Files.probeContentType(filePath);

            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .fileSize(fileSize)
                    .contentType(contentType)
                    .filePath(filePath.toString())
                    .fileUrl("/api/files/download/" + fileName)
                    .build();

        } catch (IOException e) {
            log.error("获取文件信息失败: {}", fileName, e);
            throw new FileUploadException("获取文件信息失败: " + e.getMessage());
        }
    }

    /**
     * 校验文件
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new FileUploadException(400, "文件不能为空");
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null || originalFileName.isEmpty()) {
            throw new FileUploadException(400, "文件名不能为空");
        }

        // 校验文件类型
        if (!FileUtil.isValidFileType(originalFileName)) {
            throw new FileUploadException(400, "不支持的文件类型，仅支持: jpg, jpeg, png, gif, pdf, doc, docx, xls, xlsx, txt, zip");
        }

        // 校验文件大小
        if (!FileUtil.isValidFileSize(file.getSize(), maxFileSize)) {
            throw new FileUploadException(400, "文件大小超过限制，最大允许: " + FileUtil.formatFileSize(maxFileSize));
        }
    }

    /**
     * minio上传文件
     */
    public FileUploadResponse minioUpload(MultipartFile file){
        log.info("选用minio上传");

        String bucketName = minioStorageProperties.getBucketName();
        String fileName = this.generateObjectName(file.getOriginalFilename());
        long size = file.getSize();
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        try {
            ensureBucketExists(bucketName);
            InputStream inputStream = file.getInputStream();
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(inputStream,size,-1)
                    .contentType(contentType)
                    .build());
            log.info("文件上传成功: {} -> {}", file.getOriginalFilename(), fileName);
            // 构建响应
            return FileUploadResponse.builder()
                    .fileName(fileName)
                    .originalFileName(file.getOriginalFilename())
                    .fileSize(size)
                    .contentType(contentType)
                    .uploadTime(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 生成唯一的对象名称（避免文件名冲突）
     * 格式：UUID_原始文件名
     */
    private String generateObjectName(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "_" + originalFilename;
    }

    /**
     * 确保桶存在，不存在则创建
     */
    private void ensureBucketExists(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                        .bucket(bucketName)
                        .build()
        );

        if (!exists) {
            minioClient.makeBucket(
                    MakeBucketArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
            log.info("桶创建成功: {}", bucketName);
        }
    }
}

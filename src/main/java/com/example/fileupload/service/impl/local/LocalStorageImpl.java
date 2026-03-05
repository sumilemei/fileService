package com.example.fileupload.service.impl.local;

import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.exception.FileUploadException;
import com.example.fileupload.service.FileUploadService;
import com.example.fileupload.util.FileUtil;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
public class LocalStorageImpl implements FileUploadService {

    @Value("${file.upload.path}")
    private String uploadPath;

    @Value("${file.upload.max-file-size}")
    private Long maxFileSize;

    @PostConstruct
    public void localInit(){
        Path path = Paths.get(uploadPath);
        if(!Files.exists(path)){
            try {
                Files.createDirectories(path);
                log.info("本地存储目录创建成功--{}",uploadPath);
            } catch (IOException e) {
                log.error("本地存储目录初始化失败",e);
                throw new RuntimeException("本地存储目录初始化失败",e);
            }
        }
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
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
        return List.of();
    }

    @Override
    public byte[] downloadFile(String fileName) {
        return new byte[0];
    }

    @Override
    public boolean deleteFile(String fileName) {
        return false;
    }

    @Override
    public FileUploadResponse getFileInfo(String fileName) {
        return null;
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
}

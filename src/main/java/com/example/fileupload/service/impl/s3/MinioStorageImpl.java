package com.example.fileupload.service.impl.s3;

import com.example.fileupload.config.MinioStorageProperties;
import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.service.FileUploadService;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MinioStorageImpl implements FileUploadService {

    private final MinioClient minioClient;
    private final MinioStorageProperties minioStorageProperties;


    @PostConstruct
    public void minioInit(){
        String bucketName = minioStorageProperties.getBucketName();
        boolean exists = false;
        try {
            exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucketName)
                            .build()
            );
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            throw new RuntimeException(e);
        }

        if (!exists) {
            try {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucketName)
                                .build()
                );
            } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                     InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                     XmlParserException e) {
                throw new RuntimeException(e);
            }
            log.info("桶创建成功: {}", bucketName);
        }
    }

    @Override
    public FileUploadResponse uploadFile(MultipartFile file) {
        log.info("选用minio上传");

        String bucketName = minioStorageProperties.getBucketName();
        String fileName = this.generateObjectName(file.getOriginalFilename());
        long size = file.getSize();
        String contentType = file.getContentType();
        if (contentType == null || contentType.isEmpty()) {
            contentType = "application/octet-stream";
        }

        try {
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
     * 生成唯一的对象名称（避免文件名冲突）
     * 格式：UUID_原始文件名
     */
    private String generateObjectName(String originalFilename) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid + "_" + originalFilename;
    }
}

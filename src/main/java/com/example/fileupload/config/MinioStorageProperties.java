package com.example.fileupload.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Component
@Data
public class MinioStorageProperties {
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private String bucketName;
}

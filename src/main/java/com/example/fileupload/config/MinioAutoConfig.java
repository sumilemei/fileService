package com.example.fileupload.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MinioAutoConfig {

    private final MinioStorageProperties minioStorageProperties;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(minioStorageProperties.getEndpoint())
                .credentials(minioStorageProperties.getAccessKey(),
                        minioStorageProperties.getSecretKey())
                .build();
    }

}

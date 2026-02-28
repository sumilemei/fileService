package com.example.fileupload.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.io.File;

/**
 * 文件上传配置
 */
@Configuration
public class FileUploadConfig implements WebMvcConfigurer {

    @Value("${file.upload.path}")
    private String uploadPath;

    @PostConstruct
    public void init() {
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
    }

    /**
     * 配置静态资源映射
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置文件下载路径映射
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath);
    }
}

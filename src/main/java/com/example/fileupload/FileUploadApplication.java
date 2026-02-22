package com.example.fileupload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 文件上传应用启动类
 */
@SpringBootApplication
public class FileUploadApplication {

    public static void main(String[] args) {
        SpringApplication.run(FileUploadApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("文件上传服务启动成功！");
        System.out.println("访问地址: http://localhost:9090/api");
        System.out.println("========================================\n");
    }
}

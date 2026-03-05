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
        // ANSI转义代码开始
        final String ANSI_GREEN = "\u001B[32m";
        final String ANSI_RESET = "\u001B[0m";
        System.out.println(ANSI_GREEN + "\n========================================" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "文件上传服务启动成功！" + ANSI_RESET);
        System.out.println("访问地址: http://localhost:9090/api");
        System.out.println(ANSI_GREEN + "========================================\n" + ANSI_RESET);
    }
}

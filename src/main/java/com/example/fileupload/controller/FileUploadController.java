package com.example.fileupload.controller;

import com.example.fileupload.dto.ApiResponse;
import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.dto.enums.StorageType;
import com.example.fileupload.service.StorageContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileUploadController {

    @jakarta.annotation.Resource
    private StorageContext storageContext;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file,@RequestParam("storageType") String storageType) {

        log.info("接收到文件上传请求: {}, 存储类型: {}", file.getOriginalFilename(), storageType);
        StorageType type = StorageType.fromCode(storageType);
        FileUploadResponse response = storageContext.uploadFile(file, type);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("文件上传成功", response));
    }

}

package com.example.fileupload.service;

import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.dto.enums.StorageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class StorageContext {

    private final StorageStrategyFactory storageStrategyFactory;

    /**
     * 上传单个文件
     */
    public FileUploadResponse uploadFile(MultipartFile file, StorageType storageType){
        log.info("StorageContext: 开始上传文件: {}, 存储类型: {}",
                file.getOriginalFilename(), storageType);

        FileUploadService storage = storageStrategyFactory.getStorage(storageType);
        FileUploadResponse fileUploadResponse = storage.uploadFile(file);

        log.info("StorageContext: 文件上传成功: {}", fileUploadResponse.getFileName());
        return fileUploadResponse;
    }
}

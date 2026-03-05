package com.example.fileupload.service;

import com.example.fileupload.dto.enums.StorageType;
import com.example.fileupload.service.impl.local.LocalStorageImpl;
import com.example.fileupload.service.impl.s3.MinioStorageImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class StorageStrategyFactory {

    private final Map<StorageType, FileUploadService> storageModeCache = new ConcurrentHashMap<>();

    public StorageStrategyFactory(LocalStorageImpl localStorage, MinioStorageImpl minioStorage){
        storageModeCache.put(StorageType.LOCAL,localStorage);
        storageModeCache.put(StorageType.MINIO,minioStorage);
        log.info("存储策略工厂已初始化完成，已注册策略--{}",storageModeCache.keySet());
    }

    public FileUploadService getStorage(StorageType storageType){
        FileUploadService fileUploadService = storageModeCache.get(storageType);
        if (fileUploadService == null ){
            log.error("未找到的存储类型--{}",storageType);
            throw new IllegalArgumentException("不支持的存储类型：" + storageType);
        }
        return fileUploadService;
    }
}
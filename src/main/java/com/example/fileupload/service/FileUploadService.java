package com.example.fileupload.service;

import com.example.fileupload.dto.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传服务接口
 */
public interface FileUploadService {

    /**
     * 上传单个文件
     *
     * @param file 文件
     * @return 文件上传响应信息
     */
    FileUploadResponse uploadFile(MultipartFile file);

    /**
     * 上传多个文件
     *
     * @param files 文件列表
     * @return 文件上传响应信息列表
     */
    List<FileUploadResponse> uploadFiles(List<MultipartFile> files);

    /**
     * 下载文件
     *
     * @param fileName 文件名
     * @return 文件字节数组
     */
    byte[] downloadFile(String fileName);

    /**
     * 删除文件
     *
     * @param fileName 文件名
     * @return 是否删除成功
     */
    boolean deleteFile(String fileName);

    /**
     * 获取文件信息
     *
     * @param fileName 文件名
     * @return 文件信息
     */
    FileUploadResponse getFileInfo(String fileName);
}

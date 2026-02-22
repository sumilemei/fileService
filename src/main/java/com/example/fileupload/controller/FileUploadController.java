package com.example.fileupload.controller;

import com.example.fileupload.dto.ApiResponse;
import com.example.fileupload.dto.FileUploadResponse;
import com.example.fileupload.service.FileUploadService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/files")
public class FileUploadController {

    @javax.annotation.Resource
    private FileUploadService fileUploadServicee;

    /**
     * 单文件上传
     */
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        log.info("接收到文件上传请求: {}", file.getOriginalFilename());
        FileUploadResponse response = fileUploadServicee.uploadFile(file);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("文件上传成功", response));
    }

    /**
     * 多文件上传
     */
    @PostMapping("/upload/multiple")
    public ResponseEntity<ApiResponse<List<FileUploadResponse>>> uploadFiles(
            @RequestParam("files") List<MultipartFile> files) {

        log.info("接收到多文件上传请求，文件数量: {}", files.size());
        List<FileUploadResponse> responses = fileUploadServicee.uploadFiles(files);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("文件上传成功", responses));
    }

    /**
     * 文件下载
     */
    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable("fileName") String fileName) {

        log.info("接收到文件下载请求: {}", fileName);
        byte[] fileContent = fileUploadServicee.downloadFile(fileName);

        ByteArrayResource resource = new ByteArrayResource(fileContent);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(fileContent.length)
                .body(resource);
    }

    /**
     * 删除文件
     */
    @DeleteMapping("/delete/{fileName:.+}")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @PathVariable("fileName") String fileName) {

        log.info("接收到文件删除请求: {}", fileName);
        fileUploadServicee.deleteFile(fileName);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success("文件删除成功", null));
    }

    /**
     * 获取文件信息
     */
    @GetMapping("/info/{fileName:.+}")
    public ResponseEntity<ApiResponse<FileUploadResponse>> getFileInfo(
            @PathVariable("fileName") String fileName) {

        log.info("接收到文件信息查询请求: {}", fileName);
        FileUploadResponse response = fileUploadServicee.getFileInfo(fileName);

        return ResponseEntity
                .ok()
                .body(ApiResponse.success(response));
    }
}

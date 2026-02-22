package com.example.fileupload.controller;

import com.example.fileupload.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
public class HealthController {

    /**
     * 健康检查接口
     */
    @GetMapping
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "文件上传服务");
        info.put("status", "运行中");
        info.put("timestamp", LocalDateTime.now());
        info.put("version", "1.0.0");

        return ApiResponse.success("服务正常", info);
    }

    /**
     * API信息
     */
    @GetMapping("/info")
    public ApiResponse<Map<String, String>> info() {
        Map<String, String> apis = new HashMap<>();
        apis.put("单文件上传", "POST /api/files/upload");
        apis.put("多文件上传", "POST /api/files/upload/multiple");
        apis.put("文件下载", "GET /api/files/download/{fileName}");
        apis.put("文件删除", "DELETE /api/files/delete/{fileName}");
        apis.put("文件信息", "GET /api/files/info/{fileName}");

        return ApiResponse.success("API列表", apis);
    }
}

package com.example.fileupload.exception;

import lombok.Getter;

/**
 * 文件上传自定义异常
 */
@Getter
public class FileUploadException extends RuntimeException {

    private final Integer code;

    public FileUploadException(String message) {
        super(message);
        this.code = 500;
    }

    public FileUploadException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
        this.code = 500;
    }
}

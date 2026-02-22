# Spring Boot 文件上传企业级项目

## 项目简介

这是一个标准的Spring Boot企业级文件上传项目，采用分层架构设计，实现了完整的文件上传、下载、删除等功能，并包含完善的异常处理和参数校验机制。

## 技术栈

- **Spring Boot**: 3.2.2
- **Java**: 17
- **构建工具**: Maven
- **其他**: Lombok, Validation

## 项目结构

```
file-upload-demo/
├── src/
│   ├── main/
│   │   ├── java/com/example/fileupload/
│   │   │   ├── FileUploadApplication.java     # 启动类
│   │   │   ├── config/
│   │   │   │   └── FileUploadConfig.java      # 文件上传配置
│   │   │   ├── controller/
│   │   │   │   └── FileUploadController.java  # 文件上传控制器
│   │   │   ├── service/
│   │   │   │   ├── FileUploadService.java     # 文件上传服务接口
│   │   │   │   └── impl/
│   │   │   │       └── FileUploadServiceImpl.java  # 服务实现
│   │   │   ├── dto/
│   │   │   │   ├── ApiResponse.java           # 统一响应DTO
│   │   │   │   └── FileUploadResponse.java    # 文件上传响应DTO
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java  # 全局异常处理
│   │   │   │   └── FileUploadException.java    # 自定义异常
│   │   │   └── util/
│   │   │       └── FileUtil.java              # 文件工具类
│   │   └── resources/
│   │       └── application.yml                # 应用配置
│   └── test/
└── pom.xml
```

## 功能特性

- ✅ 单文件上传
- ✅ 多文件上传
- ✅ 文件下载
- ✅ 文件删除
- ✅ 文件大小限制（最大10MB）
- ✅ 文件类型校验
- ✅ 文件名唯一性处理（UUID，防止覆盖）
- ✅ 统一响应格式
- ✅ 全局异常处理
- ✅ 日志记录

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+

### 安装和运行

1. 克隆或下载项目到本地

2. 进入项目目录
```bash
cd file-upload-demo
```

3. 编译项目
```bash
mvn clean install
```

4. 运行项目
```bash
mvn spring-boot:run
```

或者直接运行JAR包：
```bash
java -jar target/file-upload-demo-1.0.0.jar
```

5. 访问服务
服务启动后，访问地址：`http://localhost:8080/api`

## API接口文档

### 1. 单文件上传

**接口**: `POST /api/files/upload`

**参数**:
- `file`: MultipartFile (必填)

**请求示例** (curl):
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@/path/to/your/file.jpg"
```

**请求示例** (Postman):
- Method: POST
- URL: `http://localhost:8080/api/files/upload`
- Body: form-data
  - Key: `file`
  - Type: File
  - Value: 选择文件

**响应示例**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": {
    "fileName": "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg",
    "originalFileName": "test.jpg",
    "fileSize": 102400,
    "contentType": "image/jpeg",
    "filePath": "./uploads/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg",
    "uploadTime": "2025-01-20T10:30:00",
    "fileUrl": "/api/files/download/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg"
  },
  "timestamp": "2025-01-20T10:30:00"
}
```

### 2. 多文件上传

**接口**: `POST /api/files/upload/multiple`

**参数**:
- `files`: MultipartFile[] (必填)

**请求示例** (curl):
```bash
curl -X POST http://localhost:8080/api/files/upload/multiple \
  -F "files=@/path/to/file1.jpg" \
  -F "files=@/path/to/file2.pdf"
```

**响应示例**:
```json
{
  "code": 200,
  "message": "文件上传成功",
  "data": [
    {
      "fileName": "uuid1.jpg",
      "originalFileName": "file1.jpg",
      "fileSize": 102400,
      "contentType": "image/jpeg",
      "filePath": "./uploads/uuid1.jpg",
      "uploadTime": "2025-01-20T10:30:00",
      "fileUrl": "/api/files/download/uuid1.jpg"
    },
    {
      "fileName": "uuid2.pdf",
      "originalFileName": "file2.pdf",
      "fileSize": 204800,
      "contentType": "application/pdf",
      "filePath": "./uploads/uuid2.pdf",
      "uploadTime": "2025-01-20T10:30:01",
      "fileUrl": "/api/files/download/uuid2.pdf"
    }
  ],
  "timestamp": "2025-01-20T10:30:01"
}
```

### 3. 文件下载

**接口**: `GET /api/files/download/{fileName}`

**参数**:
- `fileName`: 路径参数，文件名

**请求示例** (curl):
```bash
curl -X GET http://localhost:8080/api/files/download/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg \
  -o downloaded_file.jpg
```

### 4. 文件删除

**接口**: `DELETE /api/files/delete/{fileName}`

**参数**:
- `fileName`: 路径参数，文件名

**请求示例** (curl):
```bash
curl -X DELETE http://localhost:8080/api/files/delete/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg
```

**响应示例**:
```json
{
  "code": 200,
  "message": "文件删除成功",
  "data": null,
  "timestamp": "2025-01-20T10:35:00"
}
```

### 5. 获取文件信息

**接口**: `GET /api/files/info/{fileName}`

**参数**:
- `fileName`: 路径参数，文件名

**请求示例** (curl):
```bash
curl -X GET http://localhost:8080/api/files/info/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6.jpg
```

## 配置说明

配置文件位于 `src/main/resources/application.yml`:

```yaml
spring:
  servlet:
    multipart:
      max-file-size: 10MB           # 单个文件最大大小
      max-request-size: 50MB        # 请求最大大小
      enabled: true

file:
  upload:
    path: ./uploads/                # 文件存储路径
    allowed-types: jpg,jpeg,png,gif,pdf,doc,docx,xls,xlsx,txt,zip
    max-file-size: 10485760         # 10MB in bytes

server:
  port: 8080                        # 服务端口
```

## 异常处理

项目实现了全局异常处理，主要异常类型：

- **400**: 文件为空、文件名不合法、文件类型不支持、文件大小超限
- **404**: 文件不存在
- **500**: 系统内部错误

所有异常都返回统一的JSON格式：

```json
{
  "code": 400,
  "message": "文件大小超过限制，最大允许10MB",
  "data": null,
  "timestamp": "2025-01-20T10:30:00"
}
```

## 测试

### 使用JUnit测试

```bash
mvn test
```

### 使用Postman/curl测试

1. 启动项目
2. 使用上述API接口文档中的curl命令进行测试
3. 或使用Postman导入以下配置进行测试

## 企业级特性

1. **分层架构**: Controller -> Service -> Util，职责清晰
2. **统一响应格式**: 所有接口返回统一的ApiResponse格式
3. **全局异常处理**: 使用@RestControllerAdvice统一处理异常
4. **参数校验**: 使用Validation进行参数校验
5. **日志记录**: 使用SLF4J记录关键操作日志
6. **配置化**: 所有配置集中在application.yml中
7. **工具类封装**: 文件操作工具类，提高代码复用性
8. **UUID文件名**: 防止文件名冲突和覆盖
9. **文件类型和大小校验**: 增强安全性
10. **RESTful API设计**: 符合REST规范

## 注意事项

1. 上传的文件默认存储在项目根目录下的 `uploads` 文件夹中
2. 文件大小限制为10MB，可在application.yml中修改
3. 支持的文件类型包括：jpg, jpeg, png, gif, pdf, doc, docx, xls, xlsx, txt, zip
4. 生产环境建议：
   - 配置文件存储路径为专用存储服务器
   - 添加文件访问权限控制
   - 实现文件加密存储
   - 添加数据库记录文件元信息
   - 实现文件过期清理机制

## 许可证

MIT License

## 作者

Spring Boot File Upload Demo

## 更新日志

### v1.0.0 (2025-01-20)
- 初始版本发布
- 实现单文件/多文件上传
- 实现文件下载和删除
- 实现全局异常处理
- 添加文件类型和大小校验

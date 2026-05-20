# MinIO File Storage Setup Guide

This guide explains how to set up and use the MinIO file storage integration in the filestorage application.

## Prerequisites

- Java 21
- Maven
- MinIO server running locally or remotely

## MinIO Server Setup

### Option 1: Using Docker (Recommended)

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=admin \
  -e MINIO_ROOT_PASSWORD=password123 \
  minio/minio server /data --console-address ":9001"
```

- **API Endpoint**: http://localhost:9000
- **Console URL**: http://localhost:9001
- **Default Username**: admin
- **Default Password**: password123

### Option 2: Local Installation

Visit https://docs.min.io/minio/baremetal/quickstart/quickstart.html for installation instructions.

## Application Configuration

Update `src/main/resources/application.properties`:

```properties
spring.application.name=filestorage

# MinIO Configuration
minio.url=http://localhost:9000
minio.access-key=admin
minio.secret-key=password123
minio.bucket-name=uploads
```

**Configuration Parameters:**
- `minio.url`: MinIO server endpoint (e.g., http://localhost:9000)
- `minio.access-key`: MinIO access key/username
- `minio.secret-key`: MinIO secret key/password
- `minio.bucket-name`: Default bucket name for file storage

## Building the Application

```bash
./mvnw clean build
```

## Running the Application

```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### 1. Upload a File

**Endpoint**: `POST /api/files/upload`

**Request**:
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@/path/to/file.txt"
```

**Response**:
```json
{
  "fileName": "1716192345678_file.txt",
  "message": "File uploaded successfully",
  "fileSize": 1024,
  "uploadedAt": "2024-05-20T10:45:45.678",
  "contentType": "text/plain"
}
```

### 2. Download a File

**Endpoint**: `GET /api/files/download/{fileName}`

**Request**:
```bash
curl -X GET http://localhost:8080/api/files/download/1716192345678_file.txt \
  -o downloaded_file.txt
```

### 3. Get File Information

**Endpoint**: `GET /api/files/info/{fileName}`

**Request**:
```bash
curl -X GET http://localhost:8080/api/files/info/1716192345678_file.txt
```

**Response**:
```json
{
  "fileName": "1716192345678_file.txt",
  "fileSize": 1024,
  "lastModified": "2024-05-20T10:45:45.123456Z",
  "etag": "abc123def456",
  "exists": true
}
```

### 4. Delete a File

**Endpoint**: `DELETE /api/files/{fileName}`

**Request**:
```bash
curl -X DELETE http://localhost:8080/api/files/1716192345678_file.txt
```

**Response**:
```json
{
  "message": "File deleted successfully",
  "fileName": "1716192345678_file.txt"
}
```

### 5. Get Presigned URL

**Endpoint**: `GET /api/files/presigned-url/{fileName}`

**Request**:
```bash
curl -X GET "http://localhost:8080/api/files/presigned-url/1716192345678_file.txt"
```

**Response**:
```json
{
  "presignedUrl": "http://localhost:9000/uploads/1716192345678_file.txt?X-Amz-Algorithm=...",
  "fileName": "1716192345678_file.txt",
  "expiryInSeconds": "3600"
}
```

Presigned URLs allow temporary access to files without authentication. The default expiration is 7 days. Useful for sharing files or public download links.

### 6. Check if File Exists

**Endpoint**: `GET /api/files/exists/{fileName}`

**Request**:
```bash
curl -X GET http://localhost:8080/api/files/exists/1716192345678_file.txt
```

**Response**:
```json
{
  "fileName": "1716192345678_file.txt",
  "exists": true
}
```

### 7. Health Check

**Endpoint**: `GET /api/files/health`

**Request**:
```bash
curl -X GET http://localhost:8080/api/files/health
```

**Response**:
```json
{
  "status": "UP",
  "message": "MinIO File Storage Service is running"
}
```

## Project Structure

```
src/main/java/com/smeydev/filestorage/
├── config/
│   └── MinioConfig.java              # MinIO client configuration
├── controller/
│   └── FileController.java           # REST API endpoints
├── dto/
│   ├── FileUploadResponse.java       # Upload response DTO
│   └── FileInfo.java                 # File information DTO
├── exception/
│   └── GlobalExceptionHandler.java   # Global exception handling
├── service/
│   └── MinioService.java             # MinIO operations service
└── FilestorageApplication.java       # Main application class
```

## Service Methods

### MinioService

The `MinioService` class provides the following methods:

- `ensureBucketExists()` - Creates bucket if it doesn't exist
- `uploadFile(MultipartFile file)` - Upload file and return file name
- `downloadFile(String fileName)` - Download file and return InputStream
- `deleteFile(String fileName)` - Delete file from MinIO
- `getFileInfo(String fileName)` - Get file metadata
- `getPresignedUrl(String fileName, int expiryInSeconds)` - Generate temporary access URL (note: expiryInSeconds parameter is optional)
- `fileExists(String fileName)` - Check if file exists

## Error Handling

All errors are handled by `GlobalExceptionHandler` and return structured error responses:

```json
{
  "timestamp": "2024-05-20T10:45:45.678",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Error uploading file",
  "path": "/api/files/upload"
}
```

## Security Considerations

1. **Credentials**: Store MinIO credentials securely using environment variables or a secrets manager
   ```properties
   minio.url=${MINIO_URL}
   minio.access-key=${MINIO_ACCESS_KEY}
   minio.secret-key=${MINIO_SECRET_KEY}
   minio.bucket-name=${MINIO_BUCKET_NAME}
   ```

2. **Network**: Use HTTPS in production

3. **File Naming**: Files are automatically timestamped to prevent conflicts

4. **Presigned URLs**: Set appropriate expiration times for security

## Troubleshooting

### Issue: Connection refused to MinIO
- Check if MinIO server is running
- Verify `minio.url` is correct
- Check firewall settings

### Issue: Authentication failed
- Verify `minio.access-key` and `minio.secret-key`
- Check MinIO console for correct credentials

### Issue: Bucket not found
- The application automatically creates the bucket if it doesn't exist
- Check `minio.bucket-name` configuration

## Dependencies

```xml
<dependency>
    <groupId>io.minio</groupId>
    <artifactId>minio</artifactId>
    <version>8.5.10</version>
</dependency>
```

## References

- MinIO Java SDK: https://docs.min.io/minio/baremetal/
- Spring Boot: https://spring.io/projects/spring-boot
- Lombok: https://projectlombok.org/

## Next Steps

1. Configure and start MinIO server
2. Build and run the application
3. Test file upload/download functionality
4. Integrate into your application logic
5. Consider adding authentication/authorization
6. Implement additional features as needed

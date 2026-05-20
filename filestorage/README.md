# MinIO File Storage Service

A Spring Boot application that exposes REST APIs for file upload, download, metadata, deletion, presigned URLs, and health checks using MinIO object storage.

## Features

- Upload files to MinIO
- Download files from MinIO
- Retrieve file metadata
- Delete files
- Generate presigned URLs for temporary access
- Check file existence
- Health check endpoint
- Swagger/OpenAPI UI for interactive API testing

## Prerequisites

- Java 21 or newer
- Maven 3.8+
- Docker (recommended for running MinIO)

## Getting Started

### 1. Start MinIO

Recommended: use Docker.

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=admin \
  -e MINIO_ROOT_PASSWORD=password123 \
  minio/minio server /data --console-address ":9001"
```

Or use the included `docker-compose.yml`:

```bash
docker-compose up -d
```

Verify MinIO:

- API: http://localhost:9000
- Console: http://localhost:9001
- Username: `admin`
- Password: `password123`

### 2. Build the application

```bash
./mvnw clean package
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Or run the packaged JAR:

```bash
java -jar target/filestorage-0.0.1-SNAPSHOT.jar
```

### 4. Open Swagger UI

Visit:

```text
http://localhost:8080/swagger-ui.html
```

Use the interactive documentation to test upload, download, info, delete, presigned URL, exists, and health endpoints.

## Configuration

Edit `src/main/resources/application.properties` to configure MinIO and server settings.

Example configuration:

```properties
spring.application.name=filestorage
server.port=8080

minio.url=http://localhost:9000
minio.access-key=admin
minio.secret-key=password123
minio.bucket-name=uploads

spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB
logging.level.com.smeydev.filestorage=DEBUG
```

### Environment variables

You can override values with environment variables:

```bash
export MINIO_URL=http://localhost:9000
export MINIO_ACCESS_KEY=admin
export MINIO_SECRET_KEY=password123
export MINIO_BUCKET_NAME=uploads
```

Then use the properties as:

```properties
minio.url=${MINIO_URL}
minio.access-key=${MINIO_ACCESS_KEY}
minio.secret-key=${MINIO_SECRET_KEY}
minio.bucket-name=${MINIO_BUCKET_NAME}
```

## API Endpoints

Base path: `/api/files`

- `POST /upload` - Upload a file
- `GET /download/{fileName}` - Download a file
- `GET /info/{fileName}` - Get file metadata
- `DELETE /{fileName}` - Delete a file
- `GET /presigned-url/{fileName}` - Generate a presigned URL
- `GET /exists/{fileName}` - Check if a file exists
- `GET /health` - Service health check

## Example cURL Requests

Upload a file:

```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@/path/to/file.txt"
```

Download a file:

```bash
curl -X GET http://localhost:8080/api/files/download/your-file-name \
  -o downloaded_file.txt
```

Get file info:

```bash
curl -X GET http://localhost:8080/api/files/info/your-file-name
```

Delete a file:

```bash
curl -X DELETE http://localhost:8080/api/files/your-file-name
```

Generate a presigned URL:

```bash
curl -X GET "http://localhost:8080/api/files/presigned-url/your-file-name?expiryInSeconds=3600"
```

Check existence:

```bash
curl -X GET http://localhost:8080/api/files/exists/your-file-name
```

Health check:

```bash
curl -X GET http://localhost:8080/api/files/health
```

## Project Structure

```
src/main/java/com/smeydev/filestorage/
├── config/
│   ├── MinioConfig.java
│   └── SwaggerConfig.java
├── controller/
│   └── FileController.java
├── dto/
│   ├── FileInfo.java
│   └── FileUploadResponse.java
├── exception/
│   └── GlobalExceptionHandler.java
├── service/
│   ├── FileStorageService.java
│   └── MinioService.java
└── FilestorageApplication.java
```

## Build and Test

Build the project:

```bash
./mvnw clean package
```

Run tests:

```bash
./mvnw test
```

## Notes

- The application uses Spring Boot and the MinIO Java SDK.
- The `MinioService` implementation handles bucket creation and file operations.
- `SwaggerConfig` enables API documentation in the browser.

## Troubleshooting

- If MinIO is unreachable, confirm the server is running on `localhost:9000`.
- If authentication fails, verify `MINIO_ROOT_USER` and `MINIO_ROOT_PASSWORD`.
- If port `8080` is taken, update `server.port` in `application.properties`.

## References

- [Spring Boot](https://spring.io/projects/spring-boot)
- [MinIO](https://min.io)
- [Swagger / OpenAPI](https://swagger.io)

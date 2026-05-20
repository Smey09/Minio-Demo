# Swagger/OpenAPI Documentation Guide

This project uses **Springdoc-OpenAPI** with **Swagger UI** for interactive API documentation.

## Accessing Swagger UI

Once the application is running, you can access the interactive API documentation at:

```
http://localhost:8080/swagger-ui.html
```

## OpenAPI/JSON Specification

The raw OpenAPI specification (JSON format) is available at:

```
http://localhost:8080/v3/api-docs
```

## Features

### Interactive API Testing
- Test all endpoints directly from the Swagger UI
- See real-time responses
- View request/response models
- Try different parameters

### Complete API Documentation
- Detailed descriptions for each endpoint
- Request/response schemas with examples
- Error codes and responses
- Parameter documentation

### Auto-Generated from Code
- Annotations in Java code automatically generate documentation
- No separate documentation files needed
- Always stays in sync with actual API

## Annotations Used

### Class Level
```java
@Tag(name = "File Storage", description = "APIs for file upload, download, and management with MinIO")
public class FileController { ... }
```

### Method Level
```java
@Operation(summary = "Upload a file", description = "Upload a file to MinIO storage")
@ApiResponses(value = {
    @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
    @ApiResponse(responseCode = "400", description = "Invalid request or empty file"),
    @ApiResponse(responseCode = "500", description = "Internal server error")
})
public ResponseEntity<FileUploadResponse> uploadFile(...) { ... }
```

### Parameter Level
```java
@Parameter(description = "File to upload", required = true)
@RequestParam("file") MultipartFile file
```

### DTO Level
```java
@Schema(description = "Response object for file upload")
public class FileUploadResponse {
    @Schema(description = "Uploaded file name", example = "1716192345678_document.pdf")
    private String fileName;
}
```

## Configuration

Configuration can be found in `application.properties`:

```properties
# Springdoc OpenAPI/Swagger Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operations-sorter=method
springdoc.swagger-ui.tags-sorter=alpha
```

### Configuration Options

| Property | Description | Default |
|----------|-------------|---------|
| `springdoc.api-docs.path` | API docs JSON endpoint | `/v3/api-docs` |
| `springdoc.swagger-ui.path` | Swagger UI path | `/swagger-ui.html` |
| `springdoc.swagger-ui.enabled` | Enable Swagger UI | `true` |
| `springdoc.swagger-ui.operations-sorter` | Sort operations by `method`, `alpha` | `alpha` |
| `springdoc.swagger-ui.tags-sorter` | Sort tags alphabetically | `alpha` |

## API Endpoints Documentation

All endpoints are fully documented in Swagger UI with:
- Operation IDs
- Request/Response models
- Example values
- Error responses
- Security schemes (if applicable)

### Available Endpoints

1. **POST /api/files/upload** - Upload a file
2. **GET /api/files/download/{fileName}** - Download a file
3. **GET /api/files/info/{fileName}** - Get file information
4. **DELETE /api/files/{fileName}** - Delete a file
5. **GET /api/files/presigned-url/{fileName}** - Get presigned URL
6. **GET /api/files/exists/{fileName}** - Check if file exists
7. **GET /api/files/health** - Health check

## Service Interface Pattern

The application follows a clean architecture with service interfaces:

### Structure

```
service/
├── FileStorageService.java          # Service interface
└── MinioService.java                # Implementation
```

### Benefits

1. **Loose Coupling** - Controller depends on interface, not implementation
2. **Testability** - Easy to mock interface for unit tests
3. **Flexibility** - Can switch implementations (e.g., AWS S3) without changing controller
4. **Maintainability** - Clear contracts for service operations

### Usage

```java
@RestController
public class FileController {
    private final FileStorageService fileStorageService;  // Interface
    
    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }
}
```

## Testing with Swagger UI

### Upload File
1. Go to `POST /api/files/upload` section
2. Click "Try it out"
3. Click "Choose File" and select a file
4. Click "Execute"
5. View the response

### Download File
1. Go to `GET /api/files/download/{fileName}` section
2. Enter the file name (e.g., `1716192345678_document.pdf`)
3. Click "Execute"
4. File will be downloaded

### Get File Info
1. Go to `GET /api/files/info/{fileName}` section
2. Enter the file name
3. Click "Execute"
4. View file metadata

## Example cURL Commands

### Upload
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@/path/to/file.pdf"
```

### Download
```bash
curl -X GET http://localhost:8080/api/files/download/1716192345678_file.pdf \
  -o downloaded_file.pdf
```

### Get Info
```bash
curl -X GET http://localhost:8080/api/files/info/1716192345678_file.pdf
```

### Delete
```bash
curl -X DELETE http://localhost:8080/api/files/1716192345678_file.pdf
```

### Get Presigned URL
```bash
curl -X GET http://localhost:8080/api/files/presigned-url/1716192345678_file.pdf
```

### Check Existence
```bash
curl -X GET http://localhost:8080/api/files/exists/1716192345678_file.pdf
```

### Health Check
```bash
curl -X GET http://localhost:8080/api/files/health
```

## Dependencies

Added for Swagger/OpenAPI support:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

## Customization

To customize Swagger documentation, edit `SwaggerConfig.java`:

```java
@OpenAPIDefinition(
    info = @Info(
        title = "File Storage API",
        version = "1.0.0",
        description = "MinIO-based File Storage API",
        contact = @Contact(...),
        license = @License(...)
    ),
    servers = { ... }
)
```

## Best Practices

1. **Document Complex Operations** - Add descriptions for non-obvious endpoints
2. **Use Examples** - Include realistic example values in schemas
3. **Define Error Responses** - Document all possible error codes
4. **Keep Descriptions Brief** - 1-2 sentences is usually enough
5. **Use Tags** - Organize endpoints by functionality
6. **Version Your API** - Include version in documentation

## References

- [Springdoc-OpenAPI Documentation](https://springdoc.org/)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)

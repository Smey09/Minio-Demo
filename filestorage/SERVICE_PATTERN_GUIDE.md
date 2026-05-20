# Service Interface Implementation Guide

This project follows the **Service Interface Pattern** for better code organization and maintainability.

## Architecture Overview

```
┌─────────────────────────────────────────┐
│          FileController                 │
│   (REST Endpoints)                      │
└────────────────┬────────────────────────┘
                 │ (depends on interface)
                 ▼
┌─────────────────────────────────────────┐
│       FileStorageService                │
│   (Service Interface)                   │
│                                         │
│   + uploadFile()                        │
│   + downloadFile()                      │
│   + deleteFile()                        │
│   + getFileInfo()                       │
│   + getPresignedUrl()                   │
│   + fileExists()                        │
└────────────────┬────────────────────────┘
                 │ (implements)
                 ▼
┌─────────────────────────────────────────┐
│       MinioService                      │
│   (Implementation)                      │
│                                         │
│   MinIO-specific logic                  │
└────────────────┬────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────┐
│       MinioClient                       │
│   (MinIO Java SDK)                      │
└─────────────────────────────────────────┘
```

## Implementation Details

### 1. Service Interface

**File**: `service/FileStorageService.java`

```java
public interface FileStorageService {
    void ensureBucketExists();
    String uploadFile(MultipartFile file) throws IOException;
    InputStream downloadFile(String fileName);
    void deleteFile(String fileName);
    StatObjectResponse getFileInfo(String fileName);
    String getPresignedUrl(String fileName, int expiryInSeconds);
    boolean fileExists(String fileName);
}
```

**Purpose**:
- Defines contract for file storage operations
- Technology-agnostic interface
- Clear and explicit method signatures

### 2. Service Implementation

**File**: `service/MinioService.java`

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Override
    public void ensureBucketExists() {
        // MinIO-specific implementation
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        // MinIO upload logic
    }
    
    // ... other methods
}
```

**Purpose**:
- Implements specific file storage provider (MinIO)
- Contains all MinIO-specific logic
- Handles configuration and dependencies
- Provides error handling and logging

### 3. Controller Usage

**File**: `controller/FileController.java`

```java
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService fileStorageService;  // Interface type

    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile file) {
        String fileName = fileStorageService.uploadFile(file);
        // ...
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String fileName) {
        InputStream fileStream = fileStorageService.downloadFile(fileName);
        // ...
    }
    
    // ... other endpoints
}
```

**Purpose**:
- Depends on interface, not implementation
- REST endpoint definitions
- Request/response handling
- Swagger documentation

## Benefits

### 1. Loose Coupling
- Controller doesn't know about MinIO
- Easy to swap implementations
- Changes to MinIO don't affect controller

### 2. Testability
```java
@RunWith(MockitoRunner.class)
public class FileControllerTest {
    
    @Mock
    private FileStorageService fileStorageService;  // Mock the interface
    
    @InjectMocks
    private FileController fileController;
    
    @Test
    public void testUpload() {
        // Can mock any implementation
        when(fileStorageService.uploadFile(any())).thenReturn("file.txt");
        // Test controller logic
    }
}
```

### 3. Flexibility
Can easily switch implementations:
```java
// Current: MinIO
@Service
public class MinioService implements FileStorageService { }

// Alternative: AWS S3
@Service
public class S3Service implements FileStorageService { }

// Alternative: Local File System
@Service
public class LocalFileService implements FileStorageService { }
```

### 4. Clean Code
- Clear separation of concerns
- Single responsibility principle
- Interface segregation principle
- Dependency inversion principle (SOLID)

### 5. Maintainability
- Changes to one implementation don't affect others
- Easy to understand the contract
- Framework for adding new providers
- Explicit method documentation

## Configuration Pattern

The interface pattern works seamlessly with Spring's configuration:

```java
@Configuration
public class AppConfig {
    
    @Bean
    public MinioClient minioClient(
            @Value("${minio.url}") String url,
            @Value("${minio.access-key}") String accessKey,
            @Value("${minio.secret-key}") String secretKey) {
        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
    
    // FileStorageService is auto-wired through @Service annotation
}
```

## Example: Adding AWS S3 Support

To add S3 support alongside MinIO:

```java
// S3 Implementation
@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
public class S3Service implements FileStorageService {
    
    private final S3Client s3Client;
    
    @Override
    public String uploadFile(MultipartFile file) {
        // S3 implementation
    }
    
    // ... implement other methods
}
```

Controller remains unchanged! The interface allows for multiple implementations.

## Best Practices

### 1. Keep Interface Simple
- Only include methods that are truly needed
- Avoid "god interfaces"
- Consider the most common use cases

### 2. Document the Contract
```java
/**
 * Upload a file to storage
 * 
 * @param file MultipartFile to upload
 * @return fileName of uploaded file
 * @throws IOException if upload fails
 * @throws IllegalArgumentException if file is empty
 */
String uploadFile(MultipartFile file) throws IOException;
```

### 3. Handle Errors Consistently
- Define custom exceptions for different error scenarios
- Provide meaningful error messages
- Log appropriately

### 4. Use Type Safety
```java
// Good: Specific types
void deleteFile(String fileName);

// Avoid: Generic types
void delete(Object anything);
```

### 5. Version Your Interface
When making breaking changes, consider creating a new version:
```java
public interface FileStorageService { }
public interface FileStorageServiceV2 { }
```

## Extending the Interface

To add new functionality:

```java
public interface FileStorageService {
    // ... existing methods
    
    // New method
    List<String> listFiles(String prefix) throws IOException;
}
```

Update implementations:
```java
@Service
public class MinioService implements FileStorageService {
    
    @Override
    public List<String> listFiles(String prefix) throws IOException {
        // MinIO implementation
    }
}
```

Controller code can now use the new method without changes to its core logic.

## Testing Strategy

### Unit Tests
```java
@Test
public void testUploadFile() {
    // Arrange
    when(fileStorageService.uploadFile(any())).thenReturn("test.txt");
    
    // Act
    ResponseEntity<FileUploadResponse> response = controller.uploadFile(mockFile);
    
    // Assert
    assertEquals(HttpStatus.OK, response.getStatusCode());
}
```

### Integration Tests
```java
@SpringBootTest
public class FileIntegrationTest {
    
    @Autowired
    private FileController fileController;
    
    @Test
    public void testEndToEnd() {
        // Tests with real MinioService
    }
}
```

## Migration Path

If starting without the interface pattern and wanting to add it:

1. Create the interface
2. Have MinioService implement the interface
3. Update controller to use interface type
4. Refactor incrementally
5. Add tests
6. Consider extracting additional implementations

## References

- [Spring Service Layer](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-introduction)
- [Dependency Injection](https://en.wikipedia.org/wiki/Dependency_injection)
- [SOLID Principles](https://en.wikipedia.org/wiki/SOLID)
- [Design Patterns](https://refactoring.guru/design-patterns)

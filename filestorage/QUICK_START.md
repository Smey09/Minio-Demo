# Quick Start Guide

Get up and running with the MinIO File Storage Application with Swagger UI.

## Prerequisites

- Java 21 or higher
- Maven 3.8+
- Docker (recommended for MinIO)
- Git

## Step 1: Start MinIO Server

### Using Docker (Recommended)

```bash
docker run -p 9000:9000 -p 9001:9001 \
  -e MINIO_ROOT_USER=admin \
  -e MINIO_ROOT_PASSWORD=password123 \
  minio/minio server /data --console-address ":9001"
```

Or using docker-compose:
```bash
docker-compose up -d
```

### Verify MinIO is Running

- **API**: http://localhost:9000
- **Console**: http://localhost:9001
- **Username**: admin
- **Password**: password123

## Step 2: Build the Application

```bash
cd /path/to/filestorage
./mvnw clean package
```

Or just compile:
```bash
./mvnw clean compile
```

## Step 3: Run the Application

```bash
./mvnw spring-boot:run
```

Or run the JAR:
```bash
java -jar target/filestorage-0.0.1-SNAPSHOT.jar
```

## Step 4: Access Swagger UI

Once the application starts, open your browser and navigate to:

```
http://localhost:8080/swagger-ui.html
```

You should see the interactive API documentation with all endpoints listed.

## Step 5: Test an Endpoint

### Upload a File
1. In Swagger UI, find **POST /api/files/upload**
2. Click "Try it out"
3. Click "Choose File" and select any file
4. Click "Execute"
5. View the response with the uploaded file name

### Download the File
1. Find **GET /api/files/download/{fileName}**
2. Click "Try it out"
3. Paste the file name from the upload response
4. Click "Execute"
5. The file will be downloaded

### Check File Info
1. Find **GET /api/files/info/{fileName}**
2. Click "Try it out"
3. Enter the file name
4. Click "Execute"
5. View file metadata (size, last modified, etag)

## Configuration

Edit `src/main/resources/application.properties` to change settings:

```properties
# Server port
server.port=8080

# MinIO connection
minio.url=http://localhost:9000
minio.access-key=admin
minio.secret-key=password123
minio.bucket-name=uploads

# File size limits
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# Logging level
logging.level.com.smeydev.filestorage=DEBUG
```

## Project Structure

```
filestorage/
├── src/main/java/com/smeydev/filestorage/
│   ├── config/
│   │   ├── MinioConfig.java          # MinIO client configuration
│   │   └── SwaggerConfig.java        # Swagger/OpenAPI configuration
│   ├── controller/
│   │   └── FileController.java       # REST endpoints
│   ├── dto/
│   │   ├── FileUploadResponse.java   # Upload response DTO
│   │   └── FileInfo.java             # File info DTO
│   ├── exception/
│   │   └── GlobalExceptionHandler.java
│   ├── service/
│   │   ├── FileStorageService.java   # Service interface
│   │   └── MinioService.java         # MinIO implementation
│   └── FilestorageApplication.java   # Main application class
├── src/main/resources/
│   ├── application.properties        # Application configuration
│   └── application-test.properties   # Test configuration
├── pom.xml                            # Maven configuration
├── docker-compose.yml                 # Docker compose for MinIO
├── MINIO_SETUP.md                     # MinIO setup guide
├── SWAGGER_GUIDE.md                   # Swagger documentation guide
├── SERVICE_PATTERN_GUIDE.md           # Service interface pattern guide
└── README.md                          # Project README
```

## Available Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/files/upload` | Upload a file |
| GET | `/api/files/download/{fileName}` | Download a file |
| GET | `/api/files/info/{fileName}` | Get file information |
| DELETE | `/api/files/{fileName}` | Delete a file |
| GET | `/api/files/presigned-url/{fileName}` | Get presigned URL |
| GET | `/api/files/exists/{fileName}` | Check if file exists |
| GET | `/api/files/health` | Health check |

## Testing with cURL

### Upload
```bash
curl -X POST http://localhost:8080/api/files/upload \
  -F "file=@/path/to/file.txt"
```

### Download
```bash
curl -X GET http://localhost:8080/api/files/download/1716192345678_file.txt \
  -o downloaded_file.txt
```

### Get Info
```bash
curl -X GET http://localhost:8080/api/files/info/1716192345678_file.txt
```

### Delete
```bash
curl -X DELETE http://localhost:8080/api/files/1716192345678_file.txt
```

### Health Check
```bash
curl -X GET http://localhost:8080/api/files/health
```

## Environment Variables (Optional)

For production, use environment variables instead of properties file:

```bash
export MINIO_URL=https://minio.example.com
export MINIO_ACCESS_KEY=your-access-key
export MINIO_SECRET_KEY=your-secret-key
export MINIO_BUCKET_NAME=your-bucket

./mvnw spring-boot:run
```

Then update application.properties to use:
```properties
minio.url=${MINIO_URL}
minio.access-key=${MINIO_ACCESS_KEY}
minio.secret-key=${MINIO_SECRET_KEY}
minio.bucket-name=${MINIO_BUCKET_NAME}
```

## Troubleshooting

### Issue: Connection refused to MinIO
**Solution**: Ensure MinIO is running on port 9000
```bash
docker ps  # Check if container is running
curl http://localhost:9000  # Test connectivity
```

### Issue: Authentication failed
**Solution**: Verify MinIO credentials in application.properties

### Issue: Bucket not found
**Solution**: The application auto-creates the bucket on first use

### Issue: Port 8080 already in use
**Solution**: Change port in application.properties:
```properties
server.port=8081
```

### Issue: Swagger UI not loading
**Solution**: Verify application started successfully:
```bash
curl http://localhost:8080/api/files/health
```

## Key Features

✅ **Swagger/OpenAPI Documentation** - Interactive API testing
✅ **Service Interface Pattern** - Clean, testable architecture
✅ **MinIO Integration** - Scalable object storage
✅ **Error Handling** - Global exception handler
✅ **Logging** - Comprehensive application logging
✅ **File Validation** - Size and type validation
✅ **Docker Support** - Easy deployment

## Next Steps

1. **Explore the Swagger UI** - Test all endpoints interactively
2. **Read the guides** - Check SWAGGER_GUIDE.md and SERVICE_PATTERN_GUIDE.md
3. **Add Authentication** - Implement JWT or OAuth2
4. **Write Tests** - Add unit and integration tests
5. **Deploy** - Package as Docker image or deploy to cloud

## Documentation

- [MINIO_SETUP.md](./MINIO_SETUP.md) - Detailed MinIO setup
- [SWAGGER_GUIDE.md](./SWAGGER_GUIDE.md) - Swagger documentation details
- [SERVICE_PATTERN_GUIDE.md](./SERVICE_PATTERN_GUIDE.md) - Service interface pattern

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the relevant guide
3. Check application logs: `tail -f target/logs/app.log`

## License

Apache 2.0

# MyCore Repository

This workspace contains two main components:

- `filestorage/` — a Spring Boot service for MinIO-backed file storage
- `MinIO/` — local MinIO development resources and Docker Compose configuration

## Projects

### filestorage

A Spring Boot file storage service with REST APIs for:

- uploading files
- downloading files
- fetching metadata
- deleting files
- generating presigned URLs
- checking file existence
- health checks

The service uses MinIO object storage and includes Swagger/OpenAPI support.

### MinIO

A local MinIO setup with Docker Compose and sample data storage under `MinIO/data/uploads`.

## Quick Start

### 1. Start MinIO

Use `docker-compose` from the `MinIO/` folder:

```bash
cd MinIO
docker-compose up -d
```

Alternatively, use the `docker-compose.yml` in `filestorage/` if it is configured for the service.

### 2. Build and run the file storage service

```bash
cd filestorage
./mvnw clean package
./mvnw spring-boot:run
```

### 3. Open Swagger UI

When the service is running, open Swagger UI at:

```text
http://localhost:8080/swagger-ui.html
```

## Configuration

The Spring Boot service reads MinIO settings from `filestorage/src/main/resources/application.properties`.

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

## Repository Layout

```
MyCore/
├── filestorage/       # Spring Boot file storage service
│   ├── src/
│   ├── pom.xml
│   ├── mvnw
│   └── README.md
├── MinIO/             # Local MinIO configuration and sample data
│   ├── docker-compose.yml
│   └── data/
└── README.md          # This top-level workspace overview
```

## Notes

- Ensure Java 21+ and Maven 3.8+ are installed before building `filestorage`.
- The MinIO service must be running before starting the Spring Boot application.
- Use the service's Swagger UI or REST endpoints to interact with file storage operations.

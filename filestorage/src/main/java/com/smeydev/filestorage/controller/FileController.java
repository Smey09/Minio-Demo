package com.smeydev.filestorage.controller;

import com.smeydev.filestorage.dto.FileInfo;
import com.smeydev.filestorage.dto.FileUploadResponse;
import com.smeydev.filestorage.service.FileStorageService;
import io.minio.StatObjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "File Storage", description = "APIs for file upload, download, and management with MinIO")
public class FileController {

    private final FileStorageService fileStorageService;

    /**
     * Upload a file to MinIO
     */
    @PostMapping("/upload")
    @Operation(summary = "Upload a file", description = "Upload a file to MinIO storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request or empty file"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FileUploadResponse> uploadFile(
            @Parameter(description = "File to upload", required = true) @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            String fileName = fileStorageService.uploadFile(file);

            FileUploadResponse response = new FileUploadResponse(
                    fileName,
                    "File uploaded successfully",
                    file.getSize(),
                    LocalDateTime.now(),
                    file.getContentType());

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Download a file from MinIO
     */
    @GetMapping("/download/{fileName}")
    @Operation(summary = "Download a file", description = "Download a file from MinIO storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully"),
            @ApiResponse(responseCode = "404", description = "File not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<InputStreamResource> downloadFile(
            @Parameter(description = "Name of file to download", required = true) @PathVariable String fileName) {
        try {
            InputStream fileStream = fileStorageService.downloadFile(fileName);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get file information
     */
    @GetMapping("/info/{fileName}")
    @Operation(summary = "Get file info", description = "Get metadata information about a file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File info retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<FileInfo> getFileInfo(
            @Parameter(description = "Name of file", required = true) @PathVariable String fileName) {
        try {
            StatObjectResponse stat = fileStorageService.getFileInfo(fileName);

            FileInfo fileInfo = new FileInfo(
                    fileName,
                    stat.size(),
                    stat.lastModified(),
                    stat.etag(),
                    true);

            return ResponseEntity.ok(fileInfo);
        } catch (Exception e) {
            log.error("Error getting file info: {}", e.getMessage());
            FileInfo fileInfo = new FileInfo(fileName, 0, null, null, false);
            return ResponseEntity.ok(fileInfo);
        }
    }

    /**
     * Delete a file from MinIO
     */
    @DeleteMapping("/{fileName}")
    @Operation(summary = "Delete a file", description = "Delete a file from MinIO storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> deleteFile(
            @Parameter(description = "Name of file to delete", required = true) @PathVariable String fileName) {
        try {
            fileStorageService.deleteFile(fileName);

            Map<String, String> response = new HashMap<>();
            response.put("message", "File deleted successfully");
            response.put("fileName", fileName);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get presigned URL for a file
     */
    @GetMapping("/presigned-url/{fileName}")
    @Operation(summary = "Get presigned URL", description = "Generate a presigned URL for temporary file access")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL generated successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @Parameter(description = "Name of file", required = true) @PathVariable String fileName,
            @Parameter(description = "Expiration time in seconds", example = "3600") @RequestParam(defaultValue = "3600") int expiryInSeconds) {
        try {
            String presignedUrl = fileStorageService.getPresignedUrl(fileName, expiryInSeconds);

            Map<String, String> response = new HashMap<>();
            response.put("presignedUrl", presignedUrl);
            response.put("fileName", fileName);
            response.put("expiryInSeconds", String.valueOf(expiryInSeconds));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Check if file exists
     */
    @GetMapping("/exists/{fileName}")
    @Operation(summary = "Check file existence", description = "Check if a file exists in storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File existence checked"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Map<String, Object>> fileExists(
            @Parameter(description = "Name of file to check", required = true) @PathVariable String fileName) {
        try {
            boolean exists = fileStorageService.fileExists(fileName);

            Map<String, Object> response = new HashMap<>();
            response.put("fileName", fileName);
            response.put("exists", exists);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error checking file existence: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if the file storage service is running")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Service is healthy")
    })
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "MinIO File Storage Service is running");
        return ResponseEntity.ok(response);
    }
}

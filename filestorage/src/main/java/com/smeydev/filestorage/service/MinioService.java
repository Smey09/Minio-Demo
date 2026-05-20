package com.smeydev.filestorage.service;

import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioService implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    /**
     * Check if bucket exists, create if not
     */
    public void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build());
                log.info("Bucket '{}' created successfully", bucketName);
            }
        } catch (Exception e) {
            log.error("Error ensuring bucket exists: {}", e.getMessage());
            throw new RuntimeException("Error ensuring bucket exists", e);
        }
    }

    /**
     * Upload file to MinIO
     */
    public String uploadFile(MultipartFile file) throws IOException {
        try {
            ensureBucketExists();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            log.info("File '{}' uploaded successfully", fileName);
            return fileName;
        } catch (Exception e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new RuntimeException("Error uploading file", e);
        }
    }

    /**
     * Download file from MinIO
     */
    public InputStream downloadFile(String fileName) {
        try {
            ensureBucketExists();

            log.info("Downloading file: {}", fileName);
            return minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("Error downloading file: {}", e.getMessage());
            throw new RuntimeException("Error downloading file", e);
        }
    }

    /**
     * Delete file from MinIO
     */
    public void deleteFile(String fileName) {
        try {
            ensureBucketExists();

            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());

            log.info("File '{}' deleted successfully", fileName);
        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new RuntimeException("Error deleting file", e);
        }
    }

    /**
     * Get file info
     */
    public StatObjectResponse getFileInfo(String fileName) {
        try {
            ensureBucketExists();

            log.info("Getting file info for: {}", fileName);
            return minioClient.statObject(StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("Error getting file info: {}", e.getMessage());
            throw new RuntimeException("Error getting file info", e);
        }
    }

    /**
     * Get presigned URL for file (valid for 7 days by default)
     */
    public String getPresignedUrl(String fileName, int expiryInSeconds) {
        try {
            ensureBucketExists();

            log.info("Generating presigned URL for: {}", fileName);
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .method(Method.GET)
                    .bucket(bucketName)
                    .object(fileName)
                    .build());
        } catch (Exception e) {
            log.error("Error generating presigned URL: {}", e.getMessage());
            throw new RuntimeException("Error generating presigned URL", e);
        }
    }

    /**
     * Check if file exists
     */
    public boolean fileExists(String fileName) {
        try {
            ensureBucketExists();
            getFileInfo(fileName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

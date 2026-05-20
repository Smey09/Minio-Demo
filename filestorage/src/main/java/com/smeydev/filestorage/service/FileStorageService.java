package com.smeydev.filestorage.service;

import io.minio.StatObjectResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * File Storage Service Interface
 * Defines contract for file storage operations (upload, download, delete, etc.)
 */
public interface FileStorageService {

    /**
     * Ensure the default bucket exists, create if not
     */
    void ensureBucketExists();

    /**
     * Upload a file to storage
     *
     * @param file MultipartFile to upload
     * @return fileName of uploaded file
     * @throws IOException if upload fails
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * Download a file from storage
     *
     * @param fileName name of file to download
     * @return InputStream of file contents
     */
    InputStream downloadFile(String fileName);

    /**
     * Delete a file from storage
     *
     * @param fileName name of file to delete
     */
    void deleteFile(String fileName);

    /**
     * Get file metadata information
     *
     * @param fileName name of file
     * @return StatObjectResponse containing file info
     */
    StatObjectResponse getFileInfo(String fileName);

    /**
     * Generate presigned URL for temporary file access
     *
     * @param fileName        name of file
     * @param expiryInSeconds expiration time in seconds
     * @return presigned URL string
     */
    String getPresignedUrl(String fileName, int expiryInSeconds);

    /**
     * Check if file exists in storage
     *
     * @param fileName name of file to check
     * @return true if file exists, false otherwise
     */
    boolean fileExists(String fileName);
}

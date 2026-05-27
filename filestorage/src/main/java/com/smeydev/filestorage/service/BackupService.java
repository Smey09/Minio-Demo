
package com.smeydev.filestorage.service;

import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.messages.Item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${backup.local-path}")
    private String backupPath;

    /**
     * Create backup directory
     */
    public void initializeBackupFolder() throws Exception {

        Path backupDir = Paths.get(backupPath);

        if (!Files.exists(backupDir)) {

            Files.createDirectories(backupDir);

            log.info("Backup folder created at: {}", backupDir);
        }
    }

    /**
     * Backup all files from MinIO
     */
    public void backupAllFiles() throws Exception {

        log.info("========== START BACKUP ==========");

        initializeBackupFolder();

        Iterable<Result<Item>> results =
                minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .recursive(true)
                                .build()
                );

        for (Result<Item> result : results) {

            Item item = result.get();

            String objectName = item.objectName();

            log.info("Processing: {}", objectName);

            /**
             * Get object metadata
             */
            StatObjectResponse stat =
                    minioClient.statObject(
                            StatObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .build()
                    );

            String etag = stat.etag();

            Path localFile =
                    Paths.get(backupPath, objectName);

            Path etagFile =
                    Paths.get(backupPath, objectName + ".etag");

            /**
             * Skip unchanged file
             */
            if (Files.exists(etagFile)) {

                String oldEtag =
                        Files.readString(etagFile);

                if (oldEtag.equals(etag)) {

                    log.info("Skipped unchanged file: {}", objectName);

                    continue;
                }
            }

            /**
             * Create folder if needed
             */
            if (localFile.getParent() != null) {

                Files.createDirectories(localFile.getParent());
            }

            /**
             * Download object
             */
            try (
                    InputStream inputStream =
                            minioClient.getObject(
                                    GetObjectArgs.builder()
                                            .bucket(bucketName)
                                            .object(objectName)
                                            .build()
                            )
            ) {

                Files.copy(
                        inputStream,
                        localFile,
                        StandardCopyOption.REPLACE_EXISTING
                );

                /**
                 * Save ETag
                 */
                Files.writeString(etagFile, etag);

                log.info("Backed up successfully: {}", objectName);
            }
        }

        log.info("========== END BACKUP ==========");
    }
}


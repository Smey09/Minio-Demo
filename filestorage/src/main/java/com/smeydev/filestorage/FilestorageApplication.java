package com.smeydev.filestorage;

import com.smeydev.filestorage.service.BackupService;
import com.smeydev.filestorage.service.MinioService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@SpringBootApplication
@EnableScheduling
@EnableAsync
@RequiredArgsConstructor
public class FilestorageApplication implements CommandLineRunner {

	private final MinioService minioService;
	private final BackupService backupService;

	public static void main(String[] args) {
		SpringApplication.run(FilestorageApplication.class, args);
	}

	@Override
	public void run(String... args) {

		log.info("===========================================");
		log.info("🚀 MinIO File Storage Application Starting...");
		log.info("===========================================");

		try {

			// 1. Ensure MinIO bucket exists
			minioService.ensureBucketExists();
			log.info("🟢 MinIO bucket is ready");

			// 2. Ensure backup folder exists
			backupService.initializeBackupFolder();
			log.info("🟢 Backup folder is ready");

			// 3. Optional: Run backup on startup (safe check)
			boolean runStartupBackup = false;

			if (runStartupBackup) {

				log.info("🟡 Running startup backup...");
				backupService.backupAllFiles();
			}

		} catch (Exception e) {

			log.error("🔴 Application initialization failed: {}", e.getMessage(), e);

		}

		log.info("===========================================");
		log.info("✅ Application started successfully!");
		log.info("📡 API: http://localhost:8080/api/files");
		log.info("❤️ Health: http://localhost:8080/api/files/health");
		log.info("💾 Backup: http://localhost:8080/api/backup/run");
		log.info("===========================================");
	}
}
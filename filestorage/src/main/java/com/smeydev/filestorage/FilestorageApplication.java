package com.smeydev.filestorage;

import com.smeydev.filestorage.service.MinioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
@RequiredArgsConstructor
public class FilestorageApplication implements CommandLineRunner {

	private final MinioService minioService;

	public static void main(String[] args) {
		SpringApplication.run(FilestorageApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("===========================================");
		log.info("MinIO File Storage Application Starting...");
		log.info("===========================================");

		try {
			minioService.ensureBucketExists();
			log.info("MinIO bucket is ready for use");
		} catch (Exception e) {
			log.error("Failed to initialize MinIO bucket: {}", e.getMessage());
			log.warn("Please ensure MinIO server is running and accessible");
		}

		log.info("===========================================");
		log.info("Application started successfully!");
		log.info("Access API at: http://localhost:8080/api/files");
		log.info("Health check: http://localhost:8080/api/files/health");
		log.info("===========================================");
	}
}

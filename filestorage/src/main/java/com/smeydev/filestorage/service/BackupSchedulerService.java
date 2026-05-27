package com.smeydev.filestorage.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupSchedulerService {

    private final BackupService backupService;

    /**
     * Run backup automatically every day at 1 AM
     */
    @Scheduled(cron = "0 0 1 * * *")
    public void runDailyBackup() {

        log.info("🟡 Scheduled Backup Triggered (01:00 AM)");

        runBackupInBackground();
    }

    /**
     * Run backup every 6 hours (optional)
     */
    @Scheduled(fixedDelay = 1000 * 60 * 60 * 6)
    public void runEvery6HoursBackup() {

        log.info("🟡 Scheduled Backup Triggered (Every 6 hours)");

        runBackupInBackground();
    }

    /**
     * Run backup in background thread
     */
    @Async
    public void runBackupInBackground() {

        try {

            log.info("🔵 Backup started in background thread...");

            backupService.backupAllFiles();

            log.info("🟢 Backup completed successfully (bg thread)");

        } catch (Exception e) {

            log.error("🔴 Backup failed: {}", e.getMessage(), e);
        }
    }
}


package com.smeydev.filestorage.controller;

import com.smeydev.filestorage.service.BackupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/backup")
@RequiredArgsConstructor
public class BackupController {
    private final BackupService backupService;

    @GetMapping("/run")
    public String runBackup() throws Exception {
        backupService.backupAllFiles();
        return "Backup completed successfully";
    }
}
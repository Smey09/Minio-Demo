package com.smeydev.filestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for file upload")
public class FileUploadResponse {

    @Schema(description = "Uploaded file name", example = "1716192345678_document.pdf")
    private String fileName;

    @Schema(description = "Upload status message", example = "File uploaded successfully")
    private String message;

    @Schema(description = "File size in bytes", example = "102400")
    private long fileSize;

    @Schema(description = "Upload timestamp")
    private LocalDateTime uploadedAt;

    @Schema(description = "File content type", example = "application/pdf")
    private String contentType;
}

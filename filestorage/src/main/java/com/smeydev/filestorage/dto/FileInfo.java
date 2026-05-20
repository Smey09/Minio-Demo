package com.smeydev.filestorage.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "File metadata information")
public class FileInfo {

    @Schema(description = "File name", example = "1716192345678_document.pdf")
    private String fileName;

    @Schema(description = "File size in bytes", example = "102400")
    private long fileSize;

    @Schema(description = "Last modification timestamp")
    private ZonedDateTime lastModified;

    @Schema(description = "File entity tag", example = "abc123def456xyz")
    private String etag;

    @Schema(description = "Whether file exists", example = "true")
    private boolean exists;
}

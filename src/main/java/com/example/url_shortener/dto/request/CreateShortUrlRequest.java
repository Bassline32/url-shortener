package com.example.url_shortener.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Schema(example = """
{
  "originalUrl": "https://google.com",
  "customCode": null,
  "expiresAt": null,
  "folderId": null,
  "tags": ["test"]
}
""")
public record CreateShortUrlRequest(
        @NotBlank
        String originalUrl,

        String customCode,
        LocalDateTime expiresAt,
        Long folderId,
        List<String> tags
) {}

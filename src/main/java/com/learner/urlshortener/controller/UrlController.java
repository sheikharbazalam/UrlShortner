package com.learner.urlshortener.controller;

import com.learner.urlshortener.model.Url;
import com.learner.urlshortener.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UrlController {

    private final UrlService urlService;

    @Value("${app.base-url}")
    private String appBaseUrl;

    // ─── POST /api/shorten ────────────────────────────────────────
    @PostMapping("/shorten")
    public ResponseEntity<Map<String, String>> shortenUrl(
            @RequestBody Map<String, String> request) {

        String originalUrl = request.get("url");

        if (originalUrl == null || originalUrl.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "URL cannot be empty"));
        }

        Url savedUrl = urlService.shortenUrl(originalUrl);

        return ResponseEntity.ok(Map.of(
                "shortCode", savedUrl.getShortCode(),
                "shortUrl", appBaseUrl + "/r/" + savedUrl.getShortCode(),
                "originalUrl", savedUrl.getOriginalUrl()
        ));
    }

    // ─── GET /r/{shortCode} ───────────────────────────────────────
    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        Optional<String> originalUrl = urlService.getOriginalUrl(shortCode);

        if (originalUrl.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(Objects.requireNonNull(URI.create(Objects.requireNonNull(originalUrl.get()))))
                    .build();
        }

        return ResponseEntity.notFound().build();
    }

    // ─── GET /api/stats/{shortCode} ──────────────────────────────
    @GetMapping("/stats/{shortCode}")
    public ResponseEntity<Map<String, Object>> getStats(
            @PathVariable String shortCode) {

        return urlService.getOriginalUrl(shortCode)
                .map(url -> ResponseEntity.ok(Map.<String, Object>of(
                        "shortCode", shortCode,
                        "originalUrl", url
                )))
                .orElse(ResponseEntity.notFound().build());
    }
}
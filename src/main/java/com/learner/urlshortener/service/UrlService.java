package com.learner.urlshortener.service;

import com.learner.urlshortener.model.Url;
import com.learner.urlshortener.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;
    private static final long CACHE_TTL_HOURS = 24;

    // ─── SHORTEN A URL ───────────────────────────────────────────
    public Url shortenUrl(String originalUrl) {

        // 1. Generate a unique short code
        String shortCode = generateUniqueShortCode();

        // 2. Build the Url entity and save to PostgreSQL
        Url url = new Url();
        url.setOriginalUrl(originalUrl);
        url.setShortCode(shortCode);
        url.setCreatedAt(LocalDateTime.now());
        url.setClickCount(0L);

        Url savedUrl = urlRepository.save(url);

        // 3. Cache it in Redis for 24 hours
        redisTemplate.opsForValue().set(
            "url:" + shortCode,
            Objects.requireNonNull(originalUrl),
            CACHE_TTL_HOURS,
            TimeUnit.HOURS
        );

        return savedUrl;
    }

    // ─── GET ORIGINAL URL (Redis first, then DB) ─────────────────
    public Optional<String> getOriginalUrl(String shortCode) {

        // 1. Check Redis cache first
        String cachedUrl = redisTemplate.opsForValue().get("url:" + shortCode);

        if (cachedUrl != null) {
            System.out.println("✅ Cache HIT for: " + shortCode);
            incrementClickCount(shortCode);
            return Optional.of(cachedUrl);
        }

        // 2. Cache MISS — go to PostgreSQL
        System.out.println("Cache MISS for: " + shortCode);
        Optional<Url> urlEntity = urlRepository.findByShortCode(shortCode);

        if (urlEntity.isPresent()) {
            String originalUrlValue = urlEntity.get().getOriginalUrl();
            if (originalUrlValue != null) {
                // Store back in Redis for next time
                redisTemplate.opsForValue().set(
                    "url:" + shortCode,
                    originalUrlValue,
                    CACHE_TTL_HOURS,
                    TimeUnit.HOURS
                );
                incrementClickCount(shortCode);
                return Optional.of(originalUrlValue);
            }
        }

        return Optional.empty();
    }

    // ─── INCREMENT CLICK COUNT ────────────────────────────────────
    private void incrementClickCount(String shortCode) {
        urlRepository.findByShortCode(shortCode).ifPresent(url -> {
            url.setClickCount(url.getClickCount() + 1);
            urlRepository.save(url);
        });
    }

  
    private String generateUniqueShortCode() {
        Random random = new Random();
        String shortCode;

        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            shortCode = sb.toString();
        } while (urlRepository.existsByShortCode(shortCode));

        return shortCode;
    }
}
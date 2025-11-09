package org.shrt.controller;

import org.shrt.model.UrlMapping;
import org.shrt.repository.UrlMappingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.Random;

@RestController
public class UrlShortenerController {

    private static final Logger LOGGER = LoggerFactory.getLogger(UrlShortenerController.class);

    private final UrlMappingRepository urlMappingRepository;
    private final String baseUrl = "http://sh.rt/";

    public UrlShortenerController(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    /**
     * Creates a short URL for a given long URL. This method is idempotent.
     * <p>
     * It uses a SHA-256 hash of the long URL to ensure that the same long URL always maps to the same short URL.
     * <p>
     * NOTE: In the extremely rare case of a hash collision where two different long URLs produce the same hash,
     * only the first URL to be processed will be stored.
     *
     * @param longUrl The long URL to shorten.
     * @return The shortened URL.
     */
    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody String longUrl) {
        LOGGER.info("Received request to shorten URL: {}", longUrl);
        String longUrlHash = generateHash(longUrl);
        Optional<UrlMapping> existingMapping = urlMappingRepository.findByLongUrlHash(longUrlHash);
        if (existingMapping.isPresent() && existingMapping.get().getLongUrl().equals(longUrl)) {
            String fullShortUrl = baseUrl + existingMapping.get().getShortUrl();
            LOGGER.info("Returning existing short URL: {}", fullShortUrl);
            return fullShortUrl;
        }

        String shortUrl = generateShortUrl();
        // The save operation can throw a DataIntegrityViolationException.
        // This can happen in two cases:
        // 1. A race condition where two requests for the same new URL arrive simultaneously.
        // 2. A hash collision where a new URL has the same hash as an existing, different URL.
        // In either case, we let the exception propagate, resulting in an error response.
        // The client can then retry the request. On retry, case (1) will succeed by finding the existing URL.
        // Case (2) is an accepted limitation of this implementation.
        urlMappingRepository.save(new UrlMapping(shortUrl, longUrl, longUrlHash));
        String fullShortUrl = baseUrl + shortUrl;
        LOGGER.info("Shortened URL created: {}", fullShortUrl);
        return fullShortUrl;
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity<String> getLongUrl(@PathVariable String shortUrl) {
        LOGGER.info("Received request to resolve short URL: {}", shortUrl);
        Optional<UrlMapping> urlMapping = urlMappingRepository.findById(shortUrl);
        if (urlMapping.isPresent()) {
            String longUrl = urlMapping.get().getLongUrl();
            LOGGER.info("Found long URL: {}", longUrl);
            return ResponseEntity.ok(longUrl);
        } else {
            LOGGER.warn("Short URL not found: {}", shortUrl);
            return ResponseEntity.notFound().build();
        }
    }

    private String generateShortUrl() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        // In a real-world scenario, we'd also need to check for collisions on the short URL
        return sb.toString();
    }

    private String generateHash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // This should never happen with a standard algorithm like SHA-256
            throw new RuntimeException("Could not generate hash", e);
        }
    }
}

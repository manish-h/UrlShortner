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

    @PostMapping("/shorten")
    public String shortenUrl(@RequestBody String longUrl) {
        LOGGER.info("Received request to shorten URL: {}", longUrl);
        String shortUrl = generateShortUrl();
        urlMappingRepository.save(new UrlMapping(shortUrl, longUrl));
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
        return sb.toString();
    }
}

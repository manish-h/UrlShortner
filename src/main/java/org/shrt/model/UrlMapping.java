package org.shrt.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UrlMapping {

    @Id
    private String shortUrl;
    private String longUrl;
    @Column(unique = true)
    private String longUrlHash;

    public UrlMapping() {
    }

    public UrlMapping(String shortUrl, String longUrl, String longUrlHash) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
        this.longUrlHash = longUrlHash;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getLongUrlHash() {
        return longUrlHash;
    }

    public void setLongUrlHash(String longUrlHash) {
        this.longUrlHash = longUrlHash;
    }
}

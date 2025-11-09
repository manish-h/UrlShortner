package org.shrt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class UrlMapping {

    @Id
    private String shortUrl;
    private String longUrl;

    public UrlMapping() {
    }

    public UrlMapping(String shortUrl, String longUrl) {
        this.shortUrl = shortUrl;
        this.longUrl = longUrl;
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
}

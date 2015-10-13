package com.example.dto;

import java.util.Set;

public class ResultDTO {
    private String url;
    private Set<String> siteMap;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getSiteMap() {
        return siteMap;
    }

    public void setSiteMap(Set<String> siteMap) {
        this.siteMap = siteMap;
    }
}

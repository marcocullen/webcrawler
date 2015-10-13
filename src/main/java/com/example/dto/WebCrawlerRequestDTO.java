package com.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WebCrawlerRequestDTO {
    String url;

    @JsonProperty(value = "politeness-ms")
    Integer politeness;

    @JsonProperty(value = "domain-rule")
    String domainRule;

    public String getUrl() {
        return url;
    }


    public Integer getPoliteness() {
        return politeness;
    }

    public String getDomainRule() {
        return domainRule;
    }
}

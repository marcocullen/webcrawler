package com.example.service;

import com.example.internal.Status;

import java.util.Set;

public interface WebCrawlerService {
    Status crawl(String url, int politeness, String domainRule) throws Exception;

     Set<String> getResult(String location) throws Exception;

     void removeResult(String location);
}

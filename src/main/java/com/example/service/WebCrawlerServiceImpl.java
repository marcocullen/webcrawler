package com.example.service;

import com.example.internal.Status;
import com.example.thread.PageProcessor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.concurrent.ListenableFuture;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

@Service
public class WebCrawlerServiceImpl implements WebCrawlerService {
    public static final Logger LOGGER = LoggerFactory.getLogger(WebCrawlerServiceImpl.class);

    private final ConcurrentHashMap<String, ListenableFuture<Set<String>>> sitesBeingCrawled = new ConcurrentHashMap<>();

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    synchronized public Status crawl(String url, int politeness, String domainRule) throws Exception {
        boolean alreadyCrawling = sitesBeingCrawled.containsKey(url);
        String base64Url = Base64Utils.encodeToString(url.getBytes());

        if(alreadyCrawling) {
           return new Status(sitesBeingCrawled.get(url).isDone(), base64Url);
        }

        Set<String> visitedPages = new HashSet<>();
        LOGGER.info("Web Crawl: {}", url);

        try {
            /* check the robots.txt and adhere to rules */
            URL originalUrl = new URL(url);
            URL robotsURL = new URL(originalUrl, "/robots.txt");

            /* process disallowed end points here */
            Document document = Jsoup.connect(robotsURL.toString()).get();
            String robotsRules = document.text();
            Set<String> disallowedPaths = getDisallowedPaths(robotsRules);

            /* seed the base page */
            document = Jsoup.connect(originalUrl.toString()).get();
            visitedPages.add(url);

            PageProcessor pageProcessor = new PageProcessor(visitedPages, document, politeness, domainRule, disallowedPaths);
            ListenableFuture<Set<String>> result = threadPoolTaskExecutor.submitListenable(pageProcessor);
            sitesBeingCrawled.put(url, result);
            return new Status(result.isDone(), base64Url);
        } catch (Exception ex) {
            LOGGER.error("There was a service error crawling: {}", url, ex);
            sitesBeingCrawled.remove(url);
            throw ex;
        }
    }

    @Override
    public Set<String> getResult(String location) throws Exception {
        Future<Set<String>> result = sitesBeingCrawled.get(new String(Base64Utils.decodeFromString(location)));

        if(result == null) {
            throw new Exception("Result not found, may have been removed or location is wrong");
        }

        if(result.isDone()) {
            try {
                return result.get();
            } catch (Exception ex) {
                LOGGER.error("Well this is not good... exception getting done result");
                throw ex;
            }
        } else {
            throw new Exception("Result not ready yet");
        }
    }

    @Override
    public void removeResult(String location) {
       sitesBeingCrawled.remove(new String(Base64Utils.decodeFromString(location)));
    }

    public Set<String> getDisallowedPaths(String robotRules) {
        /* parse the disallowed paths and we will avoid these */
        String[] rules = robotRules.split("\\n");
        Set<String> disallowed = new HashSet<>();

        /* parse robots.txt extract not allowed paths, knock off the final / to match page processor final path */
        for(String rule : rules) {
            if(rule.contains("Disallow:") && rule.endsWith("/")) {
                disallowed.add(rule.substring(rule.indexOf("Disallow:") + "Disallow:".length() +1, rule.length() -1));
            }
        }

        return disallowed;
    }
}

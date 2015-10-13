package com.example.thread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Callable;

public class PageProcessor implements Callable<Set<String>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PageProcessor.class);

    private Set<String> visitedPages;
    private Document document;
    private int politeness;
    private String domainRule;
    private Set<String> disallowedPaths;

    public PageProcessor(Set<String> visitedPages, Document document, int politeness, String domainRule, Set<String> disallowedPaths) {
        this.visitedPages = visitedPages;
        this.document = document;
        this.politeness = politeness;
        this.domainRule = domainRule;
        this.disallowedPaths = disallowedPaths;
    }

    @Override
    public Set<String> call() throws Exception {
        LOGGER.info("Page processor called...");
        processPage(document);
        LOGGER.info("Page processing complete...");
        return visitedPages;
    }

    public void processPage(Document document) throws IOException, InterruptedException {
        Elements links = document.select("a[href]");
        String finalPath = "";

        for (Element link : links) {
            String absLink = link.attr("abs:href");

            /* get complete path after the host */
            if(absLink.endsWith("/")) {
                finalPath = absLink.substring(0, absLink.lastIndexOf("/"));
                finalPath = finalPath.substring(finalPath.lastIndexOf("//") +2);
                if(finalPath.contains("/")) {
                    finalPath = finalPath.substring(finalPath.indexOf("/"));
                }
            }

            /* rules to determine it is a link we have not visited before and is allowed */
            if (absLink.contains(domainRule) &&
                    !visitedPages.contains(absLink) &&
                    absLink.endsWith("/")
                    && !disallowedPaths.contains(finalPath))
            {
                visitedPages.add(absLink);
                /*politeness */
                Thread.sleep(politeness);
                LOGGER.info("Calling process page: " + absLink);
                Document documentLink = Jsoup.connect(absLink).get();
                processPage(documentLink);
            }
        }
    }
}

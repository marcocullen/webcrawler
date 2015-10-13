package com.example.controller;

import com.example.dto.ResultDTO;
import com.example.dto.StatusDTO;
import com.example.dto.WebCrawlerRequestDTO;
import com.example.internal.Status;
import com.example.service.WebCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class WebCrawlerResource {
    @Autowired
    WebCrawlerService webCrawlerService;

    public static final Logger LOGGER = LoggerFactory.getLogger(WebCrawlerResource.class);

    @RequestMapping(value = "/crawl", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public StatusDTO crawl(@RequestBody WebCrawlerRequestDTO requestDTO) {
        LOGGER.info("New Crawl controller request...");
        try {
            return convertToDTO(webCrawlerService.crawl(requestDTO.getUrl(), requestDTO.getPoliteness(), requestDTO.getDomainRule()));
        } catch (Exception ex) {
            throw new RestClientException("Error processing web page: " + requestDTO.getUrl(), ex);
        }
    }

    @RequestMapping(value = "/result/{location}", method = RequestMethod.GET, produces = "application/json")
    public ResultDTO getResult(@PathVariable String location) {
        String url = new String(Base64Utils.decodeFromString(location));
        LOGGER.info("Get result for {}", url);

        try {
            Set<String> siteMap = webCrawlerService.getResult(location);
            ResultDTO resultDTO = new ResultDTO();
            resultDTO.setUrl(url);
            resultDTO.setSiteMap(siteMap);
            return resultDTO;
        } catch (Exception ex) {
            throw new RestClientException("Error retrieving result " + location, ex);
        }
    }

    @ResponseStatus(value = HttpStatus.OK)
    @RequestMapping(value = "/result/{location}", method = RequestMethod.DELETE)
    public void removeResult(@PathVariable String location) {
        LOGGER.info("Remove previous result {}", location);
        webCrawlerService.removeResult(location);
    }

    private StatusDTO convertToDTO(Status status) {
        return new StatusDTO(status.isDone(), status.getLocation());
    }
}

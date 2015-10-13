# webcrawler

The applciation can be run from the command line via mvn:spring-boot:run task

this will initialise a rest service on localhost:8080.

A web crawl of a given site can be instantiated by making a POST to :
http://localhost:8080/api/crawl

with the following sample JSON :

{"url":"http://wiprodigital.com/",
 "domain-rule":"wiprodigital.com",
 "politeness-ms": 200}

politeness-ms defines the internval time in ms between following a link....too quick and you will get a 429.
url specifies the base url of the site to be crawled.
domain-rule specifies the url rule for staying within a domain, it must be present in any link for a link to be followed.

The POST will return a status object with the location of where the result can later be retrieved from.
{
    "complete": false,
    "location": "aHR0cDovL3d3dy53aXByb2RpZ2l0YWwuY29tLw=="
}

subsequent posts will not intialise a new crawler, only 1 crawler / site can be active at any time, and thus a repeat status object with the result code will be returned 
and a true or false of if the job is done.

if the job is done, the site map in json can be retrieved from http://localhost:8080/api/result/##locationCode##, as a GET request, any exceptions that interrupted the jobs will also be returned here.

a site can be re-crawled only once its previous result has been removed, a delete request to the result location will remove the entry from memory.

Example GET/DELETE result request http://localhost:8080/api/result/aHR0cDovL3d3dy53aXByb2RpZ2l0YWwuY29tLw==

Example Result:

{
    "url": "http://www.wiprodigital.com/",
    "siteMap": [
        "http://wiprodigital.com/the-digital-organization/",
        "http://wiprodigital.com/category/digital-sports/",
        "http://wiprodigital.com/tag/ba/",
        "http://wiprodigital.com/tag/iot/",
        "http://wiprodigital.com/wipro-digital-to-enhance-digital-transformation-capability-with-designit/",
        "http://wiprodigital.com/service-design-network-design-challenge-2015/",
        .......

Quick note on trade offs and approach.

I've never written a web crawler before so I briefly tried to use a pre-existing library for implementing the core crawler such as webcrawler4j, however after some time i decided i really didn't like the way it managed its threads and its persistence so i decided to role my own simple version.

I started with a simple spring-boot application, as there was a note on production quality and this allows very easily for environment based profiles to be incorperated.

I toyed with the idea of writing a JPA layer to persist the already visited pages but this seemed like a bit overkill given the time line set out, hence i opted for an in memory map of the pages that have been visited by a given crawler.

I wanted things to be asynchronus and multi threaded such that the long running crawl can be kicked off and a location to the result returned.

I opted for a PageProcessor Callable the core of which is to recursively climb through href links according to the rules and building up the sitemap....the sitemap also ensures that we do not end up in an infinite loop between two links pointing circularly to eachothers page.

Hence a call to crawl a website instantiates a new PageProcessor callable onto an executor service and places a future to the result in a map keyed on the starting url, as a given website can only be crawled by 1 crawler at the time.....i noticed when spawning multiple crawlers using JSoup.connect we hit the 429 status code limit, hence i imagine its reusing the same connection and therefore breaking the politeness limit for the server.

The crawler will not follow paths that are marked as Disallowed in the robots.txt file.

To avoid a race condition i put a very course synchronized on the crawl method of the webservice bean, this can probably be done better, this was to avoid the potential of 2 quick subsequent submissions for the same url. [as both have their own instance of the site map, they would run independently...possibly triggering a 429 and then the last to return would overwrite the previous]

For some incredibly annoying reason my newly updated version of intellij refused to accept that my java 8 lambdas were valid even though the languge level is set to 8.... so for example the parsing of the robots.txt string i wanted to do as a map but did it in the traditional loop way. 

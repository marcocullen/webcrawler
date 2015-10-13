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

if the job is done, the site map in json can be retrieved from http://localhost:8080/api/result/<location code>, as a GET request, any exceptions that interrupted the jobs will also be returned here.

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


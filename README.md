# crawl
A simple web crawler using scalaz.

## usage in the console
The base URL of the website needs to be provided as a command line parameter or in the console as shown below:
```
scala> ausmarton.crawl.Crawler.main(Array("http://www.wiprodigital.com"))
```

## running tests
```
sbt test
```
will run the tests against a stubbed server (using Wiremock)

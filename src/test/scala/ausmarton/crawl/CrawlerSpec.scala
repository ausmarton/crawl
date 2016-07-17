package ausmarton.crawl

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.{aResponse, configureFor, stubFor, urlEqualTo}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

import scalaz.Scalaz._

class CrawlerSpec
  extends WordSpecLike with Matchers with BeforeAndAfterAll {

  val wireMockServer = new WireMockServer(8080)

  override def beforeAll: Unit = {
    wireMockServer.start()
    configureFor("localhost",8080)

    val homeHtml = "<html><body>" +
      "<a href='/a'>A</a>" +
      "<a href='/b'>B</a>" +
      "<a href='/c'>C</a>" +
      "<a href='http://www.google.com/' title='Google'>" +
      "</body></html>"
    val childPageHtml = "<html><body>" +
      "<a href='/a'>A</a>" +
      "<a href='/b'>B</a>" +
      "<a href='/c'>C</a>" +
      "<a href='/d'>D</a>" +
      "<a href='http://www.google.com/' title='Google'>" +
      "</body></html>"

    def response = (h: String) => aResponse()
      .withHeader("Content-Type","text/html")
      .withBody(h)
      .withStatus(200)

    stubFor(WireMock.get(urlEqualTo("/"))
      .willReturn(response(homeHtml)))
    stubFor(WireMock.get(urlEqualTo("/a"))
      .willReturn(response(homeHtml)))
    stubFor(WireMock.get(urlEqualTo("/b"))
      .willReturn(response(homeHtml)))
    stubFor(WireMock.get(urlEqualTo("/c"))
      .willReturn(response(childPageHtml)))
    stubFor(WireMock.get(urlEqualTo("/d"))
      .willReturn(response(homeHtml)))
  }

  override def afterAll {
    wireMockServer.stop()
  }

  "Crawler" should {
    "Respond with links" in {
      val expectedSiteMap = Link("http://localhost:8080","root",visited = true).node(
        Link("http://localhost:8080/a","A",visited = true).leaf,
        Link("http://localhost:8080/b","B",visited = true).leaf,
        Link("http://localhost:8080/c","C",visited = true).node(
          Link("http://localhost:8080/d","D",visited = true).leaf
        )
      ).flatten
      Crawler.start("http://localhost:8080").flatten shouldBe expectedSiteMap
    }
  }

}

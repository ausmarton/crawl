package ausmarton.crawl

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class CrawlerSpec
  extends WordSpecLike with Matchers with BeforeAndAfterAll {

  val wireMockServer = new WireMockServer(8080)

  override def beforeAll: Unit = {
    wireMockServer.start()
    configureFor("localhost",8080)

    val homeHtml = "<html><body>" +
      "<a href='/a' title='A'>" +
      "<a href='/b' title='B'>" +
      "<a href='/c' title='C'>" +
      "<a href='http://www.google.com/' title='Google'>" +
      "</body></html>"
    val childPageHtml = "<html><body>" +
      "<a href='/a' title='A'>" +
      "<a href='/b' title='B'>" +
      "<a href='/c' title='C'>" +
      "<a href='/d' title='D'>" +
      "<a href='http://www.google.com/' title='Google'>" +
      "</body></html>"

    def response = (h: String) => aResponse()
      .withHeader("Content-Type","text/html")
      .withBody(h)
      .withStatus(200)

    stubFor(get(urlEqualTo("/"))
      .willReturn(response(homeHtml)))
    stubFor(get(urlEqualTo("/a"))
      .willReturn(response(homeHtml)))
    stubFor(get(urlEqualTo("/b"))
      .willReturn(response(homeHtml)))
    stubFor(get(urlEqualTo("/c"))
      .willReturn(response(childPageHtml)))
    stubFor(get(urlEqualTo("/d"))
      .willReturn(response(homeHtml)))
  }

  override def afterAll {
    wireMockServer.stop()
  }

  "Crawler" should {
    "Respond with links" in {
      Crawler.start("http://localhost:8080/") shouldBe List("/a", "/b", "/c", "http://www.google.com/")
    }
  }

}

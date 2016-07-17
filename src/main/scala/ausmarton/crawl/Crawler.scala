package ausmarton.crawl

import org.jsoup.Jsoup

import scala.collection.JavaConverters._

object Crawler extends App {

  override def main(args: Array[String]): Unit = {
    args.headOption.map(start)
  }

  def start(urlSpec: String) = {
    val (internalLinks,externalLinks) = follow(urlSpec).partition(_.startsWith("/"))
    internalLinks ++ externalLinks
  }

  def follow(url: String) = Jsoup.connect(url).execute().parse().getElementsByTag("a").asScala.map(e => e.attr("href")).toList
}

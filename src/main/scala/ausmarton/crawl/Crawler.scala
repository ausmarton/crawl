package ausmarton.crawl

import java.net.URL

import org.jsoup.Jsoup
import org.jsoup.nodes.Element

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scala.collection.mutable
import scalaz.{State, Tree}

case class Link(url: String, name: String, visited: Boolean = false)

object Crawler extends App {

  override def main(args: Array[String]): Unit = {
    args.headOption.map(start)
  }

  type SiteMap = List[Link]


  def start(urlSpec: String): SiteMap = {
    crawl(List(Link(urlSpec,"root")),urlSpec)
  }

  @tailrec
  def crawl(siteMap: SiteMap, hostDomain: String): SiteMap = {
    siteMap.filterNot(_.visited).headOption match {
      case Some(link) => {
        val internalLinks = follow(link.url)
          .filterKeys(_.startsWith("/"))
          .map(l => Link(hostDomain+l._1, l._2))
          .filter(l => !siteMap.exists(_.url == l.url))
        val links : List[Link] = siteMap.map({
          case Link(link.url,_,false) => link.copy(visited = true)
          case x:Link => x
        })
        crawl((links ++ internalLinks).distinct, hostDomain)
      }
      case None => siteMap
    }
  }

  def follow(url: String) = Jsoup.connect(url).execute().parse().getElementsByTag("a").asScala.map(e => (e.attr("href"),e.text)).toMap
}

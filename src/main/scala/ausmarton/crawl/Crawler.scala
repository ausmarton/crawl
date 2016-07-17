package ausmarton.crawl


import org.jsoup.Jsoup

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scalaz._
import scalaz.Scalaz._

case class Link(url: String, name: String, visited: Boolean = false) {
  override def toString: String = s"Link=$url :: Name=$name :: visited=$visited"
}

object Crawler extends App {

  override def main(args: Array[String]): Unit = {
    args.headOption.map(start)
  }

  type SiteMap = Tree[Link]

  def start(urlSpec: String): SiteMap = {
    crawl(scalaz.Tree(Link(urlSpec,"root")),urlSpec)
  }

  @tailrec
  def crawl(siteMap: SiteMap, hostDomain: String): SiteMap = {
    siteMap.flatten.filterNot(_.visited).headOption match {
      case Some(link) => {
        val internalLinks = follow(link.url)
          .filterKeys(_.startsWith("/"))
          .map(l => Link(hostDomain+l._1, l._2))
          .filter(l => !siteMap.flatten.exists(_.url == l.url))

        val newTree = Tree.node(link.copy(visited = true),internalLinks.map(l => l.leaf).toStream)

        val newSiteMap = siteMap.flatMap(n => if(n.url == newTree.rootLabel.url) newTree else Tree(n))

        println(newSiteMap.cobind(n => n.getClass.getSimpleName+n.rootLabel.toString).drawTree)

        crawl(newSiteMap, hostDomain)
      }
      case None => siteMap
    }
  }

  def follow(url: String) = Jsoup.connect(url).execute().parse().getElementsByTag("a").asScala.map(e => (e.attr("href"),e.text)).toMap
}

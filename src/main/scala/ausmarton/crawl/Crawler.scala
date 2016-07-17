package ausmarton.crawl


import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import scala.annotation.tailrec
import scala.collection.JavaConverters._
import scalaz._
import scalaz.Scalaz._

case class Link(url: String, name: String, visited: Boolean = false, crawlable: Boolean = true) {
  override def toString: String = s"[$name]($url)"
}

object Crawler extends App {

  override def main(args: Array[String]): Unit = {
    args.headOption.map(start)
  }

  type SiteMap = Tree[Link]

  def start(urlSpec: String): SiteMap = {
    crawl(Tree(Link(urlSpec,"root")),urlSpec)
  }

  @tailrec
  def crawl(siteMap: SiteMap, hostDomain: String): SiteMap = {
    siteMap.flatten.filterNot(l => l.visited || !l.crawlable).headOption match {
      case Some(link) => {

        val (imageLinks,pages) = follow(link.url)
        val (internalPages,externalPages) = pages.partition(_._1.startsWith("/"))

        val links = {
          internalPages.map(l => Link(hostDomain+l._1, l._2)) ++
          (imageLinks ++ externalPages).map(l => Link(l._1,l._2,crawlable = false))
        }.filter(l => !siteMap.flatten.exists(_.url == l.url))

        val newTree = Tree.node(link.copy(visited = true),links.map(l => l.leaf).toStream)

        val newSiteMap = siteMap.flatMap(n => if(n.url == newTree.rootLabel.url) newTree else Tree(n))

        println(newSiteMap.cobind(n => n.rootLabel.toString).drawTree)

        crawl(newSiteMap, hostDomain)
      }
      case None => siteMap
    }
  }

  def follow(url: String) = {
    val parse: Document = Jsoup.connect(url).execute().parse()
    val images: Map[String, String] = parse.getElementsByTag("img").asScala.map(e => (e.attr("src"), e.attr("title"))).toMap
    val links: Map[String, String] = parse.getElementsByTag("a").asScala.map(e => (e.attr("href"), e.text)).toMap
    (images, links)
  }
}

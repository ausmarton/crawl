name := "crawl"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= {
  val scalazVersion: String = "7.1.9"
  Seq(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "org.scalaz" %% "scalaz-effect" % scalazVersion,
    "org.scalaz" %% "scalaz-typelevel" % scalazVersion,
    "org.scalaz" %% "scalaz-scalacheck-binding" % scalazVersion % "test",

    "org.scalatest" %% "scalatest" % "2.2.6" % "test",
    "com.github.tomakehurst" % "wiremock" % "1.33" % "test",
    "org.jsoup" % "jsoup" % "1.8+"
  )
}
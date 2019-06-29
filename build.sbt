organization := "com.kramer425"
homepage := Some(url("https://github.com/kramer425/scrape-async"))
scmInfo := Some(ScmInfo(url("https://github.com/kramer425/scrape-async"),
                            "git@github.com:kramer425/scrape-async.git"))
developers := List(Developer("kramer425",
  "kramer425",
  "",
  url("https://github.com/kramer425")))
licenses += ("MIT", url("https://tldrlegal.com/license/mit-license"))
publishMavenStyle := true

publishTo := Some(
  if (isSnapshot.value)
    Opts.resolver.sonatypeSnapshots
  else
    Opts.resolver.sonatypeStaging
)

name := "scrape-async"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.5.22"
libraryDependencies += "org.seleniumhq.selenium" % "selenium-java" % "4.0.0-alpha-1"


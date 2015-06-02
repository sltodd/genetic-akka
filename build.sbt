name := "genetic-akka"

organization := "uk.co.sltodd"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "org.scalatest" %% "scalatest" % "2.2.5" % "test",
  "org.apache.commons" % "commons-math3" % "3.5",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "com.h2database" % "h2" % "1.3.171",
  "org.hibernate" % "hibernate-core" % "4.2.0.Final"
)

scalacOptions ++= Seq(
  "-feature"
)
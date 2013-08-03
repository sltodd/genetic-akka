name := "genetic-akka"

organization := "uk.co.sltodd"

version := "1.0-SNAPSHOT"

scalaVersion := "2.10.2"

libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "org.scalatest" %% "scalatest" % "1.9.1" % "test",
  "org.apache.commons" % "commons-math3" % "3.2",
  "com.typesafe.akka" %% "akka-actor" % "2.2-M2",
  "com.typesafe.akka" %% "akka-testkit" % "2.2-M2",
  "com.h2database" % "h2" % "1.3.171",
  "org.hibernate" % "hibernate-core" % "4.2.0.Final"
)

scalacOptions ++= Seq(
  "-feature"
)

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := (
  <url>http://github.com/SLTodd/genetic-akka</url>
  <licenses>
    <license>
      <name>Apache 2.0</name>
      <url>http://www.apache.org/licenses/LICENSE-2.0.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>git@github.com:SLTodd/genetic-akka.git</url>
    <connection>scm:git:git@github.com:SLTodd/genetic-akka.git</connection>
  </scm>
  <developers>
    <developer>
      <id>SLTodd</id>
      <name>Simon Todd</name>
      <url>https://github.com/SLTodd</url>
    </developer>
  </developers>
)
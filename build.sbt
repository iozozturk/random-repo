
name := "airport-service"

version := "1.0"

scalaVersion := "2.12.2"

resolvers += Resolver.bintrayRepo("hseeberger", "maven")

val project = Project(
  id = "airport-service",
  base = file(".")
).settings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.5",
    "com.google.inject" % "guice" % "4.0",
    "net.codingwell" %% "scala-guice" % "4.1.0",
    "de.heikoseeberger" %% "akka-http-play-json" % "1.15.0",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.0",
    "com.github.tototoshi" %% "scala-csv" % "1.3.4",
    "org.scalatest" %% "scalatest" % "3.0.0" % Test,
    "com.typesafe.akka" %% "akka-http-testkit" % "10.0.0" % Test,
    "org.mockito" % "mockito-all" % "1.8.4" % Test
  ),
  fork in Test := true,
  javaOptions in Test += "-Dconfig.resource=test.conf"
)

Revolver.settings
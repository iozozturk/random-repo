name := "airport-service"

version := "1.0"

scalaVersion := "2.11.8"

val project = Project(
  id = "airport-service",
  base = file(".")
).settings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-http" % "10.0.5"
  )
)
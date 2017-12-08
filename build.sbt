import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.aerohitsaxena",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  dockerBaseImage := "openjdk:jre-alpine",
  scalafmtOnCompile in ThisBuild := true,
  scalafmtTestOnCompile in ThisBuild := true
)

lazy val akkaHttpVersion = "10.0.11"

lazy val backend = (project in file("backend"))
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-http"            % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion
    )
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)

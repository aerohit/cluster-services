import Dependencies._

// TODO: Split these settings into more sensible groups
// (eg: not every project would be published as docker image)
lazy val commonSettings = Seq(
  organization := "com.aerohitsaxena",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4",
  dockerBaseImage := "openjdk:jre-alpine",
  dockerUpdateLatest := true,
  scalafmtOnCompile in ThisBuild := true,
  scalafmtTestOnCompile in ThisBuild := true,
  dockerRepository := Option("https://hub.docker.com/aerohit")
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

// TODO: create micro service kind of plugin for above

lazy val frontend = (project in file("frontend"))
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

// The idea of this service is that it simply registers with consul on bootup
lazy val myservice = project
  .settings(
    commonSettings,
    libraryDependencies ++= Seq(
      "com.typesafe" % "config"       % "1.3.1",
      "org.scalaj"   %% "scalaj-http" % "2.3.0"
    )
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .enablePlugins(AshScriptPlugin)

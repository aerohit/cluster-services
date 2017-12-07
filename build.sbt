import Dependencies._

lazy val commonSettings = Seq(
  organization := "com.aerohitsaxena",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.4"
)

lazy val backend = (project in file("backend"))
  .settings(
    commonSettings
  )

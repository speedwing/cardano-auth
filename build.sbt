name := """cardano-auth"""
organization := "io.gimbalabs"

ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.6"

resolvers += Resolver.mavenLocal

lazy val api = (project in file("api"))
  .enablePlugins(PlayScala)
  .settings(
    libraryDependencies ++= Seq(
      guice,
      jdbc,
      ws,
      "org.postgresql" % "postgresql" % "42.2.20",
      "org.playframework.anorm" %% "anorm" % "2.6.10",
      "org.playframework.anorm" %% "anorm-postgres" % "2.6.10",
      "com.speedwing" % "lib" % "0.0.1-SNAPSHOT" excludeAll ExclusionRule(organization = "com.fasterxml.jackson.core"),
      "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test
    ),
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in packageDoc := false,
    sources in (Compile,doc) := Seq.empty,
    resolvers += Resolver.mavenLocal
  )

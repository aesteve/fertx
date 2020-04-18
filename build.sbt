lazy val root = project
  .in(file("."))
  .settings(
    name := "fertx",
    description := "Using Vert.x and dotty in a functional way",
    version := "0.0.1",
    scalaVersion := dottyLatestNightlyBuild.get
  )
scalacOptions ++= Seq(
  "-language:implicitConversions"
)
// Waiting for Vert.x to be published with Scala 2.13 (4.0.0 ?)
// libraryDependencies += ("io.vertx" %% "vertx-web-scala" % "4.0.0").withDottyCompat(scalaVersion.value)

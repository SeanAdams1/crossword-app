scalaVersion := "2.12.4"

lazy val crossword = crossProject.settings(
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "main" / "scala",
  name := "crossword-app",
  version := "0.1-SNAPSHOT",
  libraryDependencies ++= Seq(
    "com.lihaoyi" %%% "scalatags" % "0.6.7",
    //"com.lihaoyi" %%% "upickle" % "0.4.4"
    // https://mvnrepository.com/artifact/com.github.japgolly.scalacss/core
    "com.github.japgolly.scalacss" %%% "core" % "0.5.5",
    // https://mvnrepository.com/artifact/com.github.japgolly.scalacss/ext-scalatags
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.5.5"
  )

).jsSettings(
  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.4",
    "org.scala-js" %% "scalajs-library" % "0.6.22"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-xml" % "1.0.6"
    //"com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
    //"com.typesafe.akka" %% "akka-actor" % "2.4.12",
    //"org.webjars" % "bootstrap" % "3.2.0"
  )
)

lazy val crosswordJS = crossword.js
lazy val crosswordJVM = crossword.jvm.settings(
  //Include the compiled js file so it can be served
  (resources in Compile) += (fastOptJS in (crosswordJS, Compile)).value.data
)


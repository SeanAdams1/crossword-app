scalaVersion := "2.12.8"
 
val crossword = crossProject.settings(
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value  / "shared" / "main" / "scala",
  name := "crossword-app",
  version := "0.1-SNAPSHOT",
  libraryDependencies ++= Seq(
    //https://mvnrepository.com/artifact/com.lihaoyi/scalatags
    "com.lihaoyi" %%% "scalatags" % "0.7.0",
    //https://mvnrepository.com/artifact/com.lihaoyi/upickle
    "com.lihaoyi" %%% "upickle" % "0.7.5",
    //https://mvnrepository.com/artifact/com.lihaoyi/autowire
    "com.lihaoyi" %%% "autowire" % "0.2.6",
    // https://mvnrepository.com/artifact/com.github.japgolly.scalacss/core
    "com.github.japgolly.scalacss" %%% "core" % "0.5.6",
    // https://mvnrepository.com/artifact/com.github.japgolly.scalacss/ext-scalatags
    "com.github.japgolly.scalacss" %%% "ext-scalatags" % "0.5.6"
  )
).jsSettings(
  libraryDependencies ++= Seq(
    // https://mvnrepository.com/artifact/org.scala-js/scalajs-dom
    "org.scala-js" %%% "scalajs-dom" % "0.9.7",
    //https://mvnrepository.com/artifact/org.scala-js/scalajs-library
    "org.scala-js" %% "scalajs-library" % "0.6.28"
  )
).jvmSettings(
  libraryDependencies ++= Seq(
    // https://mvnrepository.com/artifact/org.scala-lang.modules/scala-xml
    "org.scala-lang.modules" %% "scala-xml" % "1.2.0",
    
    "com.typesafe.akka" %% "akka-http" % "10.1.9",

//"com.typesafe.akka" %% "akka-http-experimental" % "2.4.11",
    // https://mvnrepository.com/artifact/com.typesafe.akka/akka-actor
    "com.typesafe.akka" %% "akka-actor" % "2.6.0-M5",
    "com.typesafe.akka" %% "akka-stream" % "2.5.23",
    
    "org.webjars" % "bootstrap" % "3.2.0"
  )
)

lazy val crosswordJS = crossword.js
lazy val crosswordJVM = crossword.jvm.settings(
  //Include the compiled js file so it can be served
  (resources in Compile) += (fastOptJS in (crosswordJS  , Compile)).value.data
)


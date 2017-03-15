lazy val scalaV = "2.12.1"
lazy val akkaHttpV = "10.0.4"
lazy val reactV = "15.4.2"
lazy val scalajsreactV = "1.0.0-RC1"
lazy val upickleV = "0.4.4"
lazy val autowireV = "0.2.6"

scalaVersion in ThisBuild := scalaV

lazy val commonSettings = Seq(
  version := "0.1-SNAPSHOT",
  scalaVersion := scalaV
)

lazy val `sr-shared` = (crossProject.crossType(CrossType.Pure) in file("sr-shared"))
  .settings(commonSettings: _*)
  .settings(
    name := "sr-shared",
    libraryDependencies ++= Seq(
    )
  )

lazy val `sr-sharedJVM` = `sr-shared`.jvm
lazy val `sr-sharedJS` = `sr-shared`.js

lazy val `sr-service` = (project in file("sr-service"))
  .settings(commonSettings: _*)
  .settings(
    name := "sr-service",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %% "upickle" % upickleV,
      "com.lihaoyi" %% "autowire" % autowireV,

      "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV
    )
    ,(managedClasspath in Runtime) += (packageBin in (`sr-ui-assets`,Assets)).value
  )
  .dependsOn(`sr-sharedJVM`)

lazy val `sr-ui` = (project in file("sr-ui"))
  .enablePlugins(ScalaJSPlugin)
  .settings(commonSettings: _*)
  .settings(
    name := "sr-ui",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % upickleV,
      "com.lihaoyi" %%% "autowire" % autowireV,
      "org.scala-js" %%% "scalajs-dom" % "0.9.1",
      "com.github.japgolly.scalajs-react" %%% "core" % scalajsreactV,
      "com.github.japgolly.scalajs-react" %%% "extra" % scalajsreactV,
      "com.github.japgolly.scalajs-react" %%% "ext-scalaz72" % scalajsreactV,
      "com.github.japgolly.scalajs-react" %%% "ext-monocle" % scalajsreactV
    ),

    jsDependencies ++= Seq(
      "org.webjars.bower" % "react" % reactV
        /        "react-with-addons.js"
        minified "react-with-addons.min.js"
        commonJSName "React",

      "org.webjars.bower" % "react" % reactV
        /         "react-dom.js"
        minified  "react-dom.min.js"
        dependsOn "react-with-addons.js"
        commonJSName "ReactDOM",

      "org.webjars.bower" % "react" % reactV
        /         "react-dom-server.js"
        minified  "react-dom-server.min.js"
        dependsOn "react-dom.js"
        commonJSName "ReactDOMServer"
    ),
    skip in packageJSDependencies := false
  )
  .dependsOn(`sr-sharedJS`)

lazy val jsProjects = Seq(`sr-ui`)

lazy val `sr-ui-assets` = (project in file("sr-ui-assets"))
  .enablePlugins(SbtWeb, ScalaJSWeb)
  .settings(
    scalaVersion := scalaV
    ,name := "sr-ui-assets"
    ,scalaJSProjects := jsProjects
    ,pipelineStages := Seq(scalaJSDev)
  )
  .aggregate(jsProjects.map(Project.projectToRef): _*)

scalaVersion := "2.11.8"

lazy val mainProject = Project(
  id = "meetup",
  base = file("."),
  settings = Defaults.coreDefaultSettings ++ Seq(
    scalaVersion := "2.11.8",
    libraryDependencies ++= List(
      "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "org.slf4j" % "slf4j-nop" % "1.7.19",
      "com.typesafe.scala-logging" %% "scala-logging-slf4j" % "2.1.2",
      "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
      "org.scalatest" %% "scalatest" % "2.2.6" % Test,
      "org.scalacheck" %% "scalacheck" % "1.12.5" % Test
    )
  ))


name := "notifyIfModified"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  "com.typesafe" %% "play-plugins-mailer" % "2.2.0",
  "com.typesafe.akka" % "akka-actor_2.10" % "2.2-M1"
)     

play.Project.playScalaSettings

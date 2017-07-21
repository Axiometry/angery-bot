name := "cluckbot"

scalaVersion := "2.11.8"

version := "0.0-SNAPSHOT"

resolvers += "scalac repo" at "https://raw.githubusercontent.com/ScalaConsultants/mvn-repo/master/"

libraryDependencies += "io.scalac" %% "slack-scala-bot-core" % "0.2.1"

libraryDependencies += "com.typesafe.play" %% "play-ws" % "2.4.3"

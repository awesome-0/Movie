name := "bookstore"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.13",
  "com.typesafe.akka" %% "akka-stream" % "2.5.13",
  "com.typesafe.akka" %% "akka-http" % "10.1.3",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.13",
  "com.typesafe.akka" %% "akka-remote" % "2.5.13",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.3",
  "com.typesafe.akka" %% "akka-slf4j"      % "2.4.11",
  "com.typesafe.akka" %% "akka-http-spray-json-experimental"  % "2.4.11",
  "com.typesafe.akka" %% "akka-slf4j"      % "2.4.11"

)

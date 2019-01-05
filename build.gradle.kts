group = "com.github.aesteve"
version = "0.1-SNAPSHOT"

// Versions definitions
val scalaMajor = "2.12"
val scalaMinor = "2"
val scalaVersion = "$scalaMajor.$scalaMinor"
val vertxVersion = "3.6.2"

plugins {
    scala
    id("com.github.maiflai.scalatest") version "0.23"
}

repositories {
    mavenCentral()
}

dependencies {
    // Scala
    compile("org.scala-lang:scala-library:$scalaVersion")
    compile("io.vertx:vertx-web-scala_$scalaMajor:$vertxVersion")
    compile("io.swagger.core.v3:swagger-core:2.0.6")

    // Tests
    testCompile("org.scalatest:scalatest_$scalaMajor:3.0.4")
    testCompile("io.vertx:vertx-web-client-scala_$scalaMajor:$vertxVersion")
    testCompile("com.fasterxml.jackson.module:jackson-module-scala_$scalaMajor:2.9.7")
    testRuntime("org.pegdown:pegdown:1.6.0")
}

tasks.withType<ScalaCompile> {
    scalaCompileOptions.additionalParameters = listOf(
            "-feature",
            "-language:postfixOps",
            "-language:implicitConversions"
    )
}

// Tests
tasks.withType<Test> {
    maxParallelForks = 1
}

// Gradle Wrapper
tasks.withType<Wrapper> {
    gradleVersion = "5.1"
}

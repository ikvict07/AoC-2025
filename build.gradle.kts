plugins {
    kotlin("jvm") version "2.2.21"
    application
    id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "org.jikvict.learn"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("MainKt")
}
graalvmNative {
    binaries {
        named("main") {
            buildArgs.add("-O3")

            buildArgs.add("-march=native")

            buildArgs.add("--gc=epsilon")
            buildArgs.add("--add-opens java.base/java.nio=ALL-UNNAMED")
            buildArgs.add("--add-opens java.base/sun.nio.ch=ALL-UNNAMED")
        }
    }
}

tasks.register<Jar>("fatJar") {
    group = "build"
    description = "Builds a Fat JAR with all dependencies"
    archiveClassifier.set("all")

    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<JavaExec>("runDay") {
    group = "advent of code"
    description = "Runs the solution for a specific day. Usage: ./gradlew runDay -Pday=1"

    val day = project.findProperty("day") ?: "1"
    mainClass.set("day$day.SolutionKt")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = layout.projectDirectory.asFile

    jvmArgs(
        "-XX:+UnlockExperimentalVMOptions",
        "-XX:+UseEpsilonGC",
        "-Xms2g",
        "-Xmx2g",
        "-XX:+AlwaysPreTouch"
    )

    standardInput = System.`in`
}
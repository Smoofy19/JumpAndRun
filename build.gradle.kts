import java.util.*

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = "de.smoofy"
version = "1.1"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
        sourceCompatibility = JavaVersion.VERSION_17.toString()
        targetCompatibility = JavaVersion.VERSION_17.toString()
    }

    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveClassifier.set("")
    }

    assemble {
        dependsOn(reobfJar)
    }

    runServer {
        dependsOn("copyToServer")
        minecraftVersion("1.20.4")
    }

    register<Copy>("copyToServer") {
        val props = Properties()
        val propFile = file("build.properties")
        if (!propFile.exists()) propFile.createNewFile()
        file("build.properties").reader().let { props.load(it) }
        val path = props.getProperty("targetDir") ?: ""
        if (path.isEmpty()) throw RuntimeException("targetDir is not set in build.properties")
        from(jar)
        destinationDir = File(path)
    }
}
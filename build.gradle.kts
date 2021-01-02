plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}

tasks {
    jar {
        manifest {
            attributes(
                mutableMapOf<String,String>(
                    "Main-Class" to "ca.alexleung.lox.MainKt"
                )
            )
        }
    }

    shadowJar {
        archiveBaseName.set("klox")
        archiveClassifier.set("")
        archiveVersion.set("")
    }
}

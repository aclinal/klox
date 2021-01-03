import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

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
    implementation("org.junit.jupiter:junit-jupiter:5.4.2")
}

tasks {
    jar {
        manifest {
            attributes(
                mutableMapOf<String, String>(
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

    test {
        useJUnitPlatform()

        testLogging {
            lifecycle {
                events = mutableSetOf(TestLogEvent.FAILED, TestLogEvent.PASSED, TestLogEvent.SKIPPED)
                exceptionFormat = TestExceptionFormat.FULL
                showExceptions = true
                showCauses = true
                showStackTraces = true
                showStandardStreams = true
            }
            info.events = lifecycle.events
            info.exceptionFormat = lifecycle.exceptionFormat
        }

        val failedTests = mutableListOf<TestDescriptor>()
        val skippedTests = mutableListOf<TestDescriptor>()

        // See https://github.com/gradle/kotlin-dsl/issues/836
        addTestListener(object : TestListener {
            override fun beforeSuite(suite: TestDescriptor) {}
            override fun beforeTest(testDescriptor: TestDescriptor) {}
            override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
                when (result.resultType) {
                    TestResult.ResultType.FAILURE -> failedTests.add(testDescriptor)
                    TestResult.ResultType.SKIPPED -> skippedTests.add(testDescriptor)
                }
            }

            override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                if (suite.parent == null) { // root suite
                    logger.lifecycle("----")
                    logger.lifecycle("Test result: ${result.resultType}")
                    logger.lifecycle(
                        "Test summary: ${result.testCount} tests, " +
                                "${result.successfulTestCount} succeeded, " +
                                "${result.failedTestCount} failed, " +
                                "${result.skippedTestCount} skipped"
                    )
                    if (failedTests.isNotEmpty()) {
                        logger.lifecycle("\tFailed Tests:")
                        failedTests.forEach {
                            parent?.let { parent ->
                                logger.lifecycle("\t\t${parent.name} - ${it.name}")
                            } ?: logger.lifecycle("\t\t${it.name}")
                        }
                    }

                    if (skippedTests.isNotEmpty()) {
                        logger.lifecycle("\tSkipped Tests:")
                        skippedTests.forEach {
                            parent?.let { parent ->
                                logger.lifecycle("\t\t${parent.name} - ${it.name}")
                            } ?: logger.lifecycle("\t\t${it.name}")
                        }
                    }
                }
            }
        })
    }
}

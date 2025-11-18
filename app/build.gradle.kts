plugins {
    val kotlinVersion = "2.3.0-Beta2"
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.jetbrains.kotlin.plugin.jpa") version kotlinVersion
    application
    id("com.github.ben-manes.versions") version "0.53.0"
    idea
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(21)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

dependencies {
    val kotlinVersion = "2.3.0-Beta2"
    val hibernateVersion = "7.2.0.CR2"
    val springDataVersion = "4.0.0"
    val springFrameworkVersion = "6.2.1"
    val junitVersion = "5.11.4"

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:$kotlinVersion"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("org.hibernate:hibernate-core:$hibernateVersion")
    implementation("org.springframework.data:spring-data-jpa:$springDataVersion")
    runtimeOnly("org.postgresql:postgresql:42.7.3")

    testImplementation(platform("org.junit:junit-bom:$junitVersion"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    testImplementation("org.springframework:spring-test:$springFrameworkVersion")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("ecommerce.AppKt")
}

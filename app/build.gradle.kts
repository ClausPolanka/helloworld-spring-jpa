plugins {
    id("org.jetbrains.kotlin.jvm") version "2.1.20-Beta1"
    application
    id("org.jetbrains.kotlin.plugin.jpa") version "2.1.20-Beta1"
    id("com.github.ben-manes.versions") version "0.53.0"
    idea
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
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
    jvmToolchain(17)
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        showStandardStreams = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))

    // Use the Kotlin JDK 8 standard library.
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // My Dependencies
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.1.20-Beta1")
    implementation("org.hibernate:hibernate-core:7.0.0.Beta3")
    implementation("org.springframework.data:spring-data-jpa:4.0.0")
    runtimeOnly("org.postgresql:postgresql:42.7.3")
    testImplementation("org.springframework:spring-test:6.2.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.11.4")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    // Define the main class for the application.
    mainClass.set("ecommerce.AppKt")
}
plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"

    id("java")
}

group = "net.aniby.gray"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://cloud.experitest.com/repo/")
    maven("https://jitpack.io")
}

dependencies {
    // Android Driver
    implementation("io.appium:java-client:9.0.0")

    // Utilities
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
    implementation("org.jsoup:jsoup:1.17.1")

    // Tests
    testImplementation("junit:junit:4.13.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "net.aniby.gray.Main"
    }
}

tasks.test {
    useJUnitPlatform()
    systemProperty("file.encoding", "utf-8")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = "11"
    targetCompatibility = "11"
    options.encoding = "UTF-8"
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf(
                "Main-Class" to "net.aniby.gray.Main",
                "Implementation-Vendor" to "aniby.net"
        ))
    }
}
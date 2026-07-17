import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.collections.getOrDefault

val javaVersion = JavaVersion.VERSION_25
val dockerRepository = project.properties.getOrDefault("dockerRepository", "ghcr.io")
val repositoryPath = "mkantsurov/ihome-app"
val testSpringConfLocation = project.properties.getOrDefault("testSpringConfLocation", "")

description = "I-Home Web Application"

plugins {
    idea
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.cloud.tools.jib") version "3.4.3"
    id("org.flywaydb.flyway") version "12.7.0"
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

val ihomeVersion = run {
    val vVersion = System.getenv("V_VER")
    val vHash  = System.getenv("V_HASH")
    val suffix = System.getenv("SUFFIX") ?: ""
    if (!vVersion.isNullOrEmpty() && !vHash.isNullOrEmpty()) {
        if (suffix.isEmpty()) {
            vVersion
        } else {
            "$vVersion.$vHash$suffix"
        }
    } else {
        val gitVersion = "git tag --sort=-committerdate".runCommand()
        if (gitVersion.endsWith(".0")) {
            "$gitVersion-SNAPSHOT"
        } else {
            gitVersion
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core:12.7.0")
    implementation("org.flywaydb:flyway-database-postgresql:12.7.0")
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("io.jsonwebtoken:jjwt:0.9.0")
    implementation("org.postgresql:postgresql:42.7.11")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("com.zaxxer:HikariCP:2.7.4")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("commons-io:commons-io:2.14.0")
    implementation("net.logstash.logback:logstash-logback-encoder:6.4")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.3.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.mockito:mockito-core:5.12.0")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc:3.0.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.3.1")
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.jayway.jsonpath:json-path:2.9.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    runtimeOnly("org.springframework.boot:spring-boot-devtools")
}

fun execCommandWithOutput(input: String): String {
    return try {
        val parts = input.split("\\s".toRegex())
        val proc = ProcessBuilder(*parts.toTypedArray())
                .directory(rootDir)
                .redirectOutput(ProcessBuilder.Redirect.PIPE)
                .redirectError(ProcessBuilder.Redirect.PIPE)
                .start()
        proc.waitFor(20, TimeUnit.SECONDS)
        proc.inputStream.bufferedReader().readText().trim()
    } catch (e: IOException) {
        "<empty>"
    }
}

tasks.getByName<BootJar>("bootJar") {
    mainClass.set("technology.positivehome.ihome.ServerApplication")
    outputs.upToDateWhen { false }
    archiveFileName.set("app.jar")
}

tasks.getByName<BootRun>("bootRun") {
    dependsOn(tasks.named("test"))
    mainClass.set("technology.positivehome.ihome.ServerApplication")
    environment(mapOf(
        "SPRING_CONFIG_ADDITIONALLOCATION" to testSpringConfLocation,
        "DEEPSEEK_API_KEY" to (project.properties.getOrDefault("deepseekApiKey", "") as String)
    ))
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=40990")
}

jib {
    from {
        image = "eclipse-temurin:25-jre"
    }
    to {
        image = "$dockerRepository/$repositoryPath"
        tags = setOf(ihomeVersion, "latest")
    }
    container {
        mainClass = "technology.positivehome.ihome.ServerApplication"
        jvmFlags = listOf(
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:40990",
            "-Djava.security.egd=file:/dev/./urandom"
        )
        args = listOf()
        ports = listOf("8080")
        user = "997:667"
    }
}

springBoot {
    buildInfo()
}

tasks {
    "test"(Test::class) {
        filter {
            includeTestsMatching("technology.positivehome.ihome.server.processor.*")
            includeTestsMatching("technology.positivehome.ihome.server.persistence.repository.*")
        }
    }
}

tasks.withType<Test>().configureEach {
    group = "tests"
    useJUnitPlatform()
    delete(layout.buildDirectory)
    outputs.dir(layout.buildDirectory.dir("generated-snippets/v1"))
    jvmArgs("-Dspring.config.additional-location=$testSpringConfLocation")
    testLogging {
        events("passed", "skipped", "failed", "standardOut", "standardError")
    }
}

fun String.runCommand(): String {
    val process = ProcessBuilder(*this.trim().split(" ").toTypedArray())
        .directory(file("./"))
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readText()
    process.waitFor()
    return output.lineSequence().firstOrNull()?.trim() ?: ""
}

idea {
    project {
        module {
            name = rootProject.name
            setDownloadJavadoc(true)
        }
    }
}

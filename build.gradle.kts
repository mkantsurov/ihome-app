import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.io.IOException
import java.util.concurrent.TimeUnit

val javaVersion = JavaVersion.VERSION_25
val dockerRepository: String by project
val repositoryPath = "mkantsurov/ihome-app"
val testSpringConfLocation: String by project

description = "I-Home Web Application"

plugins {
    idea
    java
    id("org.springframework.boot")
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.cloud.tools.jib") version "3.4.3"
    id("org.flywaydb.flyway") version "7.5.2"
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
    maven { setUrl("https://repo.spring.io/release/") }
    maven { setUrl("https://repo.spring.io/libs-snapshot-local") }
    maven { setUrl("https://repo.spring.io/libs-milestone-local") }
    maven { setUrl("https://repo.spring.io/libs-release-local") }
    maven { setUrl("https://repo.spring.io/libs-milestone") }
    maven { setUrl("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("io.jsonwebtoken:jjwt:0.9.0")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("com.zaxxer:HikariCP:2.7.4")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("com.google.guava:guava:33.0.0-jre")
    implementation("org.apache.httpcomponents:httpclient:4.5.13")
    implementation("commons-io:commons-io:2.14.0")
    implementation("net.logstash.logback:logstash-logback-encoder:6.4")
    implementation("com.fasterxml.uuid:java-uuid-generator:4.3.0")
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
    mainClass.set("technology.positivehome.ihome.ServerApplication")
    environment("SPRING_CONFIG_ADDITIONALLOCATION" to testSpringConfLocation)
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

sourceSets {
    create("integrationTest") {
        java.srcDir(file("src/integrationTest/java"))
        resources.srcDir(file("src/integrationTest/resources"))
        compileClasspath += sourceSets["main"].output + configurations["testRuntimeClasspath"]
        runtimeClasspath += output + compileClasspath
    }
}

tasks.register<Test>("integrationTest") {
    description = "Runs the integration tests."
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
}

tasks {
    "test"(Test::class) {
        filter {
            includeTestsMatching("technology.positivehome.ihome.server.processor.*")
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

idea {
    project {
        module {
            name = rootProject.name
            setDownloadJavadoc(true)
        }
    }
}

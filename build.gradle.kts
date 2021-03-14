import com.palantir.gradle.docker.DockerExtension
import org.springframework.boot.gradle.tasks.bundling.BootJar
import org.springframework.boot.gradle.tasks.run.BootRun
import java.io.IOException

val javaVersion = JavaVersion.VERSION_11
val dockerRepository: String by project
val testSpringConfLocation: String by project

description = "I-Home Web Application"

plugins {
    idea
    java
    `java-library`
    maven
    jacoco
    id("org.flywaydb.flyway") version "7.5.2"
    id("org.springframework.boot") version "2.2.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.5.RELEASE"
    id("com.palantir.docker") version "0.19.2"
    id("net.ltgt.apt") version "0.10"
    id("com.github.jk1.dependency-license-report") version "1.13"
    id("org.liquibase.gradle") version "1.2.4"
    id("org.sonarqube") version "2.6.2"
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

repositories {
    mavenCentral()
    maven { setUrl("https://repo.spring.io/release/") }
    maven { setUrl("http://repo.spring.io/libs-snapshot-local") }
    maven { setUrl("http://repo.spring.io/libs-milestone-local") }
    maven { setUrl("http://repo.spring.io/libs-release-local") }
    maven { setUrl("https://repo.spring.io/libs-milestone") }
    maven { setUrl("https://plugins.gradle.org/m2/") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compile("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("org.flywaydb:flyway-core")
    implementation("org.springframework.boot:spring-boot-actuator-autoconfigure")
    implementation("org.springframework.boot:spring-boot-configuration-processor")
    implementation("io.jsonwebtoken:jjwt:0.9.0")
    implementation("org.postgresql:postgresql:42.2.2")
    implementation("javax.xml.bind:jaxb-api:2.3.0")
    implementation("com.zaxxer:HikariCP:2.7.4")
    implementation("org.apache.commons:commons-lang3:3.11")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("com.google.guava:guava:28.2-jre")
    implementation("org.apache.httpcomponents:httpclient:4.5.9")
    implementation("commons-io:commons-io:2.6")
    implementation("net.logstash.logback:logstash-logback-encoder:6.4")
    testCompile("com.jayway.jsonpath:json-path:2.4.0")
    testCompile("org.springframework.boot:spring-boot-starter-test")
    runtime("org.springframework.boot:spring-boot-devtools")
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
    mainClassName = "technology.positivehome.ihome.ServerApplication"
    outputs.upToDateWhen { false }
    archiveFileName.set("app.jar")
}

task("prepareDocker") {
    dependsOn("bootJar")
    doLast {
        println("Copying files.......................................")
        copy {
            into("$buildDir/docker/")
            from("$buildDir/libs/app.jar")
        }
    }
}

tasks.getByName<BootRun>("bootRun") {
    main = "technology.positivehome.ihome.ServerApplication"
    environment("SPRING_CONFIG_ADDITIONALLOCATION" to testSpringConfLocation)
    jvmArgs("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=40990")
}

configure<DockerExtension> {
    tags("$version", "latest")
    name = "$dockerRepository/ihome/app"
    setDockerfile(file("Dockerfile"))
    pull(true)
}


tasks.getByName("dockerClean").dependsOn("bootJar")
tasks.getByName("build").dependsOn("dockerClean")
//tasks.getByName("dockerfileZip").dependsOn("prepareDocker")
tasks.getByName("docker").dependsOn("prepareDocker")

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

tasks.named("docker") {
    dependsOn("build")
}

tasks.named("dockerPush") {
    dependsOn("dockerTag")
}

idea {
    project {
        module {
            name = rootProject.name
            setDownloadJavadoc(true)
        }
    }
}



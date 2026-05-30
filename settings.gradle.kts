pluginManagement {
    val springBootVersion: String = "3.1.4"
    plugins {
        id("org.springframework.boot") version springBootVersion
    }
    repositories {
        maven(url = "https://nexus.dev.sectigo.net/repository/iotmgr-releases/")
        gradlePluginPortal()
    }
}
rootProject.name = "ihome-app"

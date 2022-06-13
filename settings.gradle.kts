pluginManagement {
    val springBootVersion: String = "2.5.12"
    plugins {
        id("org.springframework.boot") version springBootVersion
    }
    repositories {
        maven(url = "https://nexus.dev.sectigo.net/repository/iotmgr-releases/")
        gradlePluginPortal()
    }
}
rootProject.name = "ihome-app"

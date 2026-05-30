pluginManagement {
    val springBootVersion: String = "3.4.4"
    plugins {
        id("org.springframework.boot") version springBootVersion
    }
    repositories {
        gradlePluginPortal()
    }
}
rootProject.name = "ihome-app"

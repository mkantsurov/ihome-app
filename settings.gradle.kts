pluginManagement {
    val springBootVersion: String = "3.5.14"
    plugins {
        id("org.springframework.boot") version springBootVersion
    }
    repositories {
        gradlePluginPortal()
    }
}
rootProject.name = "ihome-app"

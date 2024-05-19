pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        maven("https://repo.auxilor.io/repository/maven-public/")
    }
}

rootProject.name = "DeathMessages"

include(
    ":Core",
    ":Hooks:WorldGuard",
    ":Hooks:WorldGuard6",
    ":Hooks:WorldGuard7"
)

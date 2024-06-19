pluginManagement {
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
    ":Hooks:WorldGuard7",

    ":NMS:V1_20_6",
    ":NMS:V1_21",
    ":NMS:Wrapper",
)

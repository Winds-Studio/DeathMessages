pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        maven("https://repo.auxilor.io/repository/maven-public/")
    }
}

rootProject.name = "DeathMessages"

include(
    ":core",

    "nms:abstraction",

    "nms:spigot:v1_12_2",
    "nms:spigot:v1_13",
    "nms:spigot:v1_16_5",

    "nms:paper:v1_16_5",
    "nms:paper:v1_21_4",

    ":hooks:worldguard",
    ":hooks:worldguard6",
    ":hooks:worldguard7",
)

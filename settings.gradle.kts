pluginManagement {
    // Include 'plugins build' to define convention plugins.
    includeBuild("build-logic")
}

rootProject.name = "DeathMessages"

include(":Core", ":WorldGuard", ":WorldGuard7")

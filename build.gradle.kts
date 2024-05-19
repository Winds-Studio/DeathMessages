// Dreeam start - This below enable the `com.willfp:libreforge:shadow` dependency appear.
// TODO: maybe transfer build-logic to here as allprojects format? =-=
plugins {
    `java-library`
    id("io.github.goooler.shadow") version "8.1.7"
    id("com.willfp.libreforge-gradle-plugin") version "1.0.2"
}

allprojects {
    apply(plugin = "java")

    repositories {
        // Eco
        maven {
            name = "auxilor-repo"
            url = uri("https://repo.auxilor.io/repository/maven-public/")
        }
    }
}
// Dreeam end

tasks.build {
    doLast {
        val plugin = project(":Core")
        val file = file("${plugin.layout.buildDirectory.get()}/libs").listFiles()
            ?.find { it.name.startsWith(rootProject.name) }

        delete("bin")
        delete("build/libs")
        file?.copyTo(file("${rootProject.layout.buildDirectory.get()}/libs/${rootProject.name}-${plugin.version}.jar"), true)
        listOf(":Core", ":WorldGuard", ":WorldGuard6", ":WorldGuard7").forEach {
            delete(project(it).layout.buildDirectory.get())
        }
    }

    dependsOn(project(":Core").tasks.build)
}

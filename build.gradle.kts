// Dreeam start - This below enable the `com.willfp:libreforge:shadow` dependency appear.
// TODO: maybe transfer build-logic to here as allprojects format? =-=
plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1" apply true
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

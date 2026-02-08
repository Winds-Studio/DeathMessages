plugins {
    `java-library`
    `maven-publish`
}

group = "dev.mrshawn.deathmessages"
version = "1.4.21-SNAPSHOT"

repositories {
    mavenCentral()

    flatDir {
        dirs("./libs")
    }

    // PaperMC
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    // PlaceholderAPI
    maven {
        name = "placeholderapi-repo"
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    // NBT-API
    maven {
        name = "codemc-repo"
        url = uri("https://repo.codemc.org/repository/maven-public/")
    }

    // Eco
    maven {
        name = "auxilor-repo"
        url = uri("https://repo.auxilor.io/repository/maven-public/")
    }

    // JitPack
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io/")
    }

    // DiscordSRV
    maven {
        name = "Scarsz-Nexus"
        url = uri("https://nexus.scarsz.me/content/groups/public/")
    }

    // CombatLogX
    maven {
        name = "sirblobman-public"
        url = uri("https://nexus.sirblobman.xyz/public/")
    }

    // sk89q's
    maven {
        name = "sk89q-repo"
        url = uri("https://maven.enginehub.org/repo/")
    }

    // Lumine's
    maven {
        name = "Lumine Releases"
        url = uri("https://mvn.lumine.io/repository/maven-public/")
    }

    // worldguard-legacy
    maven {
        name = "minebench-repo"
        url = uri("https://repo.minebench.de/")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                "version" to project.version
            )
        }
    }
}

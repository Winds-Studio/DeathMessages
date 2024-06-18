plugins {
    `java-library`
    `maven-publish`
}

group = "dev.mrshawn"
version = "1.4.19-SNAPSHOT"

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

    // JitPack
    maven {
        name = "jitpack.io"
        url = uri("https://jitpack.io")
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

    // FoliaLib
    maven {
        name = "devmart-other"
        url = uri("https://nexuslite.gcnt.net/repos/other/")
    }

    // worldguard-legacy
    maven {
        name = "minebench-repo"
        url = uri("https://repo.minebench.de/")
    }

    // Eco
    maven {
        name = "auxilor-repo"
        url = uri("https://repo.auxilor.io/repository/maven-public/")
    }
}

val adventureVersion = "4.16.0" // Dreeam TODO: Item hover broken on 4.17.0

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT") // Universal
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    compileOnly("commons-io:commons-io:2.16.1")
    compileOnly("org.apache.logging.log4j:log4j-api:2.23.1")
    api("com.github.cryptomorin:XSeries:10.0.0")
    api("org.bstats:bstats-bukkit:3.0.2")
    api("com.tcoded:FoliaLib:0.3.1")

    implementation("de.tr7zw:item-nbt-api:2.12.5-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("com.discordsrv:discordsrv:1.27.0")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("com.willfp:eco:6.70.1")
    compileOnly("com.willfp:EcoEnchants:12.5.1")

    api("net.kyori:adventure-platform-bukkit:4.3.2")
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    api("net.kyori:adventure-text-minimessage:$adventureVersion")
    api("net.kyori:adventure-key:$adventureVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.build.configure {
    dependsOn("shadowJar")
}

tasks {
    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                "version" to project.version
            )
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

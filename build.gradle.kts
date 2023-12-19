plugins {
    `maven-publish`
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "8.1.1" apply true
}

group = "dev.mrshawn"
version = "1.4.18-SNAPSHOT"

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
//    maven {
//        name = "sirblobman-public"
//        url = uri("https://nexus.sirblobman.xyz/repository/public/")
//    }

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

    // acf-paper
    maven {
        name = "aikar-repo"
        url = uri("https://repo.aikar.co/content/groups/aikar/")
    }
}

val adventureVersion = "4.15.0"

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    compileOnly("commons-io:commons-io:2.15.1")
    compileOnly("org.apache.logging.log4j:log4j-api:2.22.0")
    api("com.github.cryptomorin:XSeries:9.8.0")
    api("org.bstats:bstats-bukkit:3.0.2")
    api("com.tcoded:FoliaLib:0.3.1")

    implementation("de.tr7zw:item-nbt-api:2.12.2")
    compileOnly("me.clip:placeholderapi:2.11.5")
    compileOnly("com.github.HMJosh:DiscordBotAPI:v1.1.1")
    compileOnly("com.discordsrv:discordsrv:1.27.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-core:7.1.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0-SNAPSHOT") {
        exclude(group = "org.bstats")
    }
    compileOnly("com.github.sirblobman.combatlogx:CombatLogX:11.4.0.2.Beta-1212")
    compileOnly("io.lumine:Mythic-Dist:5.5.1")

    api("net.kyori:adventure-platform-bukkit:4.3.1")
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    api("net.kyori:adventure-key:$adventureVersion")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "17"
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.build.configure {
    dependsOn("shadowJar")
}

tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
    archiveFileName = "${project.name}-${project.version}.${archiveExtension.get()}"
    exclude("META-INF/**") // Dreeam - Avoid to include META-INF/maven in Jar
    minimize {
        exclude(dependency("com.tcoded.folialib:.*:.*"))
    }
    relocate("kotlin", "dev.mrshawn.deathmessages.libs.kotlin")
    relocate("net.kyori", "dev.mrshawn.deathmessages.libs.kyori")
    relocate("com.cryptomorin.xseries", "dev.mrshawn.deathmessages.libs.xseries")
    relocate("org.bstats", "dev.mrshawn.deathmessages.libs.bstats")
    relocate("com.tcoded.folialib", "dev.mrshawn.deathmessages.libs.folialib")
    relocate("de.tr7zw.changeme.nbtapi", "dev.mrshawn.deathmessages.libs.nbtapi")
    relocate("net.dv8tion.jda", "dev.mrshawn.deathmessages.libs.jda")
}

tasks {
    processResources {
        filesMatching("**/plugin.yml") {
            expand("version" to project.version)
        }
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

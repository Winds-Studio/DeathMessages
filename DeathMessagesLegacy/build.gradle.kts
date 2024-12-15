plugins {
    id("cn.dreeam.deathmessages.wrapper")
    id("com.gradleup.shadow") version "8.3.5"
    id("com.willfp.libreforge-gradle-plugin") version "1.0.2"
}

val adventureVersion = findProperty("adventure-version")
val adventurePlatformVersion = findProperty("adventure-platform-version")

dependencies {
    implementation(project(":Hooks:WorldGuard"))
    implementation(project(":Hooks:WorldGuard6"))
    implementation(project(":Hooks:WorldGuard7"))

    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT") // Universal
    compileOnly("commons-io:commons-io:2.18.0") // Remove this
    compileOnly("org.apache.logging.log4j:log4j-api:2.24.2")
    implementation("com.github.cryptomorin:XSeries:12.0.0")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("com.github.technicallycoded:FoliaLib:0.4.3")

    implementation("de.tr7zw:item-nbt-api:2.14.0")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.discordsrv:discordsrv:1.29.0")
    compileOnly("io.lumine:Mythic-Dist:5.7.2")
    compileOnly("com.willfp:eco:6.74.4")
    compileOnly("com.willfp:EcoEnchants:12.19.3")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    compileOnly("com.github.sirblobman.combatlogx:api:11.5-SNAPSHOT")
    compileOnly(files("libs/LangUtils-1.9.jar"))
    compileOnly("org.sayandev:sayanvanish-api:1.6.0")
    compileOnly("org.sayandev:sayanvanish-bukkit:1.6.0")

    api("net.kyori:adventure-platform-bukkit:$adventurePlatformVersion")
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    api("net.kyori:adventure-text-minimessage:$adventureVersion")
    api("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    api("net.kyori:adventure-key:$adventureVersion")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    shadowJar {
        archiveFileName = "${rootProject.name}-${project.version}.${archiveExtension.get()}"
        exclude("META-INF/**") // Dreeam - Avoid to include META-INF/maven in Jar
//            minimize {
//                exclude(dependency("com.tcoded.folialib:.*:.*"))
//            }
        relocate("kotlin", "dev.mrshawn.deathmessages.libs.kotlin")
        relocate("com.google.gson", "dev.mrshawn.deathmessages.libs.gson") // Don't relocate to avoid item hover issue
        relocate("com.google.auto", "dev.mrshawn.deathmessages.libs.auto") // Don't relocate to avoid item hover issue
        relocate("net.kyori", "dev.mrshawn.deathmessages.libs.kyori") // Don't relocate to avoid item hover issue
        relocate("com.cryptomorin.xseries", "dev.mrshawn.deathmessages.libs.xseries")
        relocate("org.bstats", "dev.mrshawn.deathmessages.libs.bstats")
        relocate("com.tcoded.folialib", "dev.mrshawn.deathmessages.libs.folialib")
        relocate("de.tr7zw.changeme.nbtapi", "dev.mrshawn.deathmessages.libs.nbtapi")
        relocate("net.dv8tion.jda", "dev.mrshawn.deathmessages.libs.jda")
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                "version" to rootProject.version
            )
        }
    }

    build {
        dependsOn(shadowJar)
    }

    libreforgeJar {
        dependsOn("jar")
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

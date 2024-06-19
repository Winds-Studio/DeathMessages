plugins {
    kotlin("jvm") version "2.0.0"
}

val adventureVersion = "4.17.0-SNAPSHOT" // Dreeam TODO: Check whether item hover broken on latest

dependencies {
    api(project(":Hooks:WorldGuard"))
    api(project(":Hooks:WorldGuard6"))
    api(project(":Hooks:WorldGuard7"))

    implementation(project(":NMS:Wrapper"))
    implementation(project(":NMS:V1_20_6"))
    implementation(project(":NMS:V1_21"))

    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT") // Universal
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    compileOnly("commons-io:commons-io:2.16.1")
    compileOnly("org.apache.logging.log4j:log4j-api:2.23.1")
    api("com.github.cryptomorin:XSeries:11.0.0")
    api("org.bstats:bstats-bukkit:3.0.2")
    api("com.tcoded:FoliaLib:0.4.0")

    implementation("de.tr7zw:item-nbt-api:2.13.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.discordsrv:discordsrv:1.27.0")
    compileOnly("io.lumine:Mythic-Dist:5.6.2")
    compileOnly("com.willfp:eco:6.70.1")
    compileOnly("com.willfp:EcoEnchants:12.5.1")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")
    compileOnly("com.meowj:LangUtils:1.9")

    api("net.kyori:adventure-platform-bukkit:4.3.3")
    api("net.kyori:adventure-api:$adventureVersion")
    api("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    api("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    api("net.kyori:adventure-text-minimessage:$adventureVersion")
    api("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    api("net.kyori:adventure-key:$adventureVersion")
}

tasks {
    compileKotlin {
        kotlinOptions {
            jvmTarget = "1.8"
        }
    }
}

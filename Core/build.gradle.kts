plugins {
    kotlin("jvm") version "2.0.20"
}

val adventureVersion = findProperty("adventure-version")
val adventurePlatformVersion = findProperty("adventure-platform-version")

dependencies {
    api(project(":Hooks:WorldGuard"))
    api(project(":Hooks:WorldGuard6"))
    api(project(":Hooks:WorldGuard7"))

    implementation(project(":NMS:Wrapper"))
    implementation(project(":NMS:V1_20_6"))
    implementation(project(":NMS:V1_21"))

    compileOnly("org.spigotmc:spigot-api:1.21.1-R0.1-SNAPSHOT") // Universal
    compileOnly("commons-io:commons-io:2.16.1")
    compileOnly("org.apache.logging.log4j:log4j-api:2.24.0")
    implementation("com.github.cryptomorin:XSeries:11.2.1")
    implementation("org.bstats:bstats-bukkit:3.0.3")
    implementation("com.tcoded:FoliaLib:0.4.2")

    implementation("de.tr7zw:item-nbt-api:2.13.2")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.discordsrv:discordsrv:1.28.1")
    compileOnly("io.lumine:Mythic-Dist:5.7.1")
    compileOnly("com.willfp:eco:6.74.1")
    compileOnly("com.willfp:EcoEnchants:12.18.7")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    compileOnly("com.github.sirblobman.combatlogx:api:11.5-SNAPSHOT")
    compileOnly(files("libs/LangUtils-1.9.jar"))
    compileOnly("org.sayandev:sayanvanish-bukkit:1.4.0")

    api("net.kyori:adventure-platform-bukkit:$adventurePlatformVersion")
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

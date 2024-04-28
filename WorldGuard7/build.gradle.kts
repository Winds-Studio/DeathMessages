plugins {
    id("dev.mrshawn.deathmessages.wrapper")
    id("io.github.goooler.shadow") version "8.1.7"
}

dependencies {
    implementation(project(":WorldGuard"))
    compileOnly("com.sk89q.worldguard:worldguard-core:7.1.0-SNAPSHOT")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.1.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.4.0-SNAPSHOT") {
        exclude(group = "org.bstats")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
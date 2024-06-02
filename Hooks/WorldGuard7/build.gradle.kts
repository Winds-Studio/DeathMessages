plugins {
    id("dev.mrshawn.deathmessages.wrapper")
    id("io.github.goooler.shadow") version "8.1.7"
}

dependencies {
    implementation(project(":Hooks:WorldGuard"))
    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.9") // Latest Java 17
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") // Latest Java 17
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.3.0") { // Latest Java 17
        exclude(group = "org.bstats")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
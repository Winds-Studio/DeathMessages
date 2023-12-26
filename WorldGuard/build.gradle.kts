plugins {
    id("dev.mrshawn.deathmessages.wrapper")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply true
}

dependencies {
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.4")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
}

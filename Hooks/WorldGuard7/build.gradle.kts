plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly(project(":Hooks:WorldGuard"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") // WorldGuard7 began on 1.13.2

    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.5")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.5")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.5") {
        exclude(group = "org.bstats")
    }
}

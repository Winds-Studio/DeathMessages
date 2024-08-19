dependencies {
    compileOnly(project(":Hooks:WorldGuard"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") // WorldGuard7 began on 1.13.2

    compileOnly("com.sk89q.worldguard:worldguard-core:7.0.9") // Latest Java 17
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.9") // Latest Java 17
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:6.1.5") { // Latest Java 17
        exclude(group = "org.bstats")
    }
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

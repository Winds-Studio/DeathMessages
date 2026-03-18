plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly(project(":hooks:worldguard"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT") // WorldGuard7 began on 1.13.2

    compileOnly(libs.bundles.worldguard7) {
        exclude(group = "org.bstats")
    }
}

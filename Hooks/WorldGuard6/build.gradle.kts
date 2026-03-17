plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly(project(":hooks:worldguard"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT") // WorldGuard6 began on 1.8.x

    compileOnly(libs.bundles.worldguard6)
}

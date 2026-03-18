plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.worldguard6)
}

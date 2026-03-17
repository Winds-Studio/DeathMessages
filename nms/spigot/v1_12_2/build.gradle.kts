plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    api(project(":nms:abstraction"))
    compileOnly("org.spigotmc:spigot-api:1.12.2-R0.1-SNAPSHOT") // Universal

    compileOnly(libs.bundles.adventure)
}

plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly(project(":nms:spigot:v1_16_5"))
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.adventure)
}

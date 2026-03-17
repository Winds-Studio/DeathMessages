plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    api(project(":nms:spigot:v1_12_2"))
    compileOnly("org.spigotmc:spigot-api:1.13-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.adventure)
}

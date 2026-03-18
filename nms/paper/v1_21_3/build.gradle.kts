plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    api(project(":nms:paper:v1_16_5"))
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.adventure)
}

plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly(project(":nms:paper:v1_21_3"))
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.adventure)
}

plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    api(project(":nms:abstraction"))
    compileOnly("com.destroystokyo.paper:paper-api:1.16.5-R0.1-SNAPSHOT")

    compileOnly(libs.bundles.adventure)
}

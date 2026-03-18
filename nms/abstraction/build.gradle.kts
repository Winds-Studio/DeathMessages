plugins {
    id("cn.dreeam.deathmessages.wrapper")
}

dependencies {
    compileOnly("com.destroystokyo.paper:paper-api:1.12.2-R0.1-SNAPSHOT") // Universal

    compileOnly(libs.bundles.adventure)
}

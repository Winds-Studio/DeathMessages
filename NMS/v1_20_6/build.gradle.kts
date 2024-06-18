plugins {
    id("dev.mrshawn.deathmessages.wrapper")
    id("io.github.goooler.shadow") version "8.1.7"
}

dependencies {
    implementation(project(":NMS:Wrapper"))
    compileOnly("org.spigotmc:spigot:1.20.6-R0.1-SNAPSHOT")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}
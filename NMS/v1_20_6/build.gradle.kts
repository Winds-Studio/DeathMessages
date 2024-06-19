val adventureVersion = findProperty("adventure-version")
val adventurePlatformVersion = findProperty("adventure-platform-version")

dependencies {
    compileOnly(project(":NMS:Wrapper"))

    compileOnly("org.spigotmc:spigot:1.20.6-R0.1-SNAPSHOT")

    compileOnly("net.kyori:adventure-platform-bukkit:$adventurePlatformVersion")
    compileOnly("net.kyori:adventure-api:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-legacy:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-plain:$adventureVersion")
    compileOnly("net.kyori:adventure-text-minimessage:$adventureVersion")
    compileOnly("net.kyori:adventure-text-serializer-gson:$adventureVersion")
    compileOnly("net.kyori:adventure-key:$adventureVersion")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

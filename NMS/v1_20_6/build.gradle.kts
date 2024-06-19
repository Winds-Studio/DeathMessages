dependencies {
    compileOnly(project(":NMS:Wrapper"))

    compileOnly("org.spigotmc:spigot:1.20.6-R0.1-SNAPSHOT")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "21"
    }
}
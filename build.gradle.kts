plugins {
    `java-library`
    `maven-publish`
    id("com.gradleup.shadow") version "8.3.2"
    id("com.willfp.libreforge-gradle-plugin") version "1.0.2"
}

group = "dev.mrshawn"
version = "1.4.20-SNAPSHOT"

repositories {
    mavenCentral()

    // PaperMC
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }

    // Eco
    maven {
        name = "auxilor-repo"
        url = uri("https://repo.auxilor.io/repository/maven-public/")
    }
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "maven-publish")
    apply(plugin = "com.gradleup.shadow")

    repositories {
        mavenCentral()

        flatDir {
            dirs("./libs")
        }

        // PaperMC
        maven {
            name = "papermc-repo"
            url = uri("https://repo.papermc.io/repository/maven-public/")
        }

        // PlaceholderAPI
        maven {
            name = "placeholderapi-repo"
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }

        // CodeMC-NMS
        maven {
            name = "codemc-nms-repo"
            url = uri("https://repo.codemc.io/repository/nms/")
        }

        // NBT-API
        maven {
            name = "codemc-repo"
            url = uri("https://repo.codemc.org/repository/maven-public/")
        }

        // Eco
        maven {
            name = "auxilor-repo"
            url = uri("https://repo.auxilor.io/repository/maven-public/")
        }

        // JitPack
        maven {
            name = "jitpack.io"
            url = uri("https://jitpack.io")
        }

        // DiscordSRV
        maven {
            name = "Scarsz-Nexus"
            url = uri("https://nexus.scarsz.me/content/groups/public/")
        }

        // CombatLogX
        maven {
            name = "sirblobman-public"
            url = uri("https://nexus.sirblobman.xyz/public/")
        }

        // SayanVanish
        maven {
            name = "sayanvanish-repo"
            url = uri("https://repo.sayandev.org/snapshots")
        }

        // sk89q's
        maven {
            name = "sk89q-repo"
            url = uri("https://maven.enginehub.org/repo/")
        }

        // Lumine's
        maven {
            name = "Lumine Releases"
            url = uri("https://mvn.lumine.io/repository/maven-public/")
        }

        // FoliaLib
        maven {
            name = "devmart-other"
            url = uri("https://nexuslite.gcnt.net/repos/other/")
        }

        // worldguard-legacy
        maven {
            name = "minebench-repo"
            url = uri("https://repo.minebench.de/")
        }
    }

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks {
        compileJava {
            options.encoding = "UTF-8"
        }

        shadowJar {
            archiveFileName = "${rootProject.name}-${rootProject.version}.${archiveExtension.get()}"
            exclude("META-INF/**") // Dreeam - Avoid to include META-INF/maven in Jar
//            minimize {
//                exclude(dependency("com.tcoded.folialib:.*:.*"))
//            }
            relocate("kotlin", "dev.mrshawn.deathmessages.libs.kotlin")
            //relocate("com.google.gson", "dev.mrshawn.deathmessages.libs.gson") // Don't relocate to avoid item hover issue
            //relocate("com.google.auto", "dev.mrshawn.deathmessages.libs.auto") // Don't relocate to avoid item hover issue
            //relocate("net.kyori", "dev.mrshawn.deathmessages.libs.kyori") // Don't relocate to avoid item hover issue
            relocate("com.cryptomorin.xseries", "dev.mrshawn.deathmessages.libs.xseries")
            relocate("org.bstats", "dev.mrshawn.deathmessages.libs.bstats")
            relocate("com.tcoded.folialib", "dev.mrshawn.deathmessages.libs.folialib")
            relocate("de.tr7zw.changeme.nbtapi", "dev.mrshawn.deathmessages.libs.nbtapi")
            relocate("net.dv8tion.jda", "dev.mrshawn.deathmessages.libs.jda")
        }

        processResources {
            filesMatching("**/plugin.yml") {
                expand(
                    "version" to rootProject.version
                )
            }
        }

        build {
            dependsOn(shadowJar)
        }
    }
}

tasks {
    libreforgeJar {
        dependsOn("jar")
    }

    build {
        doLast {
            val plugin = project(":Core")
            val file = file("${plugin.layout.buildDirectory.get()}/libs").listFiles()
                ?.find { it.name.startsWith(rootProject.name) }

            delete("bin")
            delete("build/libs")
            file?.copyTo(file("${rootProject.layout.buildDirectory.get()}/libs/${rootProject.name}-${rootProject.version}.jar"), true)
            listOf(
                ":Core",
                ":Hooks:WorldGuard", ":Hooks:WorldGuard6", ":Hooks:WorldGuard7",
                ":NMS:V1_20_6", ":NMS:V1_21", ":NMS:Wrapper",
            ).forEach {
                delete(project(it).layout.buildDirectory.get())
            }
        }

        dependsOn(project(":Core").tasks.build)
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

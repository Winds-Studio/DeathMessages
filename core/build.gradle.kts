plugins {
    id("cn.dreeam.deathmessages.wrapper")
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

dependencies {
    implementation(project(":hooks:worldguard"))
    implementation(project(":nms:abstraction"))

    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT") // Latest
    compileOnly(libs.common.io) // Remove this
    compileOnly(libs.log4j.api)
    compileOnlyApi(libs.jspecify)
    implementation(libs.xseries)
    implementation(libs.bstats)
    implementation(libs.faststats)
    implementation(libs.folialib)

    implementation(libs.itemnbtapi)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.discordsrv)
    compileOnly(libs.mythicdist)
    compileOnly(libs.eco)
    compileOnly(libs.ecoenchants)
    compileOnly(libs.worldguard6)
    compileOnly(libs.combatlogx)
    compileOnly(files("libs/LangUtils-1.9.jar"))

    compileOnly(libs.bundles.adventure)
}

tasks {
    shadowJar {
        archiveFileName.set("${rootProject.name}-${project.version}.${archiveExtension.get()}")

        // WorldGuard hooks
        from(project(":hooks:worldguard6").sourceSets.main.get().output)
        from(project(":hooks:worldguard7").sourceSets.main.get().output)

        // NMS for Paper
        //from(project(":nms:paper:v1_12_2").sourceSets.main.get().output)
        from(project(":nms:paper:v1_16_5").sourceSets.main.get().output)
        from(project(":nms:paper:v1_21_3").sourceSets.main.get().output)
        from(project(":nms:paper:v1_21_4").sourceSets.main.get().output)

        // NMS for Spigot
        from(project(":nms:spigot:v1_12_2").sourceSets.main.get().output)
        from(project(":nms:spigot:v1_13").sourceSets.main.get().output)
        from(project(":nms:spigot:v1_16_5").sourceSets.main.get().output)
        from(project(":nms:spigot:v1_21_3").sourceSets.main.get().output)

        exclude("META-INF/**") // Dreeam - Avoid to include META-INF/maven in Jar
//            minimize {
//                exclude(dependency("com.tcoded.folialib:.*:.*"))
//            }
        relocate("com.cryptomorin.xseries", "${project.group}.libs.xseries")
        relocate("org.bstats", "${project.group}.libs.bstats")
        relocate("dev.faststats", "${project.group}.libs.faststats")
        relocate("com.tcoded.folialib", "${project.group}.libs.folialib")
        relocate("de.tr7zw.changeme.nbtapi", "${project.group}.libs.nbtapi")
    }

    build {
        dependsOn(shadowJar)
    }

    processResources {
        filesMatching("**/plugin.yml") {
            expand(
                mapOf(
                    "version" to project.version,
                    "depAdventurePlatformBukkit" to libs.adventure.platform.bukkit.get().toString(),
                    "depAdventureApi" to libs.adventure.api.get().toString(),
                    "depAdventureTextMinimessage" to libs.adventure.text.minimessage.get().toString(),
                    "depAdventureTextSerializerLegacy" to libs.adventure.text.serializer.legacy.get().toString(),
                    "depAdventureTextSerializerPlain" to libs.adventure.text.serializer.plain.get().toString(),
                    "depAdventureTextSerializerGson" to libs.adventure.text.serializer.gson.get().toString(),
                    "depAdventureKey" to libs.adventure.key.get().toString()
                )
            )
        }
    }

    runServer {
        minecraftVersion("1.20.4")
    }
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

dependencies {
    api(project(":Hooks:WorldGuard"))
    api(project(":Hooks:WorldGuard6"))
    api(project(":Hooks:WorldGuard7"))

    api(project(":NMS:Wrapper"))
    api(project(":NMS:V1_20_6"))
    api(project(":NMS:V1_21"))

    compileOnly("org.spigotmc:spigot-api:1.21-R0.1-SNAPSHOT") // Universal
    compileOnly("org.apache.commons:commons-lang3:3.14.0")
    compileOnly("commons-io:commons-io:2.16.1")
    compileOnly("org.apache.logging.log4j:log4j-api:2.23.1")
    api("com.github.cryptomorin:XSeries:11.0.0")
    api("org.bstats:bstats-bukkit:3.0.2")
    api("com.tcoded:FoliaLib:0.4.0")

    implementation("de.tr7zw:item-nbt-api:2.13.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("com.discordsrv:discordsrv:1.27.0")
    compileOnly("io.lumine:Mythic-Dist:5.6.2")
    compileOnly("com.willfp:eco:6.70.1")
    compileOnly("com.willfp:EcoEnchants:12.5.1")
    compileOnly("com.sk89q.worldguard:worldguard-legacy:6.2")
    compileOnly("com.github.sirblobman.combatlogx:api:11.4-SNAPSHOT")
    compileOnly("com.meowj:LangUtils:1.9")
}

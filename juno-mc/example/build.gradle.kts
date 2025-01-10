plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
}

group = "${property("group_base")}.mc.example"
version = "${property("juno_mc_version")}"

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Juno
    implementation(project(":juno-mc", "namedElements"))
    include(project(":juno-mc"))
}

tasks.withType<ProcessResources> {
    filesMatching("fabric.mod.json") {
        expand("version" to project.version)
    }
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_21.toString()
    targetCompatibility = JavaVersion.VERSION_21.toString()
}

tasks.withType<PublishToMavenLocal> {
    enabled = false
}

tasks.withType<PublishToMavenRepository> {
    enabled = false
}

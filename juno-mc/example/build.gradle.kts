plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
}

group = "${project.property("group_base")}.mc.example"

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

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

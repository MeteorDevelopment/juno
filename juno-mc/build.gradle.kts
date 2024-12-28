plugins {
    id("fabric-loom") version "1.8-SNAPSHOT"
}

group = "${project.property("group_base")}.mc"

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${project.property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")

    modImplementation(fabricApi.module("fabric-api-base", project.property("fapi_version").toString()))
    include(fabricApi.module("fabric-api-base", project.property("fapi_version").toString()))

    // Juno
    implementation(project(":juno-api"))
    include(project(":juno-api"))

    implementation(project(":juno-utils"))
    include(project(":juno-utils"))

    implementation(project(":juno-opengl"))
    include(project(":juno-opengl"))
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

tasks {
    loom {
        accessWidenerPath = file("src/main/resources/juno.accesswidener")
    }
}

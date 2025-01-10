plugins {
    id("fabric-loom") version "1.9-SNAPSHOT"
}

group = "${property("group_base")}.mc"
version = "${property("juno_mc_version")}"

val jij: Configuration by configurations.creating

configurations {
    api {
        extendsFrom(jij)
    }

    include {
        extendsFrom(jij)
    }
}

dependencies {
    // Fabric
    minecraft("com.mojang:minecraft:${property("minecraft_version")}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

    // Fabric API
    modApi(fabricApi.module("fabric-api-base", "${property("fapi_version")}"))
    include(fabricApi.module("fabric-api-base", "${property("fapi_version")}"))

    // Juno
    jij("org.meteordev:juno-api:${property("juno_api_version")}")
    jij("org.meteordev:juno-utils:${property("juno_utils_version")}")
    jij("org.meteordev:juno-opengl:${property("juno_opengl_version")}")
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

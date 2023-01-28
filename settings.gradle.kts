rootProject.name = "juno"

pluginManagement {
    repositories {
        maven {
            name = "FabricMC"
            setUrl("https://maven.fabricmc.net/")
        }
        mavenCentral()
    }
}

include("juno-api", "juno-opengl", "juno-mc", "example")

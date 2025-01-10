import java.nio.charset.StandardCharsets

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
    }

    repositories {
        maven {
            name = "Meteor - Releases"
            url = uri("https://maven.meteordev.org/releases")
        }

        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.toString()
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        options.encoding = "UTF-8"
    }

    tasks.withType<ProcessResources> {
        filesMatching("fabric.mod.json") {
            expand(
                "juno_api_version" to rootProject.property("juno_api_version"),
                "juno_opengl_version" to rootProject.property("juno_opengl_version"),
                "juno_utils_version" to rootProject.property("juno_utils_version"),
                "juno_mc_version" to rootProject.property("juno_mc_version")
            )
        }
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("java") {
                groupId = "${property("maven_group")}"

                from(components["java"])
            }
        }

        repositories {
            maven {
                setUrl("https://maven.meteordev.org/releases")

                credentials {
                    username = System.getenv("MAVEN_METEOR_ALIAS")
                    password = System.getenv("MAVEN_METEOR_TOKEN")
                }

                authentication {
                    create<BasicAuthentication>("basic")
                }
            }
        }
    }
}

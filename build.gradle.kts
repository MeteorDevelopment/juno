import java.nio.charset.StandardCharsets

subprojects {
    apply {
        plugin("java")
        plugin("maven-publish")
    }

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = StandardCharsets.UTF_8.toString()
    }

    tasks.withType<Javadoc> {
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
        options.encoding = "UTF-8"
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("java") {
                groupId = project.property("group_base").toString()

                from(components["java"])
            }
        }

        repositories {
            maven {
                setUrl("https://maven.meteordev.org/${if (version.toString().endsWith("-SNAPSHOT")) "snapshots" else "releases"}")

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

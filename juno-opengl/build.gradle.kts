plugins {
    id("java-library")
}

group = "${project.property("group_base")}.opengl"

dependencies {
    compileOnly(project(":juno-api"))
    compileOnly("org.joml:joml:${project.property("joml_version")}")

    val lwjglVersion = project.property("lwjgl_version")

    compileOnly("org.lwjgl:lwjgl:$lwjglVersion")
    compileOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

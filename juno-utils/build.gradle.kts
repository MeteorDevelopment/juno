plugins {
    id("java-library")
}

group = "${project.property("group_base")}.utils"

dependencies {
    compileOnly("org.lwjgl:lwjgl:${project.property("lwjgl_version")}")
    compileOnly("org.lwjgl:lwjgl-stb:${project.property("lwjgl_version")}")

    compileOnly("org.joml:joml:${project.property("joml_version")}")

    compileOnly(project(":juno-api"))

    implementation("org.ow2.asm:asm:${project.property("asm_version")}")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_16.toString()
    targetCompatibility = JavaVersion.VERSION_16.toString()
}
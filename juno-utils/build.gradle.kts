plugins {
    id("java-library")
}

group = "${property("group_base")}.utils"
version = "${property("juno_utils_build_version")}"

dependencies {
    // Juno
    compileOnly("org.meteordev:juno-api:${property("juno_api_version")}")

    // LWJGL
    implementation("org.lwjgl:lwjgl:${property("lwjgl_version")}")

    // JOML
    implementation("org.joml:joml:${property("joml_version")}")

    // ASM
    implementation("org.ow2.asm:asm:${property("asm_version")}")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}
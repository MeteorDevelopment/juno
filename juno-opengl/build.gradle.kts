plugins {
    id("java-library")
}

group = "${property("group_base")}.opengl"
version = "${property("juno_opengl_build_version")}"

dependencies {
    // Juno
    compileOnly("org.meteordev:juno-api:${property("juno_api_version")}")

    // LWJGL
    implementation("org.lwjgl:lwjgl:${property("lwjgl_version")}")
    implementation("org.lwjgl:lwjgl-opengl:${property("lwjgl_version")}")

    // GLSL Transformer
    implementation("io.github.douira:glsl-transformer:${property("glsl_transformer_version")}")

    // Annotations
    compileOnly("org.jetbrains:annotations:${property("annotations_version")}")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

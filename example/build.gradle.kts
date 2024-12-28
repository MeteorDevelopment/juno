plugins {
    id("application")
}

group = "${project.property("group_base")}.example"

val mainClassName = "$group.Example"

val lwjglNatives = Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else
                "natives-linux"
        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }                ->
            "natives-macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"
        arrayOf("Windows").any { name.startsWith(it) }                           ->
            if (arch.contains("64"))
                "natives-windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "natives-windows-x86"
        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
}

dependencies {
    implementation(project(":juno-api"))
    implementation(project(":juno-utils"))
    implementation(project(":juno-opengl"))

    implementation("org.joml:joml:${project.property("joml_version")}")

    // LWJGL

    implementation(platform("org.lwjgl:lwjgl-bom:${project.property("lwjgl_version")}"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")

    runtimeOnly("org.lwjgl:lwjgl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb::$lwjglNatives")
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    manifest {
        attributes("Main-Class" to mainClassName)
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

tasks.withType<JavaCompile> {
    // Idk why Gradle didn't figure this out automatically but oh well
    dependsOn(":juno-api:build")
    dependsOn(":juno-utils:build")
    dependsOn(":juno-opengl:build")
}

tasks {
    sourcesJar {
        dependsOn(":juno-api:jar")
        dependsOn(":juno-utils:jar")
        dependsOn(":juno-opengl:jar")
    }
}

tasks.withType<PublishToMavenRepository> {
    enabled = false
}

tasks.withType<PublishToMavenLocal> {
    enabled = false
}

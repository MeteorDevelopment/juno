plugins {
    id("java-library")
}

group = "${property("group_base")}.api"
version = "${property("juno_api_version")}"

dependencies {
    // Annotations
    compileOnly("org.jetbrains:annotations:${property("annotations_version")}")
}

tasks.withType<JavaCompile> {
    sourceCompatibility = JavaVersion.VERSION_17.toString()
    targetCompatibility = JavaVersion.VERSION_17.toString()
}

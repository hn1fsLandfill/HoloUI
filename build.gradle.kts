// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

gradle.projectsEvaluated {
    tasks.withType<JavaCompile>() {
        options.compilerArgs.add("-Xbootclasspath:libs/framework.jar:libs/services.jar hhh")
        options.bootstrapClasspath = files("./libs/framework.jar")
    }
}
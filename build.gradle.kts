buildscript {

    // Set project ext values as the workaround to uniform all values that can not set in BuildConfig,
    // because buildscript can not read BuildConfig fields from the script that will be compiled
    // based on buildscript
    project.extra["kotlinVersion"] = "1.3.31"
    project.extra["agpVersion"] = "3.4.1"
    project.extra["brpVersion"] = "0.9.1"

    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${project.extra["agpVersion"]}")
        classpath("com.novoda:bintray-release:${project.extra["brpVersion"]}")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
}

task("clean") {
    delete(rootProject.buildDir)
}
buildscript {

    // Set project ext values as the workaround to collect all values that can't be set in buildSrc,
    // because buildscript can not read anything from the scripts(buildSrc) that will be compiled
    // based on this buildscript
    project.extra["kotlinVersion"] = "1.3.61"
    project.extra["agpVersion"] = "3.5.2"
    project.extra["brpVersion"] = "0.9.1"

    repositories {
        google()
        jcenter()
        maven {
            setUrl("http://dl.bintray.com/steppschuh/Markdown-Generator")
        }
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

plugins {
    id("me.2bab.bro.build.release")
}

task("clean") {
    delete(rootProject.buildDir)
}
buildscript {
    repositories {
        google()
        jcenter()
        mavenLocal()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.4.1")
        classpath("com.novoda:bintray-release:0.9.1")
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
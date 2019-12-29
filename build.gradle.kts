import me.xx2bab.bro.build.BuildConfig.Versions.broDevVersion

buildscript {

    // Set project ext values as the workaround to collect all values that can't be set in buildSrc,
    // because buildscript can not read anything from the scripts(buildSrc) that will be compiled
    // based on this buildscript
    project.extra["kotlinVersion"] = "1.3.61"
    project.extra["agpVersion"] = "3.5.3"
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
        classpath(kotlin("gradle-plugin", version = project.extra["kotlinVersion"].toString()))
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
    configPublish(this)
}

plugins {
    id("me.2bab.bro.build.release")
}

task("clean") {
    delete(rootProject.buildDir)
}

fun configPublish(p: Project) {
    if(p.name == "bro-parent") {
        return
    }

    p.group = "me.2bab"
    p.version = broDevVersion

    p.apply(plugin = "com.novoda.bintray-release")

    val properties = java.util.Properties()
    val localPropertiesFile = p.rootProject.file("local.properties")
    var bintrayUserName = ""
    var bintrayApiKey = ""
    if (localPropertiesFile.exists()) {
        properties.load(localPropertiesFile.inputStream())
        bintrayUserName = properties.getProperty("bintray.user")
        bintrayApiKey = properties.getProperty("bintray.apikey")
    } else {
        bintrayUserName = System.getenv("BINTRAY_USER")
        bintrayApiKey = System.getenv("BINTRAY_APIKEY")
    }

    require(!(bintrayUserName.isEmpty() || bintrayApiKey.isEmpty())) {
        "Please set user and apiKey for Bintray uploading."
    }

    p.configure<com.novoda.gradle.release.PublishExtension> {
        userOrg = "2bab"
        groupId = p.group as String
        artifactId = p.properties["ARTIFACT_ID"] as String
        publishVersion = broDevVersion
        desc = "Modularization Solution of Android"
        website = "https://github.com/2BAB/Bro"
        bintrayUser = bintrayUserName
        bintrayKey = bintrayApiKey
    }
}
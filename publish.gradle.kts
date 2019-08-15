import com.novoda.gradle.release.PublishExtension
import me.xx2bab.bro.build.BuildConfig.Versions.broDevVersion
import java.util.*

// Redefine buildscript again, it's a workaround to make configuration available in a separated
// gradle(kotlin) file, or you will get the error that "Could not find PublishExtension class"
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${rootProject.extra["agpVersion"]}")
        classpath("com.novoda:bintray-release:${rootProject.extra["brpVersion"]}")
    }
}

project.group = "me.2bab"
project.version = broDevVersion

apply(plugin = "com.novoda.bintray-release")

val properties = Properties()
properties.load(project.rootProject.file("local.properties").inputStream())

configure<PublishExtension> {
    userOrg = "2bab"
    groupId = project.group as String
    artifactId = project.properties["ARTIFACT_ID"] as String
    publishVersion = broDevVersion
    desc = "Modularization Solution of Android"
    website = "https://github.com/2BAB/Bro"
    bintrayUser = properties.getProperty("bintray.user")
    bintrayKey = properties.getProperty("bintray.apikey")
}
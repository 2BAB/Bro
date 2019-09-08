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
val localPropertiesFile = project.rootProject.file("local.properties")
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

configure<PublishExtension> {
    userOrg = "2bab"
    groupId = project.group as String
    artifactId = project.properties["ARTIFACT_ID"] as String
    publishVersion = broDevVersion
    desc = "Modularization Solution of Android"
    website = "https://github.com/2BAB/Bro"
    bintrayUser = bintrayUserName
    bintrayKey = bintrayApiKey
}
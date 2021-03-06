
import me.xx2bab.bro.build.BuildConfig

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {

    compileSdkVersion(BuildConfig.Versions.compileSdkVersion)

    defaultConfig {
        minSdkVersion(BuildConfig.Versions.minSdkVersion)
        targetSdkVersion(BuildConfig.Versions.targetSdkVersion)
        versionCode = 2
        versionName = BuildConfig.Versions.broDevVersion
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            //isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android.txt"))
            proguardFiles(file("proguard-rules.pro"))
        }
    }

    lintOptions {
        isAbortOnError = false
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(BuildConfig.Deps.supportAnno)
    implementation(kotlin("stdlib-jdk8"))
    if (project.hasProperty("broPublish")) {
        api(BuildConfig.Deps.broAnnotationsDev)
        api(BuildConfig.Deps.broCommonDev)
    } else {
        implementation(project(":bro-annotations"))
        implementation(project(":bro-common"))
    }
    testImplementation(BuildConfig.Deps.junit)
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}

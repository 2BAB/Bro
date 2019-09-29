package me.xx2bab.bro.sample.build

import me.xx2bab.bro.sample.build.Config.Versions.broDevVersion
import org.gradle.api.JavaVersion

object Config {

    object Versions {
        const val compileSdkVersion = 28
        const val minSdkVersion = 15
        const val targetSdkVersion = 28

        const val broDevVersion = "1.1.0-SNAPSHOT"

        val broSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val broTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        const val broCommonDev = "me.2bab:bro-common:$broDevVersion"
        const val broAnnotationsDev = "me.2bab:bro-annotations:$broDevVersion"
    }

}
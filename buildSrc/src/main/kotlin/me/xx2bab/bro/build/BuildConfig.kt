package me.xx2bab.bro.build

import me.xx2bab.bro.build.BuildConfig.Versions.broDevVersion
import org.gradle.api.JavaVersion

object BuildConfig {

    object Versions {
        const val compileSdkVersion = 28
        const val buildToolsVersion = "28.0.3"
        const val minSdkVersion = 15
        const val targetSdkVersion = 28

        const val supportLibVersion = "28.0.0"
        const val kotlin = "1.3.31"
        const val broDevVersion = "0.10.13"
        const val broLatestReleaseVersion = "0.10.13"
        val broSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val broTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        const val androidGradlePlugin = "com.android.tools.build:gradle:3.4.1"
        const val broCommonDev = "me.xx2bab.bro:bro-common:$broDevVersion"

        const val supportAnnotations = "com.android.support:support-annotations:${Versions.supportLibVersion}"
        const val supportAppcompatV7 = "com.android.support:appcompat-v7:${Versions.supportLibVersion}"
    }

}
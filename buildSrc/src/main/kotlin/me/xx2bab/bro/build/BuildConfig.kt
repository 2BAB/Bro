package me.xx2bab.bro.build

import me.xx2bab.bro.build.BuildConfig.Versions.broDevVersion
import org.gradle.api.JavaVersion

object BuildConfig {

    object Versions {
        const val compileSdkVersion = 28
        const val buildToolsVersion = "28.0.3"
        const val minSdkVersion = 15
        const val targetSdkVersion = 28

        const val kotlin = "1.3.31"

        const val broDevVersion = "0.10.13"
        const val broLatestReleaseVersion = "0.10.13"

        val broSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val broTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        const val androidGradlePlugin = "com.android.tools.build:gradle:3.4.1"
        const val broCommonDev = "me.xx2bab.bro:bro-common:$broDevVersion"
        const val broAnnotationsDev = "me.xx2bab.bro:bro-annotations:$broDevVersion"
        const val javapoet = "com.squareup:javapoet:1.11.1"
        const val fastjson = "com.alibaba:fastjson:1.1.71.android"
    }

}
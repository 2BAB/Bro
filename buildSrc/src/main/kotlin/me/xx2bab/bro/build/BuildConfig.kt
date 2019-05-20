package me.xx2bab.bro.build

import me.xx2bab.bro.build.BuildConfig.Versions.broDevVersion
import org.gradle.api.JavaVersion

object BuildConfig {

    object Versions {
        const val compileSdkVersion = 28
        const val minSdkVersion = 15
        const val targetSdkVersion = 28

        const val broDevVersion = "0.10.14-SNAPSHOT"

        val broSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val broTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        const val broCommonDev = "me.2bab:bro-common:$broDevVersion"
        const val broAnnotationsDev = "me.2bab:bro-annotations:$broDevVersion"
        const val javapoet = "com.squareup:javapoet:1.11.1"
        const val fastjson = "com.alibaba:fastjson:1.1.71.android"
    }

}
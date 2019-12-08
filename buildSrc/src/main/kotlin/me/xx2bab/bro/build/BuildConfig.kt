package me.xx2bab.bro.build

import me.xx2bab.bro.build.BuildConfig.Versions.broDevVersion
import org.gradle.api.JavaVersion

object BuildConfig {

    object Versions {
        const val compileSdkVersion = 28
        const val minSdkVersion = 15
        const val targetSdkVersion = 28

        const val broDevVersion = "1.2.0.3-SNAPSHOT"

        val broSourceCompatibilityVersion = JavaVersion.VERSION_1_7
        val broTargetCompatibilityVersion = JavaVersion.VERSION_1_7
    }

    object Deps {
        const val broCommonDev = "me.2bab:bro-common:$broDevVersion"
        const val broAnnotationsDev = "me.2bab:bro-annotations:$broDevVersion"
        const val androidRuntime = "com.google.android:android:4.1.1.4"
        const val javapoet = "com.squareup:javapoet:1.11.1"
        const val fastjson = "com.alibaba:fastjson:1.1.71.android"
        const val orgJson = "org.json:json:20160212"
        const val supportAnno = "com.android.support:support-annotations:28.0.0"
        const val markdownGenerator = "net.steppschuh.markdowngenerator:markdowngenerator:1.3.1.1"
        const val junit = "junit:junit:4.12"
    }

}
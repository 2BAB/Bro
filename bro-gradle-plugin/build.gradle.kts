import me.xx2bab.bro.build.BuildConfig

plugins {
    id("maven")
    id("org.jetbrains.kotlin.jvm") version ("1.3.41")
}

configurations.all {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jre7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jre8")
}

apply(rootProject.file("publish.gradle.kts"))

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:${rootProject.extra["agpVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${rootProject.extra["kotlinVersion"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${rootProject.extra["kotlinVersion"]}")
    implementation("org.javassist:javassist:3.22.0-GA")
    implementation(BuildConfig.Deps.fastjson)
    if (project.hasProperty("broPublish")) {
        implementation(BuildConfig.Deps.broAnnotationsDev)
        implementation(BuildConfig.Deps.broCommonDev)
    } else {
        implementation(project(":bro-annotations"))
        implementation(project(":bro-common"))
    }
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}
import me.xx2bab.bro.build.BuildConfig

plugins {
    id("maven")
    kotlin("jvm")
}

configurations.all {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jre7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jre8")
}

dependencies {
    implementation(gradleApi())
    implementation("com.android.tools.build:gradle:${rootProject.extra["agpVersion"]}")
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.javassist:javassist:3.22.0-GA")
    implementation(BuildConfig.Deps.fastjson)
    compileOnly(BuildConfig.Deps.markdownGenerator)

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
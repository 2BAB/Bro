import me.xx2bab.bro.build.BuildConfig

buildscript {
    project.extra["kotlin_version"] = "1.3.31"

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${extra["kotlin_version"]}")
    }

    repositories {
        jcenter()
        mavenCentral()
    }
}

plugins {
    id("maven")
    id("org.jetbrains.kotlin.jvm") version ("1.3.31")
}

configurations.all {
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jre7")
    exclude("org.jetbrains.kotlin", "kotlin-stdlib-jre8")
}

apply(rootProject.file("publish.gradle"))
apply(rootProject.file("common.gradle"))

dependencies {
    implementation(gradleApi())
    implementation(BuildConfig.Deps.androidGradlePlugin)
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${extra["kotlin_version"]}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${extra["kotlin_version"]}")
    implementation("org.javassist:javassist:3.22.0-GA")
    if (project.hasProperty("broPublish")) {
        implementation(BuildConfig.Deps.broCommonDev)
    } else {
        implementation(project(":bro-common"))
    }
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}
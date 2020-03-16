import me.xx2bab.bro.build.BuildConfig.Deps
import me.xx2bab.bro.build.BuildConfig.Versions

plugins {
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
    implementation(Deps.fastjson)
    compileOnly(Deps.markdownGenerator)

    if (project.hasProperty("broPublish")) {
        implementation(Deps.broAnnotationsDev)
        implementation(Deps.broCommonDev)
    } else {
        implementation(project(":bro-annotations"))
        implementation(project(":bro-common"))
    }
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
}

java {
    sourceCompatibility = Versions.broSourceCompatibilityVersion
    targetCompatibility = Versions.broTargetCompatibilityVersion
}
import me.xx2bab.bro.build.BuildConfig.Deps
import me.xx2bab.bro.build.BuildConfig.Versions

plugins {
    kotlin("jvm")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    compileOnly(Deps.androidRuntime)
    implementation(Deps.javapoet)
    implementation(Deps.fastjson)
    implementation(Deps.orgJson)
    implementation(Deps.markdownGenerator)
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    if (project.hasProperty("broPublish")) {
        implementation(Deps.broAnnotationsDev)
        implementation(Deps.broCommonDev)
    } else {
        implementation(project(":bro-annotations"))
        implementation(project(":bro-common"))
    }
}

java {
    sourceCompatibility = Versions.broSourceCompatibilityVersion
    targetCompatibility = Versions.broTargetCompatibilityVersion
}

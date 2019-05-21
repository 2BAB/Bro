import me.xx2bab.bro.build.BuildConfig

plugins {
    id("java-library")
}
apply(rootProject.file("publish.gradle.kts"))

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))

    implementation(BuildConfig.Deps.javapoet)
    implementation(BuildConfig.Deps.fastjson)
    implementation(BuildConfig.Deps.orgJson)

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

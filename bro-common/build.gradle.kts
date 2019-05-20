import me.xx2bab.bro.build.BuildConfig

plugins {
    id("java-library")
}
apply(rootProject.file("publish.gradle.kts"))

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(BuildConfig.Deps.fastjson)
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}

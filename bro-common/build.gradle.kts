import me.xx2bab.bro.build.BuildConfig

plugins {
    id("java-library")
}
apply(rootProject.file("publish.gradle.kts"))

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    compileOnly(BuildConfig.Deps.androidRuntime)
    compileOnly(BuildConfig.Deps.orgJson)
    api(BuildConfig.Deps.supportAnno)
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}

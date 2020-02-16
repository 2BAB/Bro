import me.xx2bab.bro.build.BuildConfig

plugins {
    id("kotlin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    compileOnly(BuildConfig.Deps.androidRuntime)
    compileOnly(BuildConfig.Deps.orgJson)
    api(BuildConfig.Deps.supportAnno)
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}

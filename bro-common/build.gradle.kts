import me.xx2bab.bro.build.BuildConfig.Deps
import me.xx2bab.bro.build.BuildConfig.Versions

plugins {
    id("kotlin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    compileOnly(Deps.androidRuntime)
    compileOnly(Deps.orgJson)
    api(Deps.supportAnno)
    testImplementation(Deps.junit)
    testImplementation(Deps.mockito)
}

java {
    sourceCompatibility = Versions.broSourceCompatibilityVersion
    targetCompatibility = Versions.broTargetCompatibilityVersion
}

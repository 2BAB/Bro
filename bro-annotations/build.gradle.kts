import me.xx2bab.bro.build.BuildConfig

plugins {
    id("kotlin")
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to arrayOf("*.jar"))))
    implementation(kotlin("stdlib-jdk8"))
    if (project.hasProperty("broPublish")) {
        api(BuildConfig.Deps.broCommonDev)
    } else {
        implementation(project(":bro-common"))
    }
}

java {
    sourceCompatibility = BuildConfig.Versions.broSourceCompatibilityVersion
    targetCompatibility = BuildConfig.Versions.broTargetCompatibilityVersion
}

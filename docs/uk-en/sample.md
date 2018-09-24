# Sample

Bro offers a simple project named `sample` to show how to use functions of Bro in this project. For making it easier to demonstrate, `sample` is combined by single project and multi Module (sample also can be divided into multi Repo). Among them, the app is the major project, and the others are individual service modules.

```
// package the sample project
./gradlew assembleDebug

// debug bro-complier , bro-gradle-plugin
./gradlew assembleDebug -Dorg.gradle.debug=true --no-daemon

```

# Sample

Bro provides a simple project named `sample` to show how itself works. For making it easier to demonstrate, `sample` is combined by single project with multi feature modules (modules also can be separated into individual repository). Among them

- The `/app` is the main module which provides setup stuffs and stitch everything together
- The `/app.xxx` modules are features module that also provides API sets for each other
- The `/base.common` module is for API interface and data-model that comes from features modules

```
// Build the sample project
./gradlew clean assembleDebug

// debug bro-complier, bro-gradle-plugin
./gradlew assembleDebug -Dorg.gradle.debug=true --no-daemon
```

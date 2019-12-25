# Sample

Bro provides a simple project named `sample` to show how itself works. For making it easier to demonstrate, `sample` is combined by single project with multi feature modules (modules also can be separated into individual repository). Among them:

- The `/app` is the main module which provides setup stuffs and stitch everything together
- The `/app.xxx` modules are feature modules that also provides API sets for each other
- The `/base.common` module is for API interface and data-model that comes from features modules

``` bash
// Build the sample project
$ ./gradlew clean assembleDebug

// Debug bro-complier, bro-gradle-plugin with a Java based project and annotationProcessor()
$ ./gradlew clean assembleDebug --no-daemon -Dorg.gradle.debug=true 

// Debug bro-complier, bro-gradle-plugin with a Kotlin based project and kapt()
$ ./gradlew clean assembleDebug --no-daemon -Dorg.gradle.debug=true -Dkotlin.compiler.execution.strategy="in-process" -Dkotlin.daemon.jvm.options="-Xdebug,-Xrunjdwp:transport=dt_socket\,address=5005\,server=y\,suspend=n"
```

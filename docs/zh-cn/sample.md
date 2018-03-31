Bro 提供了一个简易的 sample 工程来演示如何使用 Bro 的各个功能。为了方便演示，目前 sample 工程是以单工程多 Module 的形式进行组织的（sample 工程拆分成多 Repo 也是完全支持的）。其中 app 为主工程，其余为各个业务模块。

``` shell
// 打包 sample 工程
./gradlew assembleDebug

// 调试 bro-compiler 、bro-gradle-plugin
./gradlew assembleDebug -Dorg.gradle.debug=true --no-daemon
```




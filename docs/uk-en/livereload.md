本地热部署，类似于插件化的部署形式，将模块单独打包成 apk 并进行动态加载。旨在模块化更加彻底，调试成本更低，在模块多的时候甚至可以提升十倍以上的打包速度。

不同于普通插件化做法的是 Bro 的热部署以稳定和兼容性为前提，打出的包会有资源冗余的不足，以及其他一些非完整插件化的支持。

## 用法用例

### 配置

1. 在宿主工程中，打开 Bro 热部署的开关；

    ``` gradle
    bro {
        isPlugadgetEnabled true 
    }
    ```

2. 在需要热部署的模块中，声明模块的包名（这是因为模块作为 library 不需要 applicationId，而作为 application 打包时需要）：

    ``` gradle
    bro {
        packageName 'com.example.app.home'
    }
    ```

### 打包

1. 先打包宿主并安装到手机，跟你平时打包并没有任何区别，此时打出来的包是包含了所有模块的整包：

    ``` shell
    ./gradlew clean installDebug
    ```
    
2.  当改动了某个模块需要调试时，单独打包该模块：

    ``` shell
    ./gradlew clean assembleDebug -PbroPlugadget=app.settings -PbroAppOutputs=/path/to/your/app/build/bro/debug_bro_app_outputs.zip
    ```
    其中：
    - `broPlugadget` ，必传参数，用以明确指定该模块为 **Plugadget** 模式，即开启该模块的插件打包模式；
    - `broAppOutputs` ，必传参数，用以指定依赖的宿主工程的打包产物，根据宿主打包时不同的 variant 会有不同的 .zip 文件；
    - 该命令打包完成后，产物位于 `module/build/bro/com.package.name.plugadget`，并且会通过 adb 自动将该模块产物传输至手机中，之后重启 App 即可生效；

### 特性

目前支持：

- 模块自身的任意 Java 层代码变更；
- 模块自身的除 Activity 动画以外的任何资源变更；

不支持：

- [未来也不支持] manifest 的任何变更，包括新增的 Activity 等；
- [未来也不支持] 模块依赖的 Native Lib 更新（.so）；
- [未来也不支持] 模块依赖的 Java Lib 及其资源更新（.jar .aar）；
- [未来也不支持] 通过 getApplicationContext().getResources() 获取插件增加或修改的资源，但可以通过 BroModule 提供的 getResources() 来支持这一场景；
- [未来可能会支持] 模块本身打出的 apk 就存在多 dex，这种情况 99.99% 是由于模块并没有合理地进行划分导致的，故目前并不打算支持；

### 注意事项

- 由于本地热部署目前的实现会存在插件包中资源的冗余，并不建议在线上使用（当然，本来就是朝着本地热部署 + 高效稳定的目标去做的，所以方案有所取舍，用空间换复杂度）
- 由于读取外置存储中的插件需要「存储权限」，故在 Android 6.0 及以上的系统中，第一次安装的 App 需要获取权限并完全 force stop（防止 ClassLoader 缓存）后才会能使本地热部署生效，注意很多系统仅在任务管理器中划走你的 App 是不会杀死进程的；
- 为了更好的隔离和插件打包速度，目前强校验模块配置的包名与模块代码包名的一致性，只有该包名前缀的类会打入模块插件中；
- 若主工程重新打包，则所有插件必须重打并删除 SD 卡内本 App 已有的插件（位于 /sdcard/bro/${com.app.package.name}/），后续考虑提供一个删除插件的 task；
- 暂时不建议超过一个以上的插件同时进行调试，虽然理论上是支持的，但是实际场景中打多个插件耗费的时间和打个整包可能就相差无几了，Bro 也不对这块做太多的测试和支持；


## 最佳实践

### 避免 SNAPSHOT 陷阱：

一般地，多模块项目中在新版本发布前，宿主总会采用 SNAPSHOT 的形式依赖各个业务模块。这种时候，要是有人不小心发布一个有问题的 SNAPSHOT，往往就会导致大家都打不出包。本地热部署就可以一定程度上避免这个问题，你只要每天早上打一个基线包，之后一天都可以只打包和调试自己的模块插件（当然，前提是你没有跟其他模块今天的新代码发生业务往来）。

### 约束与自由

目前，开启本地热部署的模块，会强校验代码包名（未来考虑加入资源前缀的强校验），打包模块插件时只会把该包名下的代码打入到 Plugadget 包中。虽然这样一定程度上减少了模块的自由程度（该模块根包名必须一致），但带来了诸多的好处：

- 独立的代码命名空间，是未来更多隔离措施的基础；
- 有利于线上问题的排查，看一眼包名就可以找到对应的模块和负责人；
- 减少不小心出现的类重复问题——在模块化的架构中，每个模块都是独立打包集成的（以 aar 的形式），隔离的打包环境如果加上重复的包名前缀就很可能出现同名类的打包错误；

约束其实是为了带来更多自由及稳定，如果可以，请在模块建立的那刻起就做好包名独立及资源前缀的独立，不论是否开启了本地热部署。

### 自动化开启本地热部署

控制是否开启热部署有两个地方，第一个是宿主工程的 `build.gradle` 配置：

``` gradle
bro {
    isPlugadgetEnabled true 
}
```

第二个地方是插件打包时，通过 `-PbroPlugadget` 打开插件打包的开关。

关于插件打包，其实已经较为自动化了，不同的打包环境下用不同的命令即可。而宿主工程是否打开则需要一个动态配置，比如想在 Debug 包打开，Release 包关闭：

```gradle
def isDebug() {
    if(gradle.startParameter.getTaskNames().size() == 0) { // for clean etc..
        return true
    }

    List<String> tasks = gradle.startParameter.getTaskNames();
    for(String s : tasks) {
        if(s.contains("Debug")) {
            return true;
        }
    }

    return false;
}

bro {
    isPlugadgetEnabled isDebug() 
}
```

如上可以通过自定义一个判断打包 Task Name 的函数来解决这类问题。



## 概念解释

Bro 提供的模块化，分解为三个部分；

1. **Application** (Host，等同与后续所述的「主工程」「壳工程」「主 Bundle」)：即一般工程中声明了 `apply plugin: 'com.android.application'` 的主模块，该模块一般用于打包 Apk 和做 Application 入口的配置与初始化；
2. **Library** (Business Bundle & SDK，等同于后续所述的「业务模块」「业务 Bundle」及一些「Java Bundle」)：即一般工程中声明了 `apply plugin: 'com.android.library'` 的业务模块（抑或 SDK），可独立打包独立输出 AAR，所有业务模块间除了特殊情况没有相互依赖，一般只对 Common 模块有依赖；**此外，当 Library 作为本地热部署的模块打包时，Bro 称之为 Plugadget，是 Library 的一个变种；**
3. **Common** (Interface & Model & Utils)：Common 首先是一个特殊的 Library，被除自己外的所有的业务模块依赖，内部存放所有业务的对外 Interface，虽然大部分 Android 模块化的库、文章没有明确提到 Common 是一个必要的 Library，但是不管是阿里云、微信等大公司的模块化方案均不成文地拥有这样一个接口集合模块；

关于模块化存在的形式，分成两种：

1. 单工程多模块（MonoRepo）：如 Sample 工程，主工程源码依赖了业务模块（`implementation project(:bizmodule)`），笔者认为适合比较小的工程做简单划分，但实际上 Google 等大公司的诸多开源项目都采用了这种模式；
2. 多工程多模块：像各种插件化框架做到的那样，每个 Module 都是一个单独的 Repo 单独打包发布（AAR），主工程二进制依赖业务（`implementation 'com.example.appname.bizmodule'`），但依赖主工程进行打包调试（APK），诸如 Alibaba 等公司较多使用此模式；

## 初始化

### 引入 Bro

在每个模块的 build.gradle 中配置 Bro 的插件：

``` gradle
buildscript {
    repositories {
        ...
        jcenter()
    }
    dependencies {
        ...
        classpath("me.2bab.bro:bro-gradle-plugin:1.0.0")
    }
}
```

之后引入 bro 插件：

``` gradle
apply plugin: 'me.2bab.bro'
```

另外，在模块依赖中引入 Bro 的运行时和编译时依赖：

``` gradle
implementation("me.2bab.bro:bro:1.0.0")
annotationProcessor("me.2bab.bro:bro-compiler:1.0.0")
```

### 在 Application 中初始化配置

一般地，我们需要构造一个 `BroBuilder` 对象，然后调用 `Bro.initialize(application, broBuilder);` 进行初始化操作。

``` java
public class App extends Application {
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.init first if it is needed, then init BroPlugadget
        // TODO: Add new BroPlugadget initialization here.
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        initBro();
    }


    private void initBro() {
        IBroInterceptor interceptor = new IBroInterceptor() {

            @Override
            public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
                return false;
            }

            @Override
            public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
                //Log.i("App", properties.toJsonString());
                return false;
            }

            @Override
            public boolean beforeGetApi(Context context, String target, IBroApi api, BroProperties properties) {
                return false;
            }

            @Override
            public boolean beforeGetModule(Context context, String target, IBroModule module, BroProperties properties) {
                return false;
            }
        };
        IBroMonitor monitor = new IBroMonitor() {

            @Override
            public void onActivityRudderException(int errorCode, Builder builder) {

            }

            @Override
            public void onModuleException(int errorCode) {

            }

            @Override
            public void onApiException(int errorCode) {

            }
        };

        BroBuilder broBuilder = new BroBuilder()
                .setMonitor(monitor)
                .setInterceptor(interceptor);

        Bro.initialize(this, broBuilder);
    }
}        
```

上面的代码我们配置了一个自定义的 Monitor 和 Interceptor，一般情况下我们都希望能对找不到某个 API 或无法跳转到某个页面的情况进行监控，或者通过拦截器配合实时更新的在线规则进行页面的切换、Native/Web 的切换等等功能，关于监控和拦截的详细实践，请参阅后续的『监控和拦截』小节。

此外，`BroBuilder` 的其他配置都在下方列出：

``` java
new BroBuilder()
        // 自定义 Activity 的查找器
        .setActivityFinders(activityFinderList) 
        // 自定义 Activity 的转场动画
        .setActivityTransition(R.anim.enterAnim, R.anim.exitAnim) 
        // 自定义 Activity 查找失败时的默认提示页（错误兜底）
        .setDefaultActivity(SampleDefaultActivity.class)
        // 自定义是否输出 Log
        .setLogEnable(false)
        .setMonitor(monitor)
        .setInterceptor(interceptor);
```



## 概念解释

Bro 提供的模块化，分解为三个部分；

1. **Application** (Host，等同与后续所述的「主工程」「壳工程」「主 Bundle」)：即一般工程中声明了 `apply plugin: 'com.android.application'` 的主模块，该模块一般用于打包 Apk 和做 Application 入口的配置与初始化；
2. **Library** (Business Bundle & SDK，等同于后续所述的「业务模块」「业务 Bundle」及一些「Java Bundle」)：即一般工程中声明了 `apply plugin: 'com.android.library'` 的业务模块（抑或 SDK），可独立打包独立输出 AAR，所有业务模块间除了特殊情况没有相互依赖，一般只对 Common 模块有依赖；**此外，当 Library 作为本地热部署的模块打包时，Bro 称之为 Plugadget，是 Library 的一个变种；**
3. **Common** (Interface & Model & Utils)：Common 首先是一个特殊的 Library，被除自己外的所有的业务模块依赖，内部存放所有业务的对外 Interface，虽然大部分 Android 模块化的库、文章没有明确提到 Common 是一个必要的 Library，但是不管是阿里云、微信等大公司的模块化方案均不成文地拥有这样一个接口集合模块；

关于模块化存在的形式，分成两种：

1. 单工程多模块（MonoRepo）：如 Sample 工程，主工程源码依赖了业务模块（`compile project(:bizmodule)`），个人觉得适合比较小的工程做简单划分，但实际上 Google 等大公司的诸多开源项目都采用了这种模式；
2. 多工程多模块：像各种插件化框架做到的那样，每个 Module 都是一个单独的 Repo 单独打包发布（AAR），主工程二进制依赖业务（`compile 'com.example.appname.bizmodule'`），但依赖主工程进行打包调试（APK），国内的中大型的工程较多使用此模式；

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
        classpath 'me.2bab.bro:bro-gradle-plugin:0.13.0'
    }
}
```

之后引入 bro 插件：

``` gradle
apply plugin: 'me.2bab.bro'
```


### Application 中初始化配置

``` java
public class App extends Application {
    
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // MultiDex.init(...) first if it is needed, then init the bro framework
        initBro(base);
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
    }


    private void initBro(Context baseContext) {
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
                .setDefaultActivity(SampleDefaultActivity.class)
                .setMonitor(monitor)
                .setInterceptor(interceptor);

        Bro.initialize(this, broBuilder);
    }
}        
```



# Concepts

The modularity feature provided by Bro is broken down into three parts.

1. **Application** (Host, equals to the [main project], [shell project] and [main Bundle] mentioned later): In general, it means the project which states `apply plugin: 'com.android.application`, and it's used to package APK and declare application's entrance with configuration and initialization;
2. **Library** (Feature Bundle & SDK, equals to [feature module] and [feature Bundle]): In general, it means the project which states ` apply plugin: 'com.android.library' `, and it is used as a specific feature module and can be packaged to `.aar` archive independently. Among all feature modules, they do not depend on each other but only be dependent on Common modules in general; **In addition, When the Library is packaged as a local hot-deployed module, we call it Plugadget in Bro context, which is a kind of mutant for Library;**
3. **Common** (Interface & Model & Utils): Firstly, Common is a kind of special Library, it is dependent on all business modules except itself and store all external Interfaces of feature modules inside. Although most of the Android modular libraries and articles do not explicitly mention that Common is a necessary module, the modularity solution from giant companies such as Aliyun and WeChat have such a common module(s).

## Initialize

### Import Bro

Import Bro plug-in in each module's build.gradle

``` gradle
buildscript {
    repositories {
        ...
        jcenter()
    }
    dependencies {
        ...
        classpath 'me.xx2bab.bro:bro-gradle-plugin:1.2.0'
    }
}
```

Then apply `apply plugin: 'bro'` in each module's build.gradle


### Initialize the configuration in the Application

``` java
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBro();
        ...
    }

    private void initBro() {
        IBroInterceptor interceptor = new IBroInterceptor() {

            @Override
            public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
                return false;
            }

            @Override
            public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
                Log.i("BroProperties", properties.toString());
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
                .setLogEnable(false)
                .setMonitor(monitor)
                .setInterceptor(interceptor);

        Bro.initialize(this, broBuilder);
    }
}
```



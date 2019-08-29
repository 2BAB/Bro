# Concepts

The modularity feature provided by Bro is broken down into three parts.

1. **Application** (Host, equals to the [main project], [shell project] and [main Bundle] mentioned later): In general, it means the project which states `apply plugin: 'com.android.application`, and it's used to package APK and declare application's entrance with configuration and initialization;
2.  **Library** (Feature Bundle & SDK, equals to [feature module] and [feature Bundle]): In general, it means the project which states ` apply plugin: 'com.android.library' `, and it is used as a specific feature module and can be packaged to `.aar` archive independently. Among all feature modules, they do not depend on each other but only be dependent on Common modules in general; **In addition, When the Library is packaged as a local hot-deployed module, we call it Plugadget in Bro context, which is a kind of mutant for Library;**
3. **Common** (Interface & Model & Utils): Firstly, Common is a kind of special Library, it is dependent on all business modules except itself and store all external Interfaces of feature modules inside. Although most of the Android modular libraries and articles do not explicitly mention that Common is a necessary module, the modularity solution from giant companies such as aliyun and WeChat have such a common module(s).

As for the  modularity existence form, there are two kinds:

1. Single project multiple modules: such as Sample project, the source code of the main project relying on the business model（`compile 'com.example.appname.bizmodule'`）, is suitable for relatively small projects to do small projects division;
2. Multi-project multi-modules: each Module is a separate Repo individually packaged release (AAR), as various plug-in frameworks do. The main project binaries depend on the business(`compile 'com.example.appname.bizmodule'`), but it relies on the main project for package debugging (APK). As for medium and large projects, recommend this model:

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
        classpath 'me.xx2bab.bro:bro-gradle-plugin:0.9.99.1'
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
    }

    private void initBro() {
        BroConfig config = new BroConfig.Builder()
                .setDefaultActivity(DefaultActivity.class)
                .setLogEnable(true)
                .build();
        IBroInterceptor interceptor = new IBroInterceptor() {

            @Override
            public boolean onFindActivity(Context context, String target, Intent intent, BroProperties properties) {
                return false;
            }

            @Override
            public boolean onStartActivity(Context context, String target, Intent intent, BroProperties properties) {
                return false;
            }

            @Override
            public boolean onGetApi(Context context, String target, IBroApi api, BroProperties properties) {
                return false;
            }

            @Override
            public boolean onGetModule(Context context, String target, IBroModule module, BroProperties properties) {
                return false;
            }
        };
        IBroMonitor monitor = new IBroMonitor() {

            @Override
            public void onActivityRudderException(int errorCode, ActivityRudder.Builder builder) {

            }

            @Override
            public void onModuleException(int errorCode) {

            }

            @Override
            public void onApiException(int  errorCode) {

            }
        };

        Bro.init(baseContext,
                new BroInfoMapImpl(),
                interceptor,
                monitor,
                config);
     }         
```



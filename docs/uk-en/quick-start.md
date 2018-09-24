# Concepts

The modularity provided by Bro is broken down into three parts.

1. **Application** (Host, equals to the [main project], [shell project] and [main Bundle] mentioned later): In general engineering have ` apply plugin: '. Com. Android application ` main module while are used to package APK and do application entry configuration and initialization;
2.  **Library** (Business Bundle & SDK, equals to「business module」「Business Bundle」and「Java Bundle」 ): In general engineering is done ` apply plugin: 'com. Android. Library' ` business module (or SDK) which is used as a business module and can package and output AAR independently. Among all business module in addition to the special circumstances do not depend on each other and only be dependent on Common modules in general; **In addition, When the Library is packaged as a local hot-deployed module, Bro calls it Plugadget, which is a variant of the Library;**
3. **Common** (API - Interface Center): Common is first a special  Library, and is dependent on all business modules except itself and store all the external Interface of all business inside. Although most of the Android modular libraries and articles do not explicitly mention that Common is a necessary Library, the modules of major companies such as aliyun and WeChat have such an interface module.

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

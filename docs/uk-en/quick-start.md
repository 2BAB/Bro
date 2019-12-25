# Quick Start

## Concepts

The modularity feature provided by Bro is broken down into two parts.

1. **Application** (Host container, AKA main project): In general, it means the project which declares `apply plugin: "com.android.application"`, and it's used to build the final APK and assemble any other modules the app needs.
2. **Library** (Feature Module & Infrastructure SDK): In general, it means the project which states `apply plugin: "com.android.library"`, and it is used as a specific feature module and be packaged into a `.aar` archive independently. Among all library modules, there are three different kinds of separating approaches:
    - `featureA-api` + `featureA-impl`: here featureA is constructed by `-api` and `-impl`, so when any other modules want to call APIs from featureA, they can only depend on `feature-api` without accessing the `impl`. Thus there are no deep couplings in the whole project; other feature modules can mock/replace the featureA easily.
    - `featureB`: here feature B is a mixture of `api` and `impl`, consider the scenario that your exposed `AbstractFeatureB.kt` by `featureB` and other modules  take it as a parent class and extend something like `CCCFeatureB`, `DDDFeatureB`. There is not an indirect way for this scenario to isolate the `AbstractFeatureB.kt`, but that doesn't mean we do everything in this pattern, and you can still create the `BApi.kt` and `BApiImpl.kt` for better isolation inside one module. 
    - `featureC-impl` + `featureD-impl` + ... + `common`: common is a kind of special Library, it is dependent by all feature modules except itself and store all exported Interface & Models of feature modules inside, for the rest of `featureX` modules they just do the implementation of interfaces that placed in `common` module.
    
## Initialization

### Import

- Import Bro plugin on root `build.gradle`:

``` gradle
buildscript {
    repositories {
        ...
        jcenter()
    }
    dependencies {
        ...
        classpath "me.2bab:bro-gradle-plugin:${latestVersion}"
    }
}
```

- Then apply `apply plugin: "bro"` in each module's `build.gradle`.

``` gradle
apply plugin: "com.android.library"
apply plugin: "kotlin-android'
apply plugin: "kotlin-android-extensions"
apply plugin: "kotlin-kapt"
apply plugin: "me.2bab.bro" // Add it here
``` 

- The last step, add runtime and compiler dependencies.

``` gradle
implementation("me.2bab:bro:${latestVersion}")
kapt("me.2bab:bro-compiler:${latestVersion}")
```



### Configuration

In a nutshell, we create a `BroBuilder` passing the implementation of `IBroInterceptor` and `IBroMonitor` along with some other options and call `Bro.initialize(Context, BroBuilder)` to complete initialization. For more details about interceptors and monitors, please refer to [interceptor page](https://2bab.github.io/Bro/#/uk-en/interceptor).

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

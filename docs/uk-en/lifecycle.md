## Module's Lifecycle

## Overall

To get a better modular effect, Bro help define the entry for your module, it's kind of `Application` class to your App. You can set up your module's specific dependencies here just like what you did in `Application#onCreate()`.

## Usage 

### LifeCycle Listeners

``` java
// Implements IBroModule and annotates with @BroModule 
// to expose the module to other modules
@BroModule("LocationModule")
public class LocationModule implements IBroModule {

    @Inject
    Lazy<ISettingsApi> settingsApiByLazy;

    // Declare dependencies that the module will use 
    // in #onCreate(Context context), Bro sorts modules
    // and creates a DAG (Directed acyclic graph), so that
    // dependencies will be initialized before this module
    @Override
    public Set<Class<? extends IBroApi>> getLaunchDependencies() {
        Set<Class<? extends IBroApi>> set = new HashSet<>();
        set.add(ISettingsApi.class);
        return set;
    }

    // This callback will be triggered when Bro.init(...) is being
    // calling, usually it's inside the Application#onCreate()
    @Override
    public void onCreate(Context context) {
        Log.d("ModuleCreates", "LocationExportApplication");
        DaggerLocationAppComponent.create().inject(this);
        int pi = settingsApiByLazy.get().getPi();
        Intent intent = new Intent(context, LocationService.class);
        intent.putExtra("pi", pi);
        context.startService(intent);
    }
    
}
```

## Best Practice

### Handle Module's OWN dependencies

A good modularization app should not put all libs initialization flow into your `Application#onCreate()`. If a 3rd lib is used only in your module, put the initialization flow here inside your `IBroModule` implementation. For example, an encryption libs is only accessible in your module, call `EncryptionSDK.init(context)` here inside the `onCreate(Context context)`. In this way, the supervisor can easily get statistics of the time consuming per module, to see if any of them is overloaded while the App launching. 

### Take Care of Relationship among Modules

If A-Module invokes C-API provided by B-Module, then we call A depends on B in the launch flow. We don't want A gets NPE when calls B, so declare C in `getLaunchDependencies()` will help Bro process the launch order correctly. Check the [API Exposure](https://2bab.github.io/Bro/#/uk-en/api) chapter for more related info about how to bind a `@BroApi` class with `@BroModule`.
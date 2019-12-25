# Interceptor 

## Overall

Bro provides global intercepting on per core function and error monitoring callbacks. 

## Usage

### Initialization

On initialization process of Bro, set the implementation of interceptor and monitor:

``` java
IBroInterceptor interceptor = new IBroInterceptor() {

    @Override
    public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
        // True stands for intercepting and preventing subsequent actions
        // Before looking up, you can replace the target link and 
        // or modify intent objects to set a new Uri here
        return false;
    }

    @Override
    public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
        // The looking-up process and parameter parsing have been completed, 
        // you can now do the "login" interception and other operations here
        // only before navigation (on how to get data of whether login is required 
        // from BroProperties, please refer to the best practice below)
        Log.i("BroProperties", properties.toString());
        return false;
    }

    @Override
    public boolean beforeGetApi(Context context, String target, IBroApi api, BroProperties properties) {
        // This happened after located the target BroApi, 
        // you can replace any instance of that particular API object before return.
        // This is quite a simple solution for data mocking
        return false;
    }

    @Override
    public boolean beforeGetModule(Context context, String target, IBroModule module, BroProperties properties) {
        // This happened after located the BroModule, 
        // you can replace the instance of particular module before return.
        return false;
    }
};

IBroMonitor monitor = new IBroMonitor() {

    @Override
    public void onActivityRudderException(int errorCode, Builder builder) {
        // When it throws an exception on the process of locating an Activity.
    }

    @Override
    public void onModuleException(int errorCode) {
        // When it throws an exception on the process of locating an IBroModule instance.
    }

    @Override
    public void onApiException(int errorCode) {
        // When it throws an exception on the process of locating an IBroApi instance.
    }
};

BroBuilder broBuilder = new BroBuilder()
        .setXxx()
        .setMonitor(monitor)
        .setInterceptor(interceptor);

Bro.initialize(this, broBuilder);
```
### Parameter description

``` java
public class BroProperties {

    public String clazz;
    public String module;
    public Map<String, Map<String, String>> extraAnnotations;
    
}
```

- **clazz**: it is the actual class name of the annotated class.
- **module**: the belonging module of the annotated API or activity.
- **extraAnnotations**: a map contains any other annotations(full class name) except the ones with the prefix of @BroXxx, for the key is the name of particular annotation, and the value is properties of this annotation by a Map\<prop-key, prop-value\>.


## Best practices

### Processing Pipeline

To avoid chaos and keep robust of the app, Bro doesn't provide a subscription in the pipeline way for interceptor and monitor. It means you can not add an interceptor in your feature module, and the only one subscription entry is at the initialization of Bro. You can still provide some intercepting APIs from feature modules, and manage all of them here inside the intercepting methods. A good idea that might be taken is to pipeline these sub-interceptors by your favorite order.

### Activity permission validation

In order to attract new users, many apps do not require log-in on first few pages until some important operations (such as tapping "Favourites", "Member Promo", etc.) then pop up the login dialog.In this case, we create a new annotation named `@RequireLoginSession()` in the a common module, and then add the annotation in the corresponding activities:

``` java
@RequireLoginSession(123)
@RequireMultiValues(value = 1, value1 = "AString", value2 = 12345L, value3 = 'a', value4 = true)
@BroActivity(alias = "broapp://home", module = HomeModule.class)
public class HomeActivity extends AppCompatActivity {
    ...
}
```

You can trigger the login navigation by checking the annotation `@RequireLoginSession()` above:

``` java
@Override
public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
    String loginAnnotation = "me.xx2bab.bro.sample.common.annotation.RequireLoginSession";
    if (properties.extraAnnotations.containsKey(loginAnnotation)) {
        Intent i = new Intent(context, LoginActivty.class);
        i.putExtra("orginalTarget", intent);
        startActivity(i);
        return true;
    }
    return false;
}
```

### First Aid with Online Configuration

Generally, we can preset an online configuration check inside the interceptor. When we found a bug that caused the crash in the particular page but we could not fix it immediately, we can block the access of it by modifying the configuration, as well as displaying a customized error page. This is quite a widely used and effective solution to do the "First Aid" of the app; it buys more time for real bug fixes and avoids user loss.

On the other way, when a Native page has a corresponding Web/ReactNative/Weex version, we can also do a graceful degradation from Native to other versions by pushing the configuring when a Bug occurs. For example, a redemption Activity usually has an alternative webpage that can accomplish the same operations.

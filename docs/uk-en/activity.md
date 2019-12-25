# Activity Export

## Overall

`@BroActivity(alias: String, module: IBroModule)` provides Activity export function. As you know, feature modules don't depend on each other directly, so if `FeatureA` wants to navigate to `FeatureB` via `startActivity(intent: Intent)`, we can not just pass the target Activity Class into. Now with Bro, you can pass the `Uri` or `Alias` to do the navigation.

## Usage

### Initialization

By default, you don't need to do anything for Initialization on Activity export function. However, there are some advanced options you can choose for better fitting your App. 

While initializing Bro, you can pass the implementations of `IActivityFinder` into for supporting different kinds of 

``` java
List<IBroActivityFinder> finders = new ArrayList<>();
finders.add(new AnnoActivityFinder());
finders.add(new PackageManagerActivityFinder());
        
BroBuilder broBuilder = new BroBuilder()
        .setDefaultActivity(SampleDefaultActivity.class)
        .setActivityFinders(finders)
        .setLogEnable(false)
        .setMonitor(monitor)
        .setInterceptor(interceptor);

Bro.initialize(this, broBuilder);
```

Bro iterates the list of finders in the process of looking up the target Activity. Once a finder located the target Activity, it executes the navigation and skips the rest of all.

### Declaration of Pages that need to be exported

Two approaches are supported by default so far. `IBroActivityFinder` can be extended for a broader range of support.

- Annotation: ``@BroActivity(String URI)`` Passing  ``URI`` as a parameter for the annotation for the Activity needing to be exposed.

``` java
@RequireMultiValues(value = 1, value1 = "AString", value2 = 12345L, value3 = 'a', value4 = true)
@BroActivity(alias = "broapp://settings", module = SettingsModule.class)
public class SettingsActivity extends AppCompatActivity {
    ...
}
```

- AndroidManifest: compatible with Android native router support.

```xml
<activity
    android:name="me.xx2bab.bro.sample.profile.SettingsActivity"
    android:theme="@style/Theme.AppCompat.Light">
    <intent-filter>
        <category android:name="android.intent.category.DEFAULT"/>
        <action android:name="android.intent.action.VIEW"/>
        <data
            android:host="settings"
            android:scheme="broapp" />
    </intent-filter>
</activity>
```

### Start exported Activity

To the `HomeActivity` with `BroActivity` Annotation:

``` java
Bundle bundle = new Bundle();
bundle.putString("bundleparam", "123");
Bro.get().startActivityFrom(this)
        .withExtras(bundle)
        .toUri(Uri.parse("broapp://home?urlparam=233"));        
```

To the `HomeActivity` with Manifest declaration:

``` java
Bro.get().startActivityFrom(this)
        .toUri(Uri.parse("broapp://settings"));
```


More APIs and how to get the result of navigations:

``` java
ActivityNaviProcessor processor = Bro.get()
        .startActivityFrom(this) // current Context
        .withExtras(bundle) // with Extras
        .withFlags(flags) //   with Flags
        .withCategory(category) // add category
        .forResult(resultCode) // for parameters of onActivityResult  
        .dryRun() //Activity will not start if this api is applied, which usualy is to check the existence of the target page without turning to it
        .toUri(Uri.parse("broapp://home?urlparam=233"));   // target Uri

processor.isIntentValidate(); // whether Activity is valid 
processor.isIntercepted(); // whether it is intercepted by IBroInterceptor
processor.getIntent(); // get the Intent for start Activity
processor.getBuilder(); // get the builder for start Activity
```

## Best Practice

### Annotation Declaration is Preferred

It's recommended to use `@BroActivity` to export an `Activity`, although Bro offers `IActivityFinder` and `setActivityFinders` as customized options. The reasons are listed as follows:

- Manifest declaration is strict to support customization for extra properties.
- Manifest declaration may disclose some unnecessary information easily while you can have the `DexProtector` tool for hiding Dex files to avoid Activity export data being leaked.

As a matter of fact, `AnnoActivityFinder` is able to take care of many things, `PackageManagerActivityFinder` is not mandatory. However, taking into account the actual situation, 

- Some apps integrated native manifest navigation already
- Some 3rd SDKs may provide the Activity with implicit intent like a WebView Activity accepts all "http" prefixing paths

To make these users migrate to Bro seamlessly, `PackageManagerActivityFinder` is still an excellent secondary choice for navigation.

Customized `Finder` is also acceptable to extend the Bro navigating features. For example, you got a WebView Activity and ReactNative Activity, both of them accept "http" in AndroidManifest scheme. In this case, it's a good practice to create a `HttpSchemeFinder` to route different URIs to different Activities.

### Substitute of getFragments() and startServices()

You may wonder why Bro didn't provide such as `getFragment()` and `startSerivce()` APIs, that's because:

- If you only want to get a Fragment instance without casting to the specified class type (ex. CartFragment, ProfileFragment), add an export API to do that like:

``` java
@Override
public Fragment getProfileFragment() {
    return ProfilePresenterFragment.newInstance();
}
```

- If the specify class type required, add a Interface for exported actions:

``` java
@Override
public IProfilePageActions getProfileFragment() {
    return ProfilePresenterFragment.newInstance();
}

...

// When take it as IProfilePageActions
ProfileApi.getProfileFragment().doAction();

// When take it as Fragment
Fragment f = (Fragment) ProfileApi.getProfileFragment())
getFragmentManager().beginTransaction().add(f, "profile").commit();
```

- There are not so many Services that be exported among the App so that you can do the same behavior as exported Fragment above. Or, if it's a long-term task running on the background, you can start it when the module starts (check `onCreate(context: Context)` method of the `IBroModule`).



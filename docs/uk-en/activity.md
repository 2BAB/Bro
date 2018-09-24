# Global Routing

Using Activity as the carrier,  Native Page， Web Page, Weex Page will be generalized as unified Uris.

## Usage

### Initialization

When initializing Bro, pass the implementations of ``IActivityFinder``as parameter.

````
List<IActivityFinder> finders = new ArrayList<>();
finders(new AnnoPageFinder());
finders(new PackageManagerPageFinder());

BroConfig config = new BroConfig.Builder()
                    .setLogEnable(true)
                    .setActivityFinders(finders)
                    .build();

````

Bro iterates the list of finders in the initialzation process when looking for the target Activity, and the process will not proceed when the return value of  one of ``PageFinder``s is not null.

### Declaration of Pages that need to be exposed

Two approaches are supported by default so far. ``Finder`` can be extended for wider range of support.

- Annocation : ``@BroActivity(String uri)`` Passing  ``uri`` as parameter for the annotation for the Activity needing to be exposed.

````
@BroActivity("broapp://settings")
public class SettingsActivity extends AppCompatActivity {
    ...
}
````

- Manifest : compatiable with Android native router support.
````
<intent-filter>
    <category android:name="android.intent.category.DEFAULT" />
    <data
        android:host="home"
        android:scheme="broapp" />
</intent-filter>
````

### Start Another Activity

````
Bundle bundle = new Bundle();
bundle.putString("bundleparam", "123");
Bro.startActivityFrom(context)
        .withExtras(bundle)
        .toUri(Uri.parse("broapp://home?urlparam=233"));

// more APIs and how to get the result of redirection
// ActivityRudder rudder = Bro.startPageFrom(this) // current Context
//        .withExtras(bundle) // with Extras
//        .withFlags(flags) //   with Flags
//        .withCategory(category) // add category
//        .forResult(resultCode) // for parameters of onActivityResult  
//        .justForCheck() //Activity will not start if this api is applied, which usualy is to check the existence of the target page without turning to it
//        .toUri(Uri.parse("broapp://home?urlparam=233"));   // target Uri
//
// rudder.isIntentValidate(); // whether Activity is valid 
// rudder.isIntercepted(); //    whether it is intercepted
// rudder.getIntent(); // get the  Intent for start Activity
// rudder.getBuilder(); //  get the builder for start Activity
````

## Best Practice

## It's recommended to use annotation to declare ``Page``

It's remmended to use ``@BroActivity`` to expose an ``Activity`` , though Bro gives you ``IActivityFinded`` and ``setFinders`` as customized options. The reasons are listed as follows:

- Declarations in Manifest often come with logic needing specical treatment. For example, if the third-party sdk comes along with an ``intent-filter`` for http while another ``intent-filter`` has already been declared in you app, some treatment will be applied to match and distinguish them( eg: using category).

- Manifest declarations don't supported customization for BroProperties, which means some customized attributes cannot be intercepted ( but this question will be solved in future)

- Manifest declaration may relatively expose some unnessary information.


As a matter of fact, ``AnnoActivityFinder`` seems be able to take care of everything, customized ``Finder`` is not nessary. Taking into account the actual situation, the remaining navigation login and exsited bus design often use manifest as container, in order to make users migrate seamlessly to Bro, these interfaces come into being.



## Bus for Fragments and Services

In the early versions of Bro, such methods like ``getFragment()`` and ``startSerivce()`` once existed. However, these methods cannot apply very often:

- Most of Android engineers prefer to replace fragment with transparent Activity or Dialog after breaking down modules in some scenarios, such as implementing the update dialog or display a film ticket, in this way, the implementation is more independent and easier ( data interaction can be realized through methods like ``onActivityResult()``)

- There are not so many Services in an App, let along fewer situations to expose them as usual Services are exsited as a long-term task running in background or starts when App and module starts.


It's not difficult to realized that, with BroApi offered by Bro, the above scenarios can be implemented with apis in BroApi. 
See the BroApi documentation and the Sample project for details.



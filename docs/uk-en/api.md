
# API Export

## Overall

Bro offers API export & calling functions among different modules.

- Put your Interface files into a individual module like `featureA-api`.
- Each `FeatureX-impl` doesn't depend on each other, instead they can depend on `FeatureX-api` modules for correct decoupling.
- Bro will inject the implementation of the interface when you call `Bro.get().`

## Usage

### Export interfaces

``` java
// Place your API interface (which extends IBroApi) in the feature-api or common module first.
public interface IDataApi extends IBroApi{

    int getTestData1();

}

// Implement the interface and annotate with @BroApi on the implementation class to be exposed. 
// The implementation class should be placed in its feature-impl module to avoid.

// @BroSingleton stands for keeping the single instance in App Scope,
// because we initialized Bro in Application#onCreate(), and this instance
// will be cached inside the Bro obviously so that it shares the same
// lifecycle with the entire application
@BroSingleton
// @BroApi stands for exposing this class, and in this case, it belongs 
// to SettingsModule.class, anyone who wants to invoke this API trigger 
// the module entry's initialization if it hasn't done that
@BroApi(module = SettingsModule.class) 
public class SettingsApiImpl implements IBroApi {

    @Override
    public int getPi() {
        return 314159;
    }

    @Override
    public IMinePresenter getProfileFragment() {
        return ProfilePresenterFragment.newInstance(null);
    }
    
    @Override
    public void onCreate() {
        Log.e("SettingsApiImpl", "onInit");
    }

}

```

### Invoke Export APIs from Other Modules

```
Bro.getApi(IDataApi.class).getTestData1();
```

## Best Practice

### Extend the boundaries of navigation through BroApi services

We have discussed the reason why bro doesn't provide APIs for navigation between Fragment, Service, etc. In fact, not just Fragment, Service, but modules also depend on more fine-grained components like Views. They may not be provided by a simple interface file, but they can be encapsulated and exposed through the interface of BroApi.

``` java
class DummyView extends View implements DummyAction {
    
    void start();
    
    void pause();
    
    void stop();
    
    ...
}

interface IDummyApi extends IBroApi {
    DummyAction getDummyVIew();
}

class DummyApiImpl implements IDummyApi {
    public DummyAction getDummyVIew() {
        return new DummyView(mContext);
    }
}
```

From the example above, we've encapsulated the behaviors of `DummyView` by a `DummyAction` interface and then exposed it. In most cases, the user needs `DummyAction` instead of the `DummyView`. However, the user is able to convert it into a `View` object when they are going to do something related to `View` object like `View#setVisibility(int i)`. 

Decoupling with interfaces is a flexible approach, which can create some incredible effects in particular scenarios, and provide more possibilities for improving the efficiency and decoupling of modules.

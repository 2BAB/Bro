# Overall

Bro offers you a set of callback methods, the usage of which is just like the ones you use in ``Application``.

Eg: the ``onCreate()`` method of ``IBroModule`` will be called after the ``onCreate()``of ``Application``


# Usage 

## LifeCycle Listeners

In your service module:

````
// implements IBroModule and annotates with  @BroModule to expose the module to the outside world
@BroModule("DataModule")
public class DataModule implements IBroModule {

    @Override
    public void onCreate() {
        Log.e("DataModule", "onCreate");
    }
}
```` 

## Trigger Callback Listeners

- Bro needs to be initialized in Application's ``onCreate()`` method, as ``onCreate()`` will be triggered in ``init(...)`` block of Bro.  

## Best Practice

to be continued!
Bro promises you can call interfaces between different modules with the simplest and least problematic solution. All businesses should write their interfaces which to be exposed in the Common module (or Common package with independent businesses). And then modules rely only on Common, not on each other. Bro will dynamically inject the implementation of the interfaces, and get the corresponding instance(singleton) through ``getApi()``.

## Usage
### Expose interfaces
```
// Extend IBroApi in the Common module first.
public interface IDataApi extends IBroApi{

    int getTestData1();

}

// Implement the interface and annotate the implementation class to be exposed. Implementation should be written in its own business module.
@BroApi("DataApi")
public class DataApiImpl implements IDataApi {

    @Override
    public int getTestData1() {
        return 66666;
    }

    // Provide an initial life cycle for the service when Bro is initialized.
    @Override
    public void onInit() {

    }

// Provide a life cycle for the service which declares its dependencies on other services before calling onInit()
// and then call onInit() sequentially after parsing out a dependency tree.
// If there is a cycle dependency, it will throw an exception at startup.
    @Override
    public List<Class<? extends IBroApi>> onEvaluate() {
        ArrayList<Class<? extends IBroApi>> depends = new ArrayList<>();
        depends.add(IPiApi.class);
        return depends;
    }
}
```

### Using a service
 ``Class``es pass into the interface is used to get the corresponding implementation for the interface. Generally, we only do a single mapping of the interface-implementation. (you can only get the first implementation if you have multiple implementations, while it's not usual in practical use)
```
Bro.getApi(IDataApi.class).getTestData1();
```

## Best Practice
### Extend the boundaries of navigation through BroApi services
We have discussed the reason why don't provide an approach for navigation between Fragment, Service, etc. In fact, it's not just Fragment, Service. In most cases, modules also depend on more fine-grained components in the level of View. They may not be provided by a simple data interface, but they can be encapsulated and exposed through the interface of BroApi.
```
class DummyView extends View implements DummyAction {
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
In the example, we've encapsulated the operation of DummyView into a DummyAction and exposed it. The user only needs to convert it into a View when they need to do some operations relative View. In most cases, we can continue to use DummyAction to do the DummyView operation.
Flexible use of return of interfaces can create some incredible effects in some special scenarios, and providing more possibilities for improving the efficiency and decoupling of modules.

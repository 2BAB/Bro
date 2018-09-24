# Interceptor 

It only provides global interceptions and monitoring callbacks. In essence,  it is not appropriate to expose each business to register as both intercepting and monitoring belong to the structure of the basic logic, which may cause issues such as false intercept. When using interceptors, you can customize the processing flow of Pipeline and divide interceptors into several, but the proposed way to get them together.

## Usage
### Initialization
During initialization, inject the implementation of interceptor and monitor:
```
IBroInterceptor interceptor = new IBroInterceptor() {
  // True stands for intercepting and preventing subsequent actions
    @Override
    public boolean onFindActivity(Context context, String s, Intent intent, BroProperties broProperties) {
        // Before looking for Activity, you can replace the Activity's target link and other interception funtions(intent setdata()) here
        return false;
    }

    @Override
    public boolean onStartActivity(Context context, String s, Intent intent, BroProperties broProperties) {

        // The search and Parameter splicing have been completed, you can do login interception and other functions here only before junping(get the information of whether login is required from broProperties, please refer to the best practice below)
        return false;
    }

    @Override
    public boolean onGetApi(Context context, String s, IBroApi iBroApi, BroProperties broProperties) {
        // After finding the broApi, you can replace an instance of the API, and so on, before returning .(this is common with Mock data)
        return false;
    }

    @Override
    public boolean onGetModule(Context context, String s, IBroModule iBroModule, BroProperties broProperties) {
        // After finding the broModule, you can replace the instance of module and other functions, before returning. (usually seen in Mock data)
        return false;
    }
};

IBroMonitor monitor = new IBroMonitor() {
    @Override
    public void onActivityException(int i, ActivityRudder.Builder builder) {
        // When Activity throws a error during a lookup or jump.
    }

    @Override
    public void onModuleException(int i) {
        // When it throws a error while looking for Module.
    }

    @Override
    public void onApiException(int i) {
         // When it throws a error while looking for Api.
    }
};

Bro(sApplication,
        new BroInfoMapImpl(),
        interceptor,
        monitor,
        config);
```
### Parameter description
- BroProperties,  divided into two parts: clazz is the actual class name of the annotated class; extraParam is a JSON string that contains any annotations(full class name) except the ones with beginning of @Broxxx and its contents, as well as some other class-related parameters  collected by Bro during its compilation.

## Best practices
### Page permission validation (scene validation)
In order to attract users, many apps do not check logins on the first few pages until some important operations(such as logging into "favorites", "member purchase",etc.) that pop up the login box or upgrade to membership. At this time, we can define two annotations as @needlogin @needvip in the Common module, and then add the annotations in the corresponding page:
```
@NeedLogin
@NeedVip("1")
@BroActivity("TestActivity")
public class TestActivity extends Activity {...}
```
This code will create the extraParams of BroProperties as:
```
{
"com.example.package.NeedLogin": "",
"com.example.package.NeedVip": "1",
}

```
This allows you to check in the interceptor whether the Activity jump contains such descriptions (NeedLogin, NeedVip), and do some controlling and jumping to the corresponding logic for global page permissions (for example, jump to the login page after an interception).

### Degrading and Immediate Bug Solving
Generally, we can preset an online configuration check in the interceptor. When we get a bug in page and we can't find a way to fix it at once, we can turn off the access to a specific page by activatig the  configuration, and displaying a customized 404 page. This method to deal with emergency is very effective, getting more time for actual bug fixes and reducing user loss.


At the same time, when a Native page has a corresponding Web implementation version(it is better to unify the Uri), we can also accomplish the graceful degradation of Native -> Web by push configuring when a Bug occurs in Native Activity. Thus, the bug will not affect the user's use.

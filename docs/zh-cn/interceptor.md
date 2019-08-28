只提供全局的拦截和监控回调，本质上由于是拦截、监控本身是偏底层逻辑的一层结构，暴露到各个业务方去注册颇有不适，可能会造成误拦等问题。使用拦截器时，可以自定义一个 Pipeline 的处理流程，分成几个拦截面去做，但是建议收敛内聚到一起。

## 用法用例

### 初始化

在 Bro 初始化时，注入拦截器、监控器的实现：

``` java
IBroInterceptor interceptor = new IBroInterceptor() {

    @Override
    public boolean beforeFindActivity(Context context, String target, Intent intent, BroProperties properties) {
        return false;
    }

    @Override
    public boolean beforeStartActivity(Context context, String target, Intent intent, BroProperties properties) {
        //Log.i("App", properties.toJsonString());
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

Bro(sApplication,
        new BroInfoMapImpl(),
        interceptor,
        monitor,
        config);
```
### 参数说明

- BroProperties，分两部分：clazz 为注解类的实际完整类名；extraParam 为一个 JSON 字符串，内部包含了除 @BroXXX 开头之外的任意注解（全类名）和其内容，以及 Bro 本身在编译期收集的一些其他类相关参数，具体案例可以看下面的应用场景；

## 最佳实践

### 页面权限校验（场景校验）

很多 App 为了吸引用户，总会在开始的几个页面不做登录校验，直到一些关键操作（例如登录收藏、会员购买等等）才会弹出登录框或升级会员。这时候我们可以在 Common 模块中定义个注解为 `@RequireLoginSession`，另外为了演示我们额外添加了 `@RequireMultiValues` 的注解，然后把对应需要的页面加上注解：

``` java
@RequireLoginSession(123)
@RequireMultiValues(value = 1, value1 = "AString", value2 = 12345L, value3 = 'a', value4 = true)
@BroActivity("broapp://home")
public class TestActivity extends Activity {...}
```

这段代码会生成的 BroProperties 为：

```
BroProperties{
    clazz='me.xx2bab.bro.sample.home.HomeActivity', 
    extraAnnotations={
        me.xx2bab.bro.sample.common.annotation.RequireMultiValues={
            value2=12345L, 
            value1=AString, 
            value4=true, 
            value3=a,
            value=1
        },
        me.xx2bab.bro.sample.common.annotation.RequireLoginSession={
            value=123
        }
    }
}
```

这样你就可以在拦截器中检查 Activity 的跳转是否含有此类描述（RequireLoginSession、RequireMultiValues），从而做到对全局页面权限的把控和跳转到对应的逻辑（例如拦截后跳登录页）。

### 降级和救火

一般地，我们可以在拦截器中预埋一个线上配置检查。当我们碰到了一个页面 Bug，一时难以找到修复办法、十万火急的情况下，可以通过推送配置，来关闭特定名称页面的访问权限，取而代之的是显示一个默认定制的 404 页面。这招用作紧急止血往往非常奏效，给 Bug 的真正修复争取了更多的时间并减少用户的流失。

同时，当一个 Native 页面有对应的 Web 实现版本时（最好能统一 Uri），我们也可以在 Native Activity 出现 Bug 时，通过配置推送完成 Native -> Web 的优雅降级。在不影响用户使用的前提下，平稳度过 Bug 期。



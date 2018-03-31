只提供全局的拦截和监控回调，本质上由于是拦截、监控本身是偏底层逻辑的一层结构，暴露到各个业务方去注册颇有不适，可能会造成误拦等问题。使用拦截器时，可以自定义一个 Pipeline 的处理流程，分成几个拦截面去做，但是建议收敛内聚到一起。

## 用法用例

### 初始化

在 Bro 初始化时，注入拦截器、监控器的实现：

``` java
IBroInterceptor interceptor = new IBroInterceptor() { // true 表示拦截，阻止后续动作
    @Override
    public boolean onFindActivity(Context context, String s, Intent intent, BroProperties broProperties) {
        // 在查找 Activity 之前，可以在这里做替换 Activity 的目标链接等拦截功能（intent.setData()）
        return false;
    }

    @Override
    public boolean onStartActivity(Context context, String s, Intent intent, BroProperties broProperties) {
        // 查找完成、并且所有参数也拼接完成，只在跳转之前，可以在这里做登录拦截等功能（从 broProperties 获取是否需要登录等等的信息，具体参考下方最佳实践案例）
        return false;
    }

    @Override
    public boolean onGetApi(Context context, String s, IBroApi iBroApi, BroProperties broProperties) {
        // 在查找到 broApi 之后，返回之前，可以做替换 api 的实例等功能（常见于 Mock 数据）
        return false;
    }

    @Override
    public boolean onGetModule(Context context, String s, IBroModule iBroModule, BroProperties broProperties) {
        // 在查找到 broModule 之后，返回之前，可以做替换 module 的实例等功能（常见于 Mock 数据）
        return false;
    }
};

IBroMonitor monitor = new IBroMonitor() {
    @Override
    public void onActivityException(int i, ActivityRudder.Builder builder) {
        // 当 Actiivty 查找、跳转过程抛错的时候
    }

    @Override
    public void onModuleException(int i) {
        // 当 Module 的查找抛错的时候
    }

    @Override
    public void onApiException(int i) {
         // 当 Api 的查找抛错的时候
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

很多 App 为了吸引用户，总会在开始的几个页面不做登录校验，直到一些关键操作（例如登录收藏、会员购买等等）才会弹出登录框或升级会员。这时候我们可以在 Common 模块中定义两个注解分别为 `@NeedLogin` `@NeedVip`，然后把对应需要的页面加上注解：

``` java
@NeedLogin
@NeedVip("1")
@BroActivity("TestActivity")
public class TestActivity extends Activity {...}
```
    
这段代码会生成的 BroProperties 的 extraParams 为：
    
``` json
{
"com.example.package.NeedLogin": "",
"com.example.package.NeedVip": "1",
}
```
    
这样你就可以在拦截器中检查 Activity 的跳转是否含有此类描述（NeedLogin、NeedVip），从而做到对全局页面权限的把控和跳转到对应的逻辑（例如拦截后跳登录页）。
    
### 降级和救火

一般地，我们可以在拦截器中预埋一个线上配置检查。当我们碰到了一个页面 Bug，一时难以找到修复办法、十万火急的情况下，可以通过推送配置，来关闭特定名称页面的访问权限，取而代之的是显示一个默认定制的 404 页面。这招用作紧急止血往往非常奏效，给 Bug 的真正修复争取了更多的时间并减少用户的流失。

同时，当一个 Native 页面有对应的 Web 实现版本时（最好能统一 Uri），我们也可以在 Native Activity 出现 Bug 时，通过配置推送完成 Native -> Web 的优雅降级。在不影响用户使用的前提下，平稳度过 Bug 期。



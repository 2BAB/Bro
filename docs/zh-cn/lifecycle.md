提供类似 Application 的模块生命周期回调，其中 `onCreate()` 在 App 的 Application 的 `onCreate()` 触发时回调。 

## 用法用例

### 生命周期监听

在自己业务模块中：

``` java
// 继承 IBroModule 并做注解 @BroModule 用以暴露该 Module
@BroModule("DataModule")
public class DataModule implements IBroModule {

    @Override
    public void onCreate() {
        Log.e("DataModule", "onCreate");
    }
}
```

### 触发监听回调

- `onCreate()` 的回调，会在 Application 的 `onCreate()` 触发时回调子模块初始化；

## 最佳实践

待续


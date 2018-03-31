以 Activity 为载体，支持将 Native Page、Web Page、Weex Page 、RN Page 抽象成统一的 Uri。

## 用法用例

### 初始化

初始化 Bro 时，传入 finder 的具体实现 List：

``` java
List<IActivityFinder> finders = new ArrayList<>();
finders(new AnnoPageFinder());
finders(new PackageManagerPageFinder());

BroConfig config = new BroConfig.Builder()
                    .setLogEnable(true)
                    .setActivityFinders(finders)
                    .build();
                    
...                    
```

**Bro 查找 Activity 时，会按照初始化传入的 Finders 顺序逐一进行调用查找，并在某一个 PageFinder 获取到的返回值不为 null 时停止后续查找。**


### 声明需要暴露的页面

目前默认支持两种，更多支持可自行扩展 Finder。

- Annotation：`@BroActivity(String uri)` 在需要暴露的 Activity 类上，声明该注解并填入对应的 uri 别名即可，例如：

``` java
@BroActivity("broapp://settings")
public class SettingsActivity extends AppCompatActivity {
    ...
}
```

-  Manifest：兼容 Android 原生的路由支持，如下：

``` xml
<intent-filter>
    <category android:name="android.intent.category.DEFAULT" />
    <data
        android:host="home"
        android:scheme="broapp" />
</intent-filter>
```

### 跳转

 ``` java
Bundle bundle = new Bundle();
bundle.putString("bundleparam", "123");
Bro.startActivityFrom(context)
        .withExtras(bundle)
        .toUri(Uri.parse("broapp://home?urlparam=233"));

// 更多 API，以及如何获取跳转结果
// ActivityRudder rudder = Bro.startPageFrom(this) // 当前 Context
//        .withExtras(bundle) // 携带参数
//        .withFlags(flags) // 携带 Flags
//        .withCategory(category) // 添加 category
//        .forResult(resultCode) // 用于 onActivityResult 的请求参数  
//        .justForCheck() // 若调用该 API，则不会触发最终的 startActivity，通常用来配合检查目标页面是否存在（而不想跳转）
//        .toUri(Uri.parse("broapp://home?urlparam=233"));   // 目标 Uri
//
// rudder.isIntentValidate(); // 目标 Activity 是否被找到了
// rudder.isIntercepted(); // 跳转过程中是否被拦截了
// rudder.getIntent(); // 获得跳转所用的 Intent
// rudder.getBuilder(); // 获得页面跳转的参数构造器               
 ```


## 最佳实践

### 推荐使用注解声明 Page

尽管 Bro 提供了 `IActivityFinder` 和 `setFinders` 的丰富自定义选项，但是依然建议使用 `@BroActivity` 的方式来声明一个 Activity 的暴露，主要是出于以下几点考虑：

- Manifest 声明常常会带来需要特殊处理的逻辑，例如你在自己的 App 中声明了一个 http 的 intent-filter，而你引入的一个三方 sdk 也声明了一个，就需要在总线跳转中添加一些特殊逻辑来判断和区分匹配（如 category）；
- Manifest 声明目前没有支持 BroProperties 的自定义，即无法在拦截器中获取到页面的自定义属性（不过未来可能会支持）；
- Manifest 声明会相对容易地暴露一些不必要的信息；

那这时大家会问为什么还需要设计可自定义的 Finder 实现，全部用 ExcaliburPageFinder 不就好了？**事实上，考虑到很多 App 已有的跳转逻辑、或者老的总线设计常常会使用 Manifest 做媒介，为了能让大家无缝地迁移到 Bro 的完整框架中，才设计了这样的接口。**

### Fragment、Service 等等的总线跳转？

在早期的 Bro 版本中，其实是实现过 getFragment、startService 的方法。但是我们发现，实际使用的场景里，这种情况少之又少：

1. Fragment 的获取虽然有场景，但是有些场景大家更愿意在拆模块后把 Fragment 换成透明 Activity 或者 Dialog 实现，例如 App 的更新弹窗、或者一个电影票根的展示，更独立也更方便（例如可以通过 onActivityResult 做数据交互）；
2. Service 的数量在一个 App 里本来就不多，需要暴露启动的场景又少，因为一般 Service 都是作为一个长线任务在后台默默工作，在 App 或模块启动时就会启动了；

加上 Bro 本身提供了 BroApi 的接口总线，于是不难想到: 少量的 Fragment 或 Service 的获取、启动场景，可以通过 BroApi 提供接口的方式来实现，具体可以参考 BroApi 的文档以及 Sample 工程。



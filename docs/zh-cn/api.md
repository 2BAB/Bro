提供了模块间的接口调用功能，力求最简单最不会出问题的解法。所有业务把要暴露的 Interface 写在 Common 模块（或者独立的业务 Common 包）中，然后模块之间相互不进行依赖，只依赖 Common。Bro 会在运行时动态注入这些接口的实现，通过 `getApi()` 可以获取到对应的实现实例（单例保存）。

## 用法用例

### 接口暴露

``` java
// 先继承 IBroApi 的接口，接口写在 Common 模块中
public interface IDataApi extends IBroApi{

    int getTestData1();

}

// 再实现该接口并注解实现类用以 expose，实现写在自己的业务模块中
@BroApi("DataApi")
public class DataApiImpl implements IDataApi {

    @Override
    public int getTestData1() {
        return 66666;
    }

    // 为服务提供一个初始化的生命周期，会在初始化 Bro 时做
    @Override
    public void onInit() {

    }
    
    // 为服务提供一个声明对其他服务依赖情况的生命周期，会在 onInit() 之前做，
    // 解析出一个依赖树之后，再按顺序进行 onInit()，
    // 如果有环依赖，会在启动时抛错
    @Override
    public List<Class<? extends IBroApi>> onEvaluate() {
        ArrayList<Class<? extends IBroApi>> depends = new ArrayList<>();
        depends.add(IPiApi.class);
        return depends;
    }
}
```

### 使用某个服务

传入接口的 Class 用以获取接口对应的实现，一般地，我们只做接口-实现的单一映射。（若有多实现则只能取到第一个实现，当然实际使用中一般都不会做多实现的）

``` java
Bro.getApi(IDataApi.class).getTestData1();
```

## 最佳实践

### 通过 BroApi 服务拓展导航的边界

在导航章节的最佳实践中提到过为什么不提供 Fragment、Service 等导航方法的原因，事实上，不仅是 Fragment、Service，很多时候模块之间依赖的可能还有更细粒度的 View 级别的组件。他们可能不是一个简单的数据接口就提供出去，但却可以借助 BroApi 的接口中心做一下封装和暴露，例如：

``` java
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

如上，我们将 DummyView 的操作封装到 DummyAction 中暴露出去，使用方只需要在需要对 DummyView 做 View 相关操作时强转成 View，大多数情况下可以继续保持使用 DummyAction 来对 DummyView 操作。

灵活地运用接口返回可以在一些特殊的场景下创造不可思议的效果，给研发效率的提升和模块解耦的思路提供了更多的可能。


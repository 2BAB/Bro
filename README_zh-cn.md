# Bro

中文 | [English](https://github.com/2BAB/Bro/blob/master/README.md)

[![JCenter](https://api.bintray.com/packages/2bab/maven/bro/images/download.svg)](https://bintray.com/2bab/maven/bro/_latestVersion) [![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Android 的模块化解决方案。


## Why Modularization

- 你在 Native、H5、RN、Weex 之间的页面跳转过程繁琐，没有统一逻辑（譬如有些页面需要登录态有些不需要）；
- 你想要给不同的用户群体展示不同的界面、功能，但是你必须在所有相关的地方加上 if/else；
- 你维护的模块需要吐出一些接口给其他模块调用，你们都不想被对方直接依赖，但是又没有一个全局的双向通信管道，可能会有大量的 BroadcastReceiver 或者 EventBus 到处飞线；
- 尽管你正在维护的是一个中小型的 App（可能团队都不超过 10 人），但是团队成员们还是想尽量独立地开发自己的模块，甚至是独立打包和运行自己的应用，而不受其他人的影响（例如想 Mock 掉另外一个模块的依赖）；
- ...

所有的这些问题，都是我所碰到，并且 Bro 想尝试去解决的。建议在阅读更多的细节之前，提前了解下冯森林老师 16 年底在 MDCC 上分享的 [《回归初心，从容器化到组件化》](https://github.com/MDCC2016/Android-Session-Slides/blob/master/02-From.Containerization.To.Modularity.pdf)。一定要强调一点的是，**Bro 的目标不是一个路由框架，而是以组件解耦、高效开发为目标的模块化框架**，包括提供了模块编译期元数据收集等基础功能。不过 Bro 并没有多数的组件化框架那样，做让各个组件独立安装的选项，而是各个模块还是长在了主工程的这个容器之上，然后通过本地 Debug 时的热部署方式提速模块的打包。
  
  
## Features

- 支持模块初始化的分离（类似于继承了 Application），便于监控各个模块的初始化状况；
- 支持模块间的基础路由功能，可以**自定义**实现 Native Activity 和 WebPage 以及 Weex、RN 等容器的轻松互跳和全局统一的 Uri 规则；
- 支持模块间的 API 暴露和调用，并可籍此实现跨模块的 Fragment 获取或 Service 启动等等额外功能；
- 支持**自定义**注解（添加跨模块的同一标识）、子注解处理器（生成自定义的路由表或者文档等）、注解作为额外属性的配置（在拦截器中使用）等等；
- 提供全局的拦截器和监控器来拦截上述所有过程，并可以在拦截器中方便地获取到页面、服务的自定义属性；
- 支持自动生成默认的页面路由代码、文档，并包括了各类自定义属性；
- 支持模块以常规的 aar 格式输出，集成到宿主，而在打包 aar 的过程中即生成模块的配置信息到 aar 中，有效减少打整包的时间；
- 支持模块独立打包更新到现有的 APK [README](media/README.md)中，即本地热部署（*目前新版本重构中*，并仅在本地打包时可使用）；
- 更多的自定义空间，请参考后续文档的最佳实践部分；


## Tutorial

所有文档托管在 Github Page，并使用 [Docsify](https://github.com/docsifyjs/docsify) 进行展示. 欢迎贡献你的想法到 `/docs` 下的文档.

- 如何集成
    - [快速开始](https://2bab.github.io/Bro/#/zh-cn/quick-start)
    - [模块生命周期](https://2bab.github.io/Bro/#/zh-cn/lifecycle)
    - [模块间页面跳转](https://2bab.github.io/Bro/#/zh-cn/activity)
    - [模块间服务调用](https://2bab.github.io/Bro/#/zh-cn/api)
    - [拦截和监控](https://2bab.github.io/Bro/#/zh-cn/interceptor)
    - [模块热部署](https://2bab.github.io/Bro/#/zh-cn/livereload)
    - [样例应用](https://2bab.github.io/Bro/#/zh-cn/sample)
- 为什么 Bro 做了这些取舍
    - 未完待续...
   
  
## Compatible Specification

精力有限，Bro 只会至多支持最新两个 Minor 版本的 Android Gradle Plugin（例如最新版是 3.4.x，那同时会支持 3.3.x）：

AGP Version|Compatible Status
-----------|-----------------
3.5.x | Testing...
3.4.x | Support (last support version - 1.0.0)


## Git Commit Check

Check this [link](https://medium.com/walmartlabs/check-out-these-5-git-tips-before-your-next-commit-c1c7a5ae34d1) to make sure everyone will make a **meaningful** commit message.

So far we haven't added any hook tool, but follow the regex below:

```
(chore|feat|docs|fix|refactor|style|test|hack|release)(:)( )(.{0,80})
```
  

## License

>
> Copyright 2016-2019 2BAB
>
>Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
>
>   http://www.apache.org/licenses/LICENSE-2.0
>
> Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.



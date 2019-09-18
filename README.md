# Bro

English | [中文](https://github.com/2BAB/Bro/blob/master/README_zh-cn.md)

[![JCenter](https://api.bintray.com/packages/2bab/maven/bro/images/download.svg)](https://bintray.com/2bab/maven/bro/_latestVersion) [![Apache 2](https://img.shields.io/badge/License-Apache%202-brightgreen.svg)](https://www.apache.org/licenses/LICENSE-2.0)

A Modularization Solution on Android platform.


## Why Modularization

- The processes that you navigate among Native Activity, H5, RN, Weex are complicated and don't have a consistent logic. (For example, some pages required login session while others not.)
- You want to display different UIs and functionalities to different user groups, but you have to add plenty of if/else blocks in all relevant areas.
- The module you are maintaining on is required to expose some APIs for other modules. However, no one wants to be dependent on each other directly and there is not a global bidirectional communication channel that we can base on (In this case, you may have a lot of BroadcastReceiver or EventBus receiver everywhere)
- Although you are in a small team (Probably less than ten people), team members still want to develop their modules as independently as possible, even package and debug Library module independently without being influenced by others. (Such as mocking any other modules)
- ...

All of these problems are what I have met and what Bro tries to solve. I suggest to read [<From.Containerization.To.Modularity>](https://github.com/MDCC2016/Android-Session-Slides/blob/master/02-From.Containerization.To.Modularity.pdf) before continue integrate Bro (shared by Oasis Feng at the end of 2016), you will get a clear target of what problems are modularization and componentization trying to solve.

> (Quoted From the Slide)
> 
> - Engineering
>   - Enforced decoupling for high-cohesion code.
>   - Module-independent(parallel) development and testing
>   - Flexible integration, deployment&upgrade
>
> - Product
>   - Selective Installation(light-weight initial install)
>   - Hybrid-friendly(web&native, mix&match)
>   - Open to (3rd-party) extensions
 
It's important to emphasize this idea that **Bro's goal is not to be a routing framework, but to be a lightweight modular framework aimed at component decoupling and efficient development.**

  
## Features

 - Supports individual initialization for each module and make it convenient to monitor the initialization cost;
 - Supports Activity routing, and supports customizing global consistent URI rules for Native Activity and Web, RN, Weex and other containers;
 - Supports the interface exposure and interfaces acquisition among modules, and by it to implement the acquisition Fragment or start Service from other modules.
 - Supports customizing annotations (used by adding identification), sub-annotation-processor (used by generating custom routing table or docs), properties of Activities, Services, etc. (used by Interceptor).
 - Supports global interceptors and monitors to intercept all the processes above-mentioned and acquires the custom properties of Activities and Services in interceptors conveniently.
 - Supports automatically generating the page routing table (Java Class) and documentation including all custom properties.
 - Supports exporting regular `.aar` format and then integrates into the host. During the compiling of AAR, Bro help generates the meta-data of the module and put it into the AAR archive, this would reduce the time consuming when we package the entire APK.
 - Supports exporting compiled Libary module and push it into the App that we already installed on the device, named Hot-Reload (only can be used for local packaging and debugging, now during refactoring)
 - For more customization please refer to the best practices section of the subsequent documentation.


## Tutorial

All docs are hosting on Github Page using [Docsify](https://github.com/docsifyjs/docsify). Contributing to `/docs` directory is welcome.

- How
    - [Quick Start](https://2bab.github.io/Bro/#/uk-en/quick-start)
    - [Module's LifeCycle](https://2bab.github.io/Bro/#/uk-en/lifecycle)
    - [Activity Exposure](https://2bab.github.io/Bro/#/uk-en/activity)
    - [API Exposure](https://2bab.github.io/Bro/#/uk-en/api)
    - [Interceptor](https://2bab.github.io/Bro/#/uk-en/interceptor)
    - [LiveReload](https://2bab.github.io/Bro/#/uk-en/livereload)
    - [Sample](https://2bab.github.io/Bro/#/uk-en/sample)
- Why
    - To be continued...
   
  
## Compatible Specification

Bro is only supported & tested on latest **2** Minor versions of Android Gradle Plugin.

AGP Version|Compatible Status
-----------|-----------------
3.5.x | Testing...
3.4.x | Support (last support version - 1.0.1)


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



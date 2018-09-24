Bro is a lightweight Android modularity solution

## problems&scenarios
- The processes that you jump among Native,H5,RN,Weex are complicated and don't have a uniform logic
(for example,some pages need login status while others.)
- You want to display different UI and function to different user groups,but you must add if/else in all relevant places
- The module you are maintaining must let out some interfaces to let other modules call. And all of you don't want to be
dependent on each other directly but don't have global bidirectional communication channels.You may hav a lot of BroadcastReceiver and EventBus everywhere.
- Althogh you are maintaining a small App ( Probably no more than ten people),but team members should develop their own modules as independently as possible
even package and run you own applications independently without being influenced by others.(such as commenting out their code module to make their code running. )
- ...

All of these problems are what I have met and what Bro tries to solve.If you have knew [<From.Containerization.To.Modularity> ](https://github.com/MDCC2016/Android-Session-Slides/blob/master/02-From.Containerization.To.Modularity.pdf)
 shared by Oasis Feng in the end of 2016,you may have a good understanding of the concept of modularization and componentization.
 It's important to emphasize one thing,** Bro's goal is not to  be a routing framework, but to be a modular framework aimed at component decoupling and efficient development. **
 (But Bro isn't like most of componentized framework which has the option for each component to start debug independently. The module are still on top of the main project's container and then decouple modules and packaging through local hot deployment.)

 ## Feature

 - Bro provides the startup callbacks required for the modules.

 - Bro supports inter-module basic routing and implements easy interhopping and globally uniform Uri rules for Native Activity and webex, RN and other containers;

 - Bro provides the interface exposure service and interface acquisition service between modules,and by it realize the acquisition or start of cross-module Fragment and Service.

 - Bro supports custom properties of pages and service.

 - Bro provides global interceptors and monitors to intercept all the processes above-mentioned and acquires the custom properties of pages and service in interceptors conveniently.

 - Bro automatically generates the page routing code and documentations as well as the service code and  documentations,including all kinds of custom properties.

 - Bro provides modules that are output in normal aar format and then integrates into the host to package in whole.At the same time,provide module output in the form of apk for local hot deployment.(only recommended for local debug)

 - For more customization please refer to the best practices section of subsequent documentation.

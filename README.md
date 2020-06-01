# Vigour

> 原始项目在[这里][origin]。原始项目不再维护，只是作为仓库保存历史代码。
> 此项目参考了[nifty][nifty]项目，虽然该项目不在维护了，有些地方还是值得学习的。
>
> [nifty]: https://github.com/facebookarchive/nifty.git
> [origin]: https://github.com/Casper-Mars/springboot-starter-thrift
> 

---
## 开发阶段

### 第一阶段（已完成）
配合spring，实现自动装配出thrift的server

* 使用自定义的注解
* 在spring容器初始化后，获取全部的thrift server实现类。
* 构建服务类统一代理实现的thrift server类，并最终注入到ioc容器中

### 第二阶段（已完成）
配合spring，实现自动装配出thrift的客户端

* 使用自定义的注解，标记客户端使用的熔断类
* 使用cglib动态代理，在thrift调用中出现服务熔断，则调用本地实现类
* 手动注册客户端bean

### 第三阶段（已完成）
添加对spring cloud的支持。

* 改造eureka客户端的注册流程。在服务端启动后，如果有配置eureka，则在eureka客户端注册的服务信息中添加thrift服务信息，包括服务名称和端口
* 改造eureka客户端的获取服务信息流程。在客户端启动后，如果有配置成从eureka获取服务信息，则从eureka注册中心获取服务的信息并提取其中的metaData，解析metaData获取thrift服务信息。
* 注册监听器，监听eureka客户端的服务列表更新。获取最新的服务列表后更新本地的thrift服务信息

### 第四阶段（已完成）
融合server和client。实现server和client并存，赋予微服务服务能力的同时也有客户端功能。

### 第五阶段（已完成）
分离客户端和eurake客户端，把纯封装thrift的抽出成client-core，再定义eurake客户端实现eurake-thrift-client。eurake-thrift-client只需提供eurake相关的bean并引入client-core依赖组装而成。分离的目的为了未来更好地扩展出其他版本的服务注册中心的客户端
同理分离服务端

### 第六阶段（已完成）
修改客户端处理服务的逻辑。原来的扫描全部的服务并建立代理bean的方式有缺陷。在微服务不需要大部分的服务时，只会消耗系统资源去维护服务列表（包括列表刷新和底层socket的操作）。
因此，现在改成按需建立代理bean，只有被ThriftClient注解注释的才会被认为是需要的服务而创建代理bean。同时被注解注释的类会作为熔断回调类在服务down的时候调用。

### 第七阶段（待实现）
修改客户端的socket管理。目前的管理方式没有处理服务端掉线的问题。尝试使用netty做底层通讯框架，方便管理socket和处理各种事件。设置掉线重连机制。


---
## 技术要点

### springboot starter

服务端和客户端都采用了springboot starter标准，实现只需引入便能自动装配的功能。

### thrift RPC 框架

客户端和服务端都采用了Apache开源的thrift RPC框架。使用thrift作为通讯协议。

### netty 框架

服务端采用了netty框架作为通讯框架。
客户端也可以采用了netty作为通讯框架。

### cglib 动态代理

客户端中，所有的thrift服务的是通过cglib动态代理生成代理对象，并注入到ioc容器中，和mybatis的接口注入原理一样。

### spring ioc注入

客户端和服务端都需要用到spring的ioc容器。因此，免不了和ioc注入打交道，实现自定义的注入逻辑。需要了解ioc注入的生命周期、注入的切入点和触发点。

---
## 还存在的问题

### 客户端请求包混乱问题

* 触发条件：客户端使用同一个socket（同一个服务）进行并发地请求
* 问题：服务端netty接收到数据包后进行解码时，发现多个请求的数据糅合在一起，导致无法正常解析。
* 根本原因：thrift的客户端是线程不安全的，当多个请求调用时，底层是共享一个socket。因此在并发的情况下，socket的缓冲区会就会出现写混乱，是比拆包粘包更严重的问题。
* 解决方案：让每个thrift的socket独占一个线程。多个线程对同一个socket的调用，会先放到任务队列中，使用同步机制回去异步执行的结果。

### 客户端本地thrift服务列表更新问题

* 触发条件：客户端在更新thrift服务列表时，接收用户的请求并调用被更新的服务
* 问题：thrift服务列表更新时，如果是执行移动服务操作，则有几率响应用户请求的时候，刚好获取列表最后一个实例的同时，该实例被移除，会导致访问越界。
* 根本原因：多线程安全问题
* 解决方案：加读写锁，不过会有一定的响应性能损耗，一定几率会挂起响应。如果不加锁，直接捕抓异常，则一定几率降低服务质量，会触发熔断

### netty化的客户端

* 存在的困难：原始的nifty项目在客户端这一块的处理是按照有序请求的方式处理的。请求在发送后都会阻塞，下个请求需要等待上一个完成了才能进行处理。
所以要求服务端也是有序的响应。目前服务端的逻辑是强制性有序的。目前需要解决有序和无序的兼容。有序是原生的thrift客户端，无序的是netty代理的客户端。

---
## 开发指南

### 架构

#### 客户端

* 关键类的架构图

> ![avatar][client-core-image]

* client-core 

> 只是核心的部分，实现了扫描客户端并动态代理的功能。细节上着重体现在注入和维护两方面。


* client-eureka

> 实现了服务提供的功能，并实时刷新服务。![avatar][client-eureka-image]

* 结合netty

> thrift框架和netty框架结合。需要理解thrift的架构是怎样的，在那个点的功能可以由netty进行代理。
经过粗糙的理解，thrift定义了transport的概念负责底层的socket操作的，从这个点出发，用netty进行代理transport就可以实现结合两个框架




[client-core-image]:./info/client-core.png
[client-eureka-image]:./info/client-eureka.png






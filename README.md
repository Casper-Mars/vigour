# Vigour

> 原始项目在[这里][origin]。原始项目不再维护，只是作为仓库保存历史代码。
> 此项目参考了[nifty][nifty]项目，虽然该项目不在维护了，有些地方还是值得学习的。
>
> [nifty]: https://github.com/facebookarchive/nifty.git
> [origin]: https://github.com/Casper-Mars/springboot-starter-thrift
>

---

## 使用指南

### 概念介绍

框架是基于c/s模式设计的，客户端和服务端都兼容在框架中，通过配置文件进行配置激活。

### 配置介绍

框架支持的配置属性和默认值如下：
```yaml
thrift:
    client:
        netty: #客户端规定启用netty
          enable: false #是否启用netty
          boss-pool-size: 1 #netty的调度线程池大小(客户端不需要)
          work-pool-size: 4 #netty的io处理线程池大小
        base-package: com.demo #包扫描路径，用于指示带有@ThriftClient的类所在的地方
        enable: false #是否启动客户端
        max-frame-size: 67108864 #thrift数据帧的大小的最大值
        server-infos: #一个map结构的数据集合，key为服务端地址(ip-端口)。value为服务名称，多个服务名称用逗号隔开
          "localhost-8090": AuthService,LoginService
    server:
        enable: false #是否启用服务端
        port: 8090 #thrift服务处理监听的端口
        name: thrift-server #名称，用处不大
        max-connections: 10 #服务端能同时处理的最大连接数
        max-frame-size: 67108864 #thrift数据帧的大小的最大值
        thrift-version: 0.13.0 #thrift版本号，暂时无用处
        netty: #可选是否启用netty作为底层通讯处理。默认是不启用的netty而用原生的thrift服务处理
          enable: true #是否启用netty
          boss-pool-size: 1 #netty的io处理线程池大小
          work-pool-size: 4 #netty的调度线程池大小
```

### 客户端使用

* 单应用使用

使用springboot框架的，只需引入thrift-springboot-starter依赖即可。客户端的使用方式和feign类型。区别在于需要使用者实现thrift的接口并在类上加入注解@ThriftClient。该实现类作为调用熔断时使用的类。例如：
```java
@ThriftClient
public class AuthClient implements AuthService.Iface {
    @Override
    public String auth(String username, String password) throws TException {
        return "本地熔断_auth";
    }
}
```
如果不需要熔断处理，方法体置空即可。
使用其他框架的，需要引入thrift-netty依赖，并手动配置thrift接口实现类。thrift-netty包提供了创建基于netty的TTransport的逻辑。

* 微服务使用

框架提供了支持eureka服务发现的实现。参考这种思路，可以扩展到其他的服务发现中间件。
使用eureka的实现版本(thrift-starter-eureka-client)需要项目使用了spring cloud全家桶并引入netflix的eureka依赖。

### 服务端使用

服务端的依赖和客户端的一样，使用方式也是基本一样，只是使用的注解不一样。
服务端要在实现了thrift接口的类上加上@ThriftService注解。例如：
```java
@ThriftService
public class TestServcie implements LoginService.Iface{

    @Value("${server.port}")
    private int port;

    @Override
    public String doAction(Request request) throws TException {
        return String.valueOf(port);
    }
}
```
注意：ThriftService和ThriftClient一样，都是继承了spring的@Component的。


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

### 第七阶段（已完成）

修改客户端的socket管理。目前的管理方式没有处理服务端掉线的问题。尝试使用netty做底层通讯框架，方便管理socket和处理各种事件。设置掉线重连机制。

### 第八阶段（已完成）

thrift耦合了netty后，底层的实际通讯逻辑经由netty实现。把耦合后的thrift和netty从spring的逻辑中抽离出来，形成新的模块，spring只是负责管理依赖和注入依赖，thrift负责管理rpc的协议和调用，netty负责管理底层的网络通讯

### 第九阶段(待实现)

* 重新处理服务端的逻辑：thrift-netty的服务端代码大部分主要的逻辑还是沿用Facebook的nifty项目的，而且经过改造过，某些细节上和原来的不一样，魔鬼往往就藏在这些细节中。所以在此阶段需要对服务端的逻辑重新梳理。
* 客户端和服务端添加超时控制：在主流的rpc处理中，都会有超时处理，要不重试要不异常。项目中还未对超时做处理。

### 第十阶段(待实现)

* 原始的thrift是支持多种协议的，例如：常用的二进制压缩协议、符合thrift-json数据格式的http协议。需要扩展框架支持的协议

### 第十一阶段(待实现)

* 客户端对channel是复用的，而且是支持异步的rpc调用。但是请求量大时，一个channel可能负载不了，需要引入一套智能负载算法，在负载量大的时候动态增加channel副本减轻单一channel的压力。



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

## 问题

### 客户端请求包混乱问题(close)

* 触发条件：客户端使用同一个socket（同一个服务）进行并发地请求
* 问题：服务端netty接收到数据包后进行解码时，发现多个请求的数据糅合在一起，导致无法正常解析。
* 根本原因：thrift的客户端是线程不安全的，当多个请求调用时，底层是共享一个socket。因此在并发的情况下，socket的缓冲区会就会出现写混乱，是比拆包粘包更严重的问题。
* 解决方案：让每个thrift的socket独占一个线程。多个线程对同一个socket的调用，会先放到任务队列中，使用同步机制回去异步执行的结果。

### 客户端本地thrift服务列表更新问题(close)

* 触发条件：客户端在更新thrift服务列表时，接收用户的请求并调用被更新的服务
* 问题：thrift服务列表更新时，如果是执行移动服务操作，则有几率响应用户请求的时候，刚好获取列表最后一个实例的同时，该实例被移除，会导致访问越界。
* 根本原因：多线程安全问题
* 解决方案：加读写锁，不过会有一定的响应性能损耗，一定几率会挂起响应。如果不加锁，直接捕抓异常，则一定几率降低服务质量，会触发熔断

### netty化的客户端(close)

* 存在的困难：原始的nifty项目在客户端这一块的处理是按照有序请求的方式处理的。请求在发送后都会阻塞，下个请求需要等待上一个完成了才能进行处理。
所以要求服务端也是有序的响应。目前服务端的逻辑是强制性有序的。目前需要解决有序和无序的兼容。有序是原生的thrift客户端，无序的是netty代理的客户端。
* 解决方案：设计上，netty的channel代理thrift的transport进行读写，每次请求获取客户端都新建一个代理后的transport，其中该transport复用相同ip端口的channel
* 副作用：每次的请求都需要反射生成thrift的客户端，需要额外的开销

### netty化后的客户端(close)

* 问题：在多线程的情况下，底层创建channel的逻辑没有加锁，导致高并发的情况下会出现多个相同目标主机的链接的channel。
* 解决方案：加上锁

### thrift请求数据构造(close)

* 问题：不清除thrift是怎样构造请求的，在获取seqId的时候，不同的请求得到的结果是一样的。因此推测seqId是对于同一个请求而言才有意义。
* 解决方案：再把thrift协议封装一次，每个channel为所发送的thrift请求添加唯一的id，接收的时候按照id进行响应

### 多线程高并发下会出现某些请求失败的问题(close)

* 问题：在多个线程同时调用同一个channel发送请求时，会出现某些请求失败，一直得不到服务端的响应。通过抓包排查发现，请求的数据包确实是通过网卡发送出去了，而服务器的网卡也接收到了，但是服务进程却没有接收到数据包。
通过对比单线程循环发送请求的tcp数据包顺序和多线程并发发送的顺序，发现循环发送的情况数据包是按照：发->收->确认的顺序。而多线程的情况下是多个线程的数据包按照先后次序依次发送，并没有等服务端的响应，随后服务端的响应也只是其中某几个请求。
通过对比，分析得出推论：在多线程的情况下，可能是缺少了确认的数据包，导致通讯不完整，只有某些包是有确认数据包的，所以只有某些请求响应了
* 原因：通过进一步分析，判断有可能是tcp粘包拆包的问题造成的。仔细查看下，客户端是做了粘包拆包处理，但是经过分析，服务端并没有。因此怀疑是服务端没有做相应的处理，
添加相应的处理后，果然就可以了。
* 解决方案：客户端和服务端都要添加tcp粘包拆包的处理

### 服务端会出现内存泄漏

* 问题：在多线程访问下，会有一定几率出现内存泄漏的异常

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

## change log

>只有重大的逻辑变更才会记录在这里

### 客户端变更

* 2020-06-24

```

原来的客户端是使用原生的thrift客户端进行rpc通讯的。当搭配eureka使用的时候，需要对eureka监听到的服务刷新事件进行处理。底层需要维护一套在线可用的transport列表
经过netty化后的客户端，可以减少维护成本，服务的可用只需根据netty的链接状态即可维护，此时的eureka更多的只是作为一个服务信息增量提供的中间件。

```






[client-core-image]:./info/client-core.png
[client-eureka-image]:./info/client-eureka.png




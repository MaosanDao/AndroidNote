# Android Service的启动流程原理分析
## 启动方式
```
关于Service在Android中有两种启动方法：
  1.启动
  2.绑定

它们的生命周期如下：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231111588-262034472.png)
## 在新的进程中启动Service
```
分为5个步骤：
  
  1.App向AMS发送一个启动Service的消息
  2.AMS检查启动Service的进程是否存在，如果不存在，先把Service信息存下来，然后创建一个新的进程
  3.新进程启动后，通知AMS说我可以啦
  4.AMS把刚才保存的Service信息发送给新进程
  5.新进程启动Service
```
### 第一阶段，App告诉AMS我要启动Service
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231119900-1492923568.png)
### 第二阶段，检测进程是否存在
```
1.AMS检查Service是否在Manifest中声明了，没声明会直接报错
2.AMS检查启动Service的进程是否存在，如果不存在，先把Service信息存下来，然后创建一个新的进程。

注：
  在AMS中，每个Service，都使用ServiceRecord对象来保存。
```
### 第三阶段，启动新进程并通知
```
Service启动新进程的流程和App启动新进程的流程是相似的。

新进程启动后，也会创建新的ActivityThread，然后把ActivityThread对象通过AMP传递给AMS，告诉AMS，新进程启动成功了。
```
### 第四阶段，AMS把Service信息告诉新进程
```
AMS把传进来的ActivityThread对象改造为ApplicationThreadProxy，也就是ATP，通过ATP，把要启动的Service信息发送给新进程。
```
### 第五阶段，新进程启动Service
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231131010-228401732.png)
```
新进程通过ApplicationThread接收到AMS的信息，和前面介绍的启动Activity的最后一步相同,
借助于ActivityThread和H，执行Service的onCreate方法。在此期间，为Service创建了Context上下文对象，并与Service相关联。
```
#### handleCreateService方法
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231142197-538428403.png)
```
从PMS中取出包的信息packageInfo，这是一个LoadedApk对象，然后获取它的classloader，
反射出来一个类的对象，在这里反射的是Service。
```
***
## 在同一进程启动Service
```
步骤：

  1.App向AMS发送一个启动Service的消息
  2.AMS例行检查，比如Service是否声明了，把Service在AMS这边注册。
    AMS发现要启动的Service就是App所在的Service，就通知App启动这个Service。
  3.App启动Service
```
## 在统一进程绑定Service
```
步骤：

  1.App向AMS发送一个绑定Service的消息
  2.AMS例行检查，比如Service是否声明了，把Service在AMS这边注册。AMS发现要启动的Service就是App所在的Service，
    就先通知App启动这个Service，然后再通知App，对Service进行绑定操作。
  3.App收到AMS第1个消息，启动Service
  4.App收到AMS第2个消息，绑定Service，并把一个Binder对象传给AMS
  5.AMS把接收到的Binder对象，发送给App
  6.App收到Binder对象，就可以使用了
  
疑问？
  为什么App需要要Binder来回传递，为啥不自己用就行了？
  
  因为考虑到不在一个进程里面的这种情况，所以只能这样咯，代码不能写两份吧。哈哈
```
### 第一阶段，App向AMS发送一个绑定Service的消息
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231153400-1053122975.png)
### 第四阶段，App收到消息开始绑定并返回Binder给AMS
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231200791-687448931.png)
### 第五和六阶段，AMS把Binder对象发送给App，使用了AIDL
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170520231209541-1531293878.png)
```
如上图所示，App最后调用的是ServiceDispatcher的connect方法，而这个方法，最终会调用
ServiceConnection的onServiceConnected方法。
```















































































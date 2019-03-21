# Android App开发人员需要知道的一些Android底层知识
***
## Binder
### 什么是Binder
```
Binder是为了解决跨进程通信。

首先，Binder分为Client和Server两个进程。

注意，Client和Server是相对的。谁发消息，谁就是Client，谁接收消息，谁就是Server。

举个例子，两个进程A和B之间使用Binder通信，进程A发消息给进程B，那么这时候A是Binder Client，B是Binder Server；
进程B发消息给进程A，那么这时候B是Binder Client，A是Binder Server——其实这么说虽然简单了，但还是不太严谨，我们先这么理解着。
```
### Binder的组成结构
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170516223325025-1448613892.png)
```
图中的ServiceManager，负责把Binder Server注册到一个容器中。
```
### Binder的通信过程
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170516223354650-984999229.png)
```
由上图可见：

Client想要直接调用Server的add方法，是不可以的，因为它们在不同的进程中，这时候就需要Binder来帮忙了。

具体过程为：

1.首先是Server在SM这个容器中注册。
2.Client想要调用Server的add方法，就需要先获取Server对象， 但是SM不会把真正的Server对象返回给Client，
  而是把Server的一个代理对象返回给Client，也就是Proxy。
3.Client调用Proxy的add方法，SM会帮他去调用Server的add方法，并把结果返回给Client。
```
***
## AIDL
```
Android系统中很多系统服务都是aidl，比如说剪切板。
```
### AIDL
```
AIDL中的几个类：

IBinder
IInterface
Binder
Proxy
Stub
```
### 他们如何进行IPC的通信
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170516223504650-228437964.png)
```
由上图分析：

1.先从Client看起，对于AIDL的使用者，我们这么写程序：
  MyAidl.Stub.asInterface(某IBinder对象).sum(1, 2);
  
  asInterface方法的作用是判断参数——也就是IBinder对象，和自己是否在同一个进程：
    是，则直接转换、直接使用，接下来就跟Binder跨进程通信无关啦。
    否，则把这个IBinder参数包装成一个Proxy对象，这时调用Stub的sum方法，间接调用Proxy的sum方法。
      return new MyAidl.Stub.Proxy(obj);
      
2.Proxy在自己的sum方法中，会使用Parcelable来准备数据，把函数名称、函数参数都写入_data，让_reply接收函数返回值。
  最后使用IBinder的transact方法，把数据就传给Binder的Server端了。
  
  mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0); //这里的mRemote就是asInterface方法传过来的obj参数

3.Server则是通过onTransact方法接收Client进程传过来的数据，包括函数名称、函数参数，找到对应的函数，这里是sum，把参数喂进去，得到结果，返回。
   所以onTransact函数经历了读数据-->执行要调用的函数-->把执行结果再写数据的过程。
```
***
## AMS初探
```
如果站在四大组件的角度来看，AMS就是Binder中的Server。

AMS全称是ActivityManagerService，看字面意思是管理Activity的，但其实四大组件都归它管。
```
### 什么是Hook
```
Hook翻译过来是钩子的意思，我们都知道无论是手机还是电脑运行的时候都依赖系统各种各样的API，当某些API不能满足我们的要求时，
我们就得去修改某些api，
使之能满足我们的要求。这样api hook就自然而然的出现了。我们可以通过api hook，改变一个系统api的原有功能。

基本的方法就是通过hook“接触”到需要修改的api函数入口点，改变它的地址指向新的自定义的函数。
当然这种技术同样适用于Android系统，
在Android开发中，我们同样能利用Hook的原理让系统某些方法运行时调用的是我们定义的方法，从而满足我们的要求。
```
### 为什么Hook总是在Binder的Client端
```
AMS要负责和所有App的四大组件进行通信，也真够他忙的。如果在一个App中，在AMS层面把剪切板功能给篡改了，
那会导致Android系统所有的剪切板功能被篡改——这就是病毒了，如果是这样的话，Android系统早就死翘翘了。

所以Android系统不允许我们这么做。

我们只能在AMS的另一侧，Client端，也就是四大组件这边做篡改，这样即使我们把剪切板功能篡改了，
也只影响篡改代码所在的App，在别的App中，剪切板功能还是正常的。
```
***
## Activity
### App是怎么启动的
```
在手机屏幕上点击某个App的Icon，假设就是斗鱼App吧，这个App的首页（或引导页）就出现在我们面前了。
这个看似简单的操作，背后经历了Activity和AMS的反反复复的通信过程。

首先要搞清楚，在手机屏幕上点击App的icon快捷图标，此时手机屏幕就是一个Activity，而这个Activity所在的App，业界称之为Launcher。
Launcher是手机系统厂商提供的，类似小米华为这样的手机，比拼的就是谁的Launcher绚丽和人性化。

怎么启动的？

Launcher中为每个App的icon提供了启动这个App所需要的Intent信息：
action：android.intent.action.MAIN
category: android.intent.category.LAUNCHER
cmp: 斗鱼的包名+ 首页Activity名

然后这些信息时怎么来的?
这些信息是App安装（或Android系统启动）的时候，PackageManagerService从斗鱼的apk包的manifest文件中读取到的。

启动App这么简单么？
```
### 没有那么简单
```
假设我们要启动一个斗鱼App，那么是怎么启动的？

仔细想想，我们会发现，Launcher和斗鱼是两个不同的App，他们位于不同的进程中，它们之间的通信是通过Binder完成的——这时候AMS出场了：

启动流程如下：
1.Launcher通知AMS，要启动斗鱼App，而且指定要启动斗鱼的哪个页面（也就是首页）。
2.AMS通知Launcher，好了我知道了，没你什么事了，同时，把要启动的首页记下来。
3.Launcher当前页面进入Paused状态，然后通知AMS，我睡了，你可以去找斗鱼App了。

4.AMS检查斗鱼App是否已经启动了。是，则唤起斗鱼App即可。否，就要启动一个新的进程。
  AMS在新进程中创建一个ActivityThread对象，启动其中的main函数。
5.斗鱼App启动后，通知AMS，说我启动好了。
6.AMS翻出之前在第二步存的值，告诉斗鱼App，启动哪个页面。
7.斗鱼App启动首页，创建Context并与首页Activity关联。然后调用首页Activity的onCreate函数。

1-3步，则是Launcher和AMS相互通信
4-7步，斗鱼App和AMS相互通信
```
### 分析具体的启动过程
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519224933760-1702392298.png)
#### 1-2步
```
点击Launcher上的斗鱼App的icon快捷图标，这时会调用Launcher的startActivitySafely方法，
其实还是会调用Activity的startActivity方法，intent中带着要启动斗鱼App所需要的关键信息，如下所示：

action = “android.intent.action.MAIN”
category = “android.intent.category.LAUNCHER”
cmp = “com.douyu.activity.MainActivity”

这样，我们终于明白，为什么在Mainfest中，给首页指定action和category了。
在app的安装过程中，会把这个信息“记录”在Launcher的斗鱼启动快捷图标中
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519224954541-1243107283.png)
#### 第3步startActivityForResult
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225012385-1869709294.png)
```
其中会发现一个mMainThread的变量，这是一个ActivityThread类型的变量。

什么是ActivityThread?
  ActivityThread，就是主线程，也就是UI线程，它是在App启动时创建的，它代表了App应用程序。
  
```



































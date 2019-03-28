# Android App开发人员需要知道的一些Android底层知识（[原文](https://www.cnblogs.com/Jax/p/6880631.html)）
***
## 目录
* [Binder](#binder)
  * [什么是Binder](#什么是binder)
  * [Binder的组成结构](#binder的组成结构)
  * [Binder的通信过程](#binder的通信过程)
* [AIDL](#aidl)
  * [他们如何进行IPC的通信](#他们如何进行ipc的通信)
* [AMS初探](#ams初探)
  * [什么是hook](#什么是hook)
  * [为什么Hook总是在Binder的Client端](#为什么hook总是在binder的client端)
* [App是怎么启动的](#app是怎么启动的)
  * [启动App的第一个阶段](#启动app的第一个阶段)
  * [启动App的第二个阶段](#启动app的第二个阶段)
  * [启动App的第三个阶段](#启动app的第三个阶段)
  * [启动App的第四个阶段](#启动app的第四个阶段)
  * [启动App的第五个阶段](#启动app的第五个阶段)
  * [启动App的第六个阶段](#启动app的第六个阶段)
  * [启动App的第七个阶段](#启动app的第七个阶段)
* [Android内部的页面跳转](https://github.com/MaosanDao/AndroidNote/blob/master/bottom/inner_jump.md) 
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
### 这是Java生成的本地AIDL文件代码
```java
public interface IMyAidlInterface extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements cn.venii.n1.IMyAidlInterface {
        private static final java.lang.String DESCRIPTOR = "cn.venii.n1.IMyAidlInterface";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an cn.venii.n1.IMyAidlInterface interface,
         * generating a proxy if needed.
         */
        public static cn.venii.n1.IMyAidlInterface asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            //这里在本地找是否是本地进程，如果是，则返回本地对象，否则返回代理对象
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof cn.venii.n1.IMyAidlInterface))) {
                return ((cn.venii.n1.IMyAidlInterface) iin);
            }
            return new cn.venii.n1.IMyAidlInterface.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }
        //Binder Server的处理方法
        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            java.lang.String descriptor = DESCRIPTOR;
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(descriptor);
                    return true;
                }
                case TRANSACTION_basicTypes: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    long _arg1;
                    _arg1 = data.readLong();
                    boolean _arg2;
                    _arg2 = (0 != data.readInt());
                    float _arg3;
                    _arg3 = data.readFloat();
                    double _arg4;
                    _arg4 = data.readDouble();
                    java.lang.String _arg5;
                    _arg5 = data.readString();
                    this.basicTypes(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                    reply.writeNoException();
                    return true;
                }
                //这里就是Binder Server真正调用的方法的地方
                case TRANSACTION_sum: {
                    data.enforceInterface(descriptor);
                    int _arg0;
                    _arg0 = data.readInt();
                    int _arg1;
                    _arg1 = data.readInt();
                    this.sum(_arg0, _arg1);
                    reply.writeNoException();
                    return true;
                }
                default: {
                    return super.onTransact(code, data, reply, flags);
                }
            }
        }
        //这个就是内部类 --- 本地的代理对象
        private static class Proxy implements cn.venii.n1.IMyAidlInterface {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            /**
             * Demonstrates some basic types that you can use as parameters
             * and return values in AIDL.
             */
            @Override
            public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(anInt);
                    _data.writeLong(aLong);
                    _data.writeInt(((aBoolean) ? (1) : (0)));
                    _data.writeFloat(aFloat);
                    _data.writeDouble(aDouble);
                    _data.writeString(aString);
                    //这里代理对象，调用了远程的Binder去完成具体方法
                    mRemote.transact(Stub.TRANSACTION_basicTypes, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override
            public void sum(int a, int b) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(a);
                    _data.writeInt(b);
                    mRemote.transact(Stub.TRANSACTION_sum, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_basicTypes = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
        static final int TRANSACTION_sum = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    }

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, java.lang.String aString) throws android.os.RemoteException;

    public void sum(int a, int b) throws android.os.RemoteException;
}
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
### 启动App的第一个阶段
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
  
那Application类呢？

  其实，Application对我们App开发人员来说也许很重要，但是在Android系统中还真的没那么重要，他就是个上下文。
  Activity不是有个Context上下文吗？Application就是整个ActivityThread的上下文。
```
##### ActivityThread里面的main方法
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225039353-1677547683.png)
```
为什么不是我们App自己写main函数呢？

  Android App的main函数，在ActivityThread里面，而这个类是Android系统提供的底层类，不是我们提供的。
  所以这就是Andoid有趣的地方。Android App的入口是Mainifest中定义默认启动Activity。
  
  这是由Android AMS与四大组件的通信机制决定的。
```
#### startActivityForResult中的Binder对象
```
在使用startActivityForResult中，我们发现了mMainThread使用了getApplicationThread方法。
  而它则是获取到了Binder对象，类型为ApplicationThread，也就是Launcher所在的App进程。
  
mToken?
  mToken，这也是个Binder对象，它代表了Launcher这个Activity，这里也通过Instrumentation传给AMS，AMS一查电话簿，就知道是谁向AMS发起请求了
```
#### 第4步execStartActivity
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225106307-741280835.png)
```
就是一个透传，Activity把数据借助Instrumentation，传递给ActivityManagerNative
```
#### AMP/AMN
```
ActivityManagerNative，简称AMN

AMN通过getDefault方法，从ServiceManager中取得一个名为Activity的对象，
然后把它包装成一个ActivityManagerProxy对象（简称AMP），AMP就是AMS的代理对象。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225130822-867987839.png)
```
AMN的getDefault方法返回类型为IActivityManager，而不是AMP。
IActivityManager是一个实现了IInterface的接口，里面定义了四大组件所有的生命周期。
AMN和AMP都实现了IActivityManager接口，AMS继承自AMN。
```
### AMP（ActivityManagerProxy）调用startActivity方法
```
你会发现AMP的startActivity方法，和AIDL的Proxy方法，是一模一样的，写入数据到另一个进程，也就是AMS，然后等待AMS返回结果。
```
***
### 启动App的第二个阶段
```
启动App所经历的7个阶段图示：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225853353-1638311589.png)
#### AMS处理Launcher传来的消息
```
这个阶段主要是Binder的Server端在做事情。因为我们是没有机会修改Binder的Server端逻辑的。

1.首先Binder，也就是AMN/AMP，和AMS通信，肯定每次是做不同的事情，就比如说这次Launcher要启动斗鱼App，
  那么会发送类型为START_ACTIVITY——TRANSACTION的请求给AMS，同时会告诉AMS要启动哪个Activity。
  
2.AMS说，好，我知道了，然后它会干一件很有趣的事情，就是检查斗鱼App中的Manifest文件，是否存在要启动的Activity。
  如果不存在，就抛出Activity not found的错误。
  
3.但是Launcher还活着啊，所以接下来AMS会通知Launcher，哥们儿没你什么事了，你“停薪留职”吧。

那么AMS是通过什么途径告诉Launcher的呢？
```
#### AMS如何通知Luncher让它休息？
```
面不是把Launcher以及它所在的进程给传过来了吗？它在AMS这边保存为一个ActivityRecord对象，
这个对象里面有一个ApplicationThreadProxy，单单从名字看就出卖了它，这就是一个Binder代理对象。
它的Binder真身，也就是ApplicationThread。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225921916-810851677.png)
```
上图就是展示了AMS如何通知App端，进行休眠。

它是通过ApplicationThreadProxy这个App进程的代理对象来调用方法去通知App端进行休眠的。
```
***
### 启动App的第三个阶段
#### 具体怎么通知Luncher休眠的
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225928432-1563080021.png)
```
APT接收到来自AMS的消息后，就调用ActivityThread的sendMessage方法，向Launcher的主线程消息队列发送一个PAUSE_ACTIVITY消息。
其中ActivityThread为主线程，即UI线程。

所以需要使用Handler将这个任务处理了，然后将结果返回给UI线程:
  在ApplicationThread中sendMessage给ActivityThread，让其完成剩下的工作。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225946869-1315230205.png)
```
而这个H则是，在ActivityThread的内部类，如下图：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519225957400-731726279.png)
```
ActivityThread的handlePauseActivity做了什么事情？
  1.ActivityThread里面有一个mActivities集合，保存当前App也就是Launcher中所有打开的Activity，把它找出来，让它休眠。
  2.通过AMP通知AMS，我真的休眠了。
```
***
### 启动App的第四个阶段
```
AMS接下来要启动斗鱼App的首页，因为斗鱼App不在后台进程中，所以要启动一个新的进程。
这里调用的是Process.start方法，并且指定了ActivityThread的main函数为入口函数。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230007713-577694475.png)
***
### 启动App的第五个阶段
```
新的进程启动，以ActivityThread的main函数作为入口。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230017916-541390106.png)
```
在启动新进程的时候，为这个进程创建ActivityThread对象，这就是我们耳熟能详的主线程（UI线程）。

在创建好UI线程后，立即进入ActivityThread的main函数，然后就要做2件重大意义的事情：

  1.创建一个主线程Looper，也就是MainLooper。
  2.创建Application。记住，Application是在这里生成的。
```
#### Application的重要性？
```
App开发人员对Application非常熟悉，因为我们可以在其中写代码，进行一些全局的控制，所以我们通常认为Application是掌控全局的，
其实Application的地位在App中并没有那么重要，它就是一个Context上下文，仅此而已。

App中的灵魂是ActivityThread，也就是主线程，只是这个类对于App开发人员是访问不到的。
```
#### 创建好新的App之后
```
 创建新App的最后，就是告诉AMS，我启动好了，同时把自己的ActivityThread对象发送给AMS，
 从此以后，AMS的电话簿中就多了这个新的App的登记信息。
 AMS以后向这个App发送消息，就通过这个ActivityThread对象。
```
***
### 启动App的第六个阶段
```
该阶段就是AMS告诉App启动哪一个页面。

AMS把传入的ActivityThread对象，转为一个ApplicationThread对象，用于以后和这个App跨进程通信。

因为在第一阶段中，Luncher发送给AMS要启动斗鱼App的哪一个Activity信息。所以，
到了这个阶段，AMS就要去从记录中将这个信息翻出来，然后通过ApplicationThread去告诉App。
```
### 启动App的第七个阶段
```
App启动Ams传递信息中的页面。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230029494-413774325.png)
```
App通过ApplicationThread接收到从AMS发来的启动消息。然后仍然是在H的handleMessage方法的switch语句中处理，
只不过，这次消息的类型是LAUNCH_ACTIVITY：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230047322-2090810570.png)
```
如上图所示，ActivityClientRecord是个什么东西？
  这是AMS传递过来的要启动的Activity。
  
其中，还有一个getPackageInfoNoCheck方法：
  这个方法会提取Apk中的所有资源，然后设置给r的packageInfo属性。
  
然后在H这个类中，反过来再次调用ActivityThread中的handleLaunchActivity方法，那这个方法做了什么事情：
  1.通过Instrumentation的newActivity方法，创建出来要启动的Activity实例。
  2.为这个Activity创建一个上下文Context对象，并与Activity进行关联。
  3.通过Instrumentation的callActivityOnCreate方法，执行Activity的onCreate方法，从而启动Activity。
```
***
### 到此整个App就启动完毕了
```
App启动完毕。这个流程是经过了很多次握手， App和ASM，频繁的向对方发送消息，而发送消息的机制，是建立在Binder的基础之上的。
```











































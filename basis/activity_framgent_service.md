# Activity、Service、Fragment生命周期与相关知识点
****
## 列表
* [Activity](#activity的生命周期)
* [Fragment](#fragment的生命周期)
* [Service](#service生命周期)
****
## Activity的生命周期
#### 图示
![Activity生命周期](https://images2018.cnblogs.com/blog/651317/201804/651317-20180430074450821-2004590573.png)
* onCreate
```
该方法是在Activity被创建时回调，它是生命周期第一个调用的方法，我们在创建Activity时一般都需要重写该方法，
然后在该方法中做一些初始化的操作，如通过setContentView设置界面布局的资源，初始化所需要的组件信息等。

此方法的传参Bundle为该Activity上次已异常情况销毁时，保存的状态信息。
```
* onStart
```
此方法被回调时表示Activity正在启动，此时Activity已处于可见状态，只是还没有在前台显示，因此无法与用户进行交互。
```
* onResume
```
当此方法回调时，则说明Activity已在前台可见，可与用户交互了（处于前面所说的Active/Running形态）。
```
* onPause
```
此方法被回调时则表示Activity正在停止（Paused形态），一般情况下onStop方法会紧接着被回调。
但通过流程图我们还可以看到一种情况是onPause方法执行后直接执行了onResume方法，这属于比较极端的现象了，
这可能是用户操作使当前Activity退居后台后又迅速地再回到到当前的Activity，此时onResume方法就会被回调。

Activity切换的时候，旧的Activity的onPause会先执行，然后才会启动新的Activity（AMS的功能）。
```
* onStop
```
一般在onPause方法执行完成直接执行，表示Activity即将停止或者完全被覆盖（Stopped形态），此时Activity不可见，仅在后台运行。

注意：新的Activity如果是透明主题的时候，是不会走onStop方法的。
```
* onRestart
```
表示Activity正在重新启动，当Activity由不可见变为可见状态时，该方法被回调。
这种情况一般是用户打开了一个新的Activity时，当前的Activity就会被暂停（onPause和onStop被执行了），
接着又回到当前Activity页面时，onRestart方法就会被回调。
```
* onDestory
```
此时Activity正在被销毁，也是生命周期最后一个执行的方法，一般我们可以在此方法中做一些回收工作和最终的资源释放。
```
#### 注意
```
onRestart只会在onStop后调用，onStop则是长时间后台而执行的。
```
#### 区别
```
onStart与onStop是从Activity是否可见这个角度调用的。
 
onResume和onPause是从Activity是否显示在前台这个角度来回调的。

除此之外，他们的实际使用没有明显区别。
```
#### 记忆方式（[原文](http://www.cnblogs.com/kofi1122/archive/2011/04/10/2011772.html)）
![](https://images.cnblogs.com/cnblogs_com/kofi/201104/201104101952164342.png)
```
由上图可见，除了onRestart之外，他们都是一一对应的：

1.onCreate创建与onDestroy销毁；
2.onStart可见与onStop不可见；
3.onResume可编辑（即焦点）与onPause；

我们可以这样来理解：

我们把Activity比作一本书，我们要看书，首先从书架上取出书（onCreate），然后放到桌上（onStart），
接着打开书（onResume），这样我们就可以看书并可以在书本上写字了。

如果这时候我们要启动另一个Activity，也就是要看另一本书，首先我们放下手中的笔或者说合上书（onPause），
然后从书架上拿下另一本书（书2：onCreate），然后把书本2放到桌上并打开（书2：onStart、onResume）。

如果书本1被书本2完全盖住了，即不可见了，就调用书本1的onStop；而如果书本2较小，没有完全盖住书本1，则不会调用。
我们还可以把书本1放回书架上，即onDestroy。
```
## Activity的生命周期的各个阶段
```
完整的生命周期：

Activity在onCreate()和onDestroy()之间所经历的。
在onCreate()中完成各初始化操作，在onDestroy()中释放资源。

可见的生命周期：

Activity在onStart()和onStop()之间所经历的。
活动对于用户是可见的，但仍无法与用户进行交互。

前台的生命周期：

Activity在onResume()和onPause()之间所经历的。
活动可见，且可交互。
```
## onSaveInstanceState和onRestoreInstanceState（[详细解释点这里](https://www.jianshu.com/p/89e0a7533dbe)）
```
出现时机：非用户手动销毁，异常情况下重建Activity。
系统出现异常的时候，调用onSaveInstanceState来保存状态，它的调用在onStop之前，而且和onPause没有时序关系。

onSaveInstanceState与onPause的区别：前者适用于对临时性状态的保存，而后者适用于对数据的持久化保存。

Activity被重新创建时，调用onRestoreInstanceState（该方法在onStart之后），
并将onSavaInstanceState保存的Bundle对象作为参数传到onRestoreInstanceState与onCreate方法。
```
***
## Fragment的生命周期
#### 图示
![Fragment](https://images2018.cnblogs.com/blog/651317/201804/651317-20180430074659570-1842421622.png)
* setUserVisibleHint
```
设置Fragment可见或者不可见时会调用此方法。
在该方法里面可以通过调用getUserVisibleHint()获得Fragment的状态是可见还是不可见的，如果可见则进行懒加载操作。
```
* onAttach
```
执行该方法时，Fragment与Activity已经完成绑定，该方法有一个Activity类型的参数，代表绑定的Activity，
这时候你可以执行诸如mActivity = activity的操作。
```
* onCreate
```
初始化Fragment。
```
* onCreateView
```
初始化Fragment的布局。加载布局和findViewById的操作通常在此函数内完成。
```
* onActivityCreate
```
执行该方法时，与Fragment绑定的Activity的onCreate方法已经执行完成并返回，在该方法内可以进行与Activity交互的UI操作。
```
* onStart
```
此时Fragment由不可见变为可见
```
* onResume
```
执行该方法时，Fragment处于活动状态，用户可与之交互。
```
* onPause
```
执行该方法时，Fragment处于暂停状态，但依然**可见**，用户不能与之交互。
```
* onStop
```
执行该方法时，Fragment完全不可见。
```
* onDestoryView
```
销毁与Fragment有关的视图，但未与Activity解除绑定，依然可以通过onCreateView方法重新创建视图。
通常在ViewPager+Fragment的方式下会调用此方法。
```
* onDestroy
```
销毁Fragment。通常按Back键退出或者Fragment被回收时调用此方法。
```
* onDetach
```
解除与Activity的绑定。在onDestroy方法之后调用。
```
#### 注意
* Fragment创建
```
setUserVisibleHint()->onAttach()->onCreate()->onCreateView()->onActivityCreated()->onStart()->onResume()
```
* Fragment变为不可见状态（锁屏、回到桌面、被Activity完全覆盖）
```
onPause()->onSaveInstanceState()->onStop()
```
* Fragment变为部分可见状态（打开Dialog样式的Activity）
```
onPause()->onSaveInstanceState()
```
* 在Fragment上退出应用
```
onPause()->onStop()->onDestroyView()->onDestroy()->onDetach()
注意退出不会调用onSaveInstanceState方法，因为是人为退出，没有必要再保存数据。
```
****
## Service生命周期
#### 图示
![Service生命周期](https://images2018.cnblogs.com/blog/651317/201804/651317-20180430075056938-2036915962.png)
其中4个手动调用的方法
```
* startService --> 启动服务
* stopService --> 关闭服务
* bindService --> 绑定服务
* unbindService --> 解绑服务
```
5个自动调用的方法
```
* onCreat --> 创建服务
* onStartCommand --> 开始服务
* onDestroy --> 销毁服务
* onBind --> 绑定服务
* onUnBind --> 解绑服务
```
#### 相关知识点
##### 服务分类：
* 本地服务
```
本地服务依附在主进程上而不是独立的进程，这样在一定程度上节约了资源，另外Local服务因为是在同一进程因此不需要IPC，
也不需要AIDL。相应bindService会方便很多。主进程被Kill后，服务便会终止。
```
* 远程服务
```
远程服务为独立的进程，对应进程名格式为:所在包名加上你指定的android:process字符串。
由于是独立的进程，因此在Activity所在进程被Kill的时候，该服务依然在运行，不受其他进程影响，
有利于为多个进程提供服务具有较高的灵活性。该服务是独立的进程，会占用一定资源，并且使用AIDL进行IPC稍微麻烦一点。
```
##### 启动类型
* startService启动
```
主要用于启动一个服务执行后台任务，不进行通信。停止服务使用stopService；
```
* bindService启动
```
该方法启动的服务可以进行通信。停止服务使用unbindService；
```
##### 启动详解
###### StartService方式
* onCreate
```
如果service没被创建过，调用startService()后会执行onCreate()回调。
如果service已处于运行中，调用startService()不会执行onCreate()方法。
也就是说，onCreate()只会在第一次创建service时候调用，多次执行startService()不会重复调用onCreate()，此方法适合完成一些初始化工作。
```
* onStartCommand
```
如果多次执行了Context的startService()方法，那么Service的onStartCommand()方法也会相应的多次调用。
onStartCommand()方法很重要，我们在该方法中根据传入的Intent参数进行实际的操作，
比如会在此处创建一个线程用于下载数据或播放音乐等。
```
* onBind
```
Service中的onBind()方法是抽象方法，Service类本身就是抽象类，所以onBind()方法是必须重写的，即使我们用不到。
```
* onDestory
```
在销毁的时候会执行Service该方法。
```
###### BindService方式
特点：
```
* bindService启动的服务和调用者之间是典型的client-server模式。调用者是client，
  service则是server端。service只有一个，但绑定到service上面的client可以有一个或很多个。
  这里所提到的client指的是组件，比如某个Activity。
  
* client可以通过IBinder接口获取Service实例，从而实现在client端直接调用Service中的方法以实现灵活交互，
  这在通过startService方法启动中是无法实现的。
  
* bindService启动服务的生命周期与其绑定的client息息相关。当client销毁时，client会自动与Service解除绑定。
  当然，client也可以明确调用Context的unbindService()方法与Service解除绑定。
  当没有任何client与Service绑定时，Service会自行销毁。
```
（Server端）让Service支持bindService的调用方式，需要支持：
* 在Service的onBind()方法中返回IBinder类型的实例
* onBInd()方法返回的IBinder的实例需要能够返回Service实例本身
```
最简单的方法就是在service中创建binder的内部类，加入类似getService()的方法返回Service
```
```java
//通过binder实现调用者client与Service之间的通信
private MyBinder binder = new MyBinder();

//client 可以通过Binder获取Service实例
public class MyBinder extends Binder {
  public TestTwoService getService() {
      return TestTwoService.this;
  }
}

//在onBind方法内
public IBinder onBind(Intent intent) {
  Log.i("Kathy", "TestTwoService - onBind - Thread = " + Thread.currentThread().getName());
  return binder;
}
```
Client端要做的事情：
* 创建ServiceConnection类型实例，并重写onServiceConnected()方法和onServiceDisconnected()方法
```java
private ServiceConnection conn = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        isBind = true;
        //当执行到onServiceConnected回调时，可通过IBinder实例得到Service实例对象，这样可实现client与Service的连接
        TestTwoService.MyBinder myBinder = (TestTwoService.MyBinder) binder;
        service = myBinder.getService();
        Log.i("Kathy", "ActivityA - onServiceConnected");
        int num = service.getRandomNumber();
        Log.i("Kathy", "ActivityA - getRandomNumber = " + num);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        //onServiceDisconnected回调被执行时，表示client与Service断开连接，在此可以写一些断开连接后需要做的处理
        isBind = false;
        Log.i("Kathy", "ActivityA - onServiceDisconnected");
    }
};
```
需要注意事项：
```
* 只有第一次被绑定的时候，service才会调用onCreate和onBind方法。从而从中获取该service的实例。
* 如果该service绑定多个client，那么只有当最后一个client被解绑的时候，service才会调用onUnBind方法，
  且会执行onDestory方法。
```
如何保证Service不被杀死？
```
1.onStartCommand方式中，返回START_STICKY
  调用Context.startService方式启动Service时，如果Android面临内存匮乏，可能会销毁当前运行的Service，
  待内存充足时可以重建Service。
  而Service被Android系统强制销毁并再次重建的行为依赖于Service的onStartCommand()方法的返回值。

  还有其他的启动方式：
    1.1.START_NOT_STICKY
      如果返回START_NOT_STICKY，表示当Service运行的进程被Android系统强制杀掉之后，不会重新创建该Service。
  
    1.2.START_STICKY
      表示Service运行的进程被Android系统强制杀掉之后，Android系统会将该Service依然设置为started状态（即运行状态），
      但是不再保存onStartCommand方法传入的intent对象，然后Android系统会尝试再次重新创建该Service，
      并执行onStartCommand回调方法，但是onStartCommand回调方法的Intent参数为null，
      也就是onStartCommand方法虽然会执行但是获取不到intent信息。
  
    1.3.START_REDELIVER_INTENT
      如果返回START_REDELIVER_INTENT，表示Service运行的进程被Android系统强制杀掉之后，与返回START_STICKY的情况类似，
      Android系统会将再次重新创建该Service，并执行onStartCommand回调方法，但是不同的是，
      Android系统会再次将Service在被杀掉之前最后一次传入onStartCommand方法中的Intent再次保留下来并再次传入
      到重新创建后的Service的onStartCommand方法中，这样我们就能读取到intent参数。只要返回START_REDELIVER_INTENT，
      那么onStartCommand重的intent一定不是null。
  
  
2.提高Service的优先级
  在AndroidManifest.xml文件中对于intent-filter可以通过android:priority = "1000"这个属性设置最高优先级，
  1000是最高值，如果数字越小则优先级越低，同时适用于广播。
  
3.提升Service进程的优先级
  提升为前台进程。
  
4.在onDestroy中重启Service

5.系统广播中启动Service
```
****
## （拓展）View生命周期
#### 图示
![View生命周期](https://images2018.cnblogs.com/blog/651317/201805/651317-20180501205230658-1287610640.png)
****
## 说明
```
以上均是在网络上摘录所得，侵权删。
```

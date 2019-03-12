# Android中进程保活的一些策略
***
## 目录
* [Android手机进程回收策略](#android手机进程回收策略) 
* [如何保活](#如何保活)
  * [提升进程的优先级](#提升进程的优先级) 
    * [如何视为前台进程](#如何视为前台进程)
    * [如何视为可见进程](#如何视为可见进程)
    * [如何视为服务进程](#如何视为服务进程)
    * [如何视为后台进程](#如何视为后台进程)
    * [空进程](#空进程)
  * [保活手段](#保活手段)
    * [1像素的悬浮窗](#1像素的悬浮窗)
    * [将Service设置为前台服务](#将service设置为前台服务)
  * [杀死进程后拉活](#杀死进程后拉活)
    * [在Service的onStart中返回START_STICK](#在service的onstart中返回start_stick)
    * [系统级App](#系统级app)
    * [复写Service的onDestroy方法](#复写service的ondestroy方法)
    * [监听一些系统级或者其他应用的广播将进程拉活](#监听一些系统级或者其他应用的广播将进程拉活)
    * [使用时钟AlarmManager唤醒](#使用时钟alarmmanager唤醒)
    * [双服务守护](#双服务守护)
    * [多个App之间拉起](#多个App之间拉起)
    * [JobSchedule机制拉活](#jobSchedule机制拉活)
## Android手机进程回收策略
```
Android中，主要的内存回收是靠LowMemorykiller来完成，且它是以oom_adj和oom_score来进行内存处理的：

  1.每个进程都有一个oom_adj值。
  2.每个进程都有一个oom_score值，它是根据oom_adj计算而来的。分数越大，就越容易杀死。
  3.oom_adj越小，进程越不容易被杀死。
    3.1 oom_adj >= 4 是容易被杀死的进程
    3.2 oom_adj 0-3 不容易被杀死的进程
    3.3 oom_adj < 0 不会被杀死
```
## 如何保活
```
根据上述的探究，一共有两个方向：

  1.提升进程的优先级
  2.进程被杀死后，拉活进程
```
### 提升进程的优先级
```
Android中进程的优先级顺序:

  1.前台进程
  2.可见进程
  3.服务进程
  4.后台进程
  5.空进程
```
#### 如何视为前台进程
```
  1.托管于用户正在交互的Activity
    已经调用了Actitity的OnResume方法
  2.托管于某个Service，后者绑定到用户正在监护的Activity
    bindService的Activity正处于活动状态。
  3.正在前台运行的Service
    服务调用了startForeground方法
  4.正在执行一个生命周期的Service
    onCreate、onStart、onDestroy
  5.正在执行onReceive的广播
  
这些只有当系统内存不足的情况下，万不得已的情况下，才会终止它们。
```
#### 如何视为可见进程
```
  1.托管不在前台，但是仍对用户可见的Activity（已调用onPause方法）
  2.绑定到可见的Service中
  
可见进程极其重要，除非为了维持前台进程而必须终止，否则系统不会终止它们。
```
#### 如何视为服务进程
```
正在运行已使用的startService方法启动的服务，并且不属于以上两个更高的类别。

  比如，在后台播放音乐或者网络下载数据等。
```
#### 如何视为后台进程
```
包含目前对用户不可见的 Activity 的进程:

  已调用 Activity 的onStop()方法
  
这些进程对用户体验没有直接影响，系统可能随时终止它们，以回收内存供前台进程、可见进程或服务进程使用。
```
#### 空进程
```
不含任何活动应用组件的进程。保留这种进程的的唯一目的是用作缓存，以缩短下次在其中运行组件所需的启动时间。 
为使总体系统资源在进程缓存和底层内核缓存之间保持平衡，系统往往会终止这些进程。
```
### 保活手段
#### 1像素的悬浮窗
```
监控手机锁屏解锁时间，在屏幕锁屏的时候启动一个1像素的Activity，在用户解锁的时候，再将其Activity销毁掉。
目的是，解决第三方应用和系统管理工具在锁屏后大概5分钟，会杀死后台进程，已到达省电的问题。
```
```java
//方案、步骤

//设置一个1像素的Activity
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");
    setContentView(R.layout.activity_singlepixel);
    Window window = getWindow();
    //放在左上角
    window.setGravity(Gravity.START | Gravity.TOP);
    WindowManager.LayoutParams attributes = window.getAttributes();
    //宽高设计为1个像素
    attributes.width = 1;
    attributes.height = 1;
    //起始坐标
    attributes.x = 0;
    attributes.y = 0;
    window.setAttributes(attributes);

    ScreenManager.getInstance(this).setActivity(this);
}

//设置一些属性
//排除Activity在RecentTask中的显示：
android:excludeFromRecents = "true"
android:exported = "false"
android:finishOnTaskLaunch = "false" 

//监听系统锁屏解锁广播
//启动Activity
Intent.ACTION_SCREEN_OFF
//销毁Activity
Intent.ACTION_USER_PRESENT
```
#### 将Service设置为前台服务
```
使用serForeground将Service这是为前台服务，但是这个必须在系统的通知栏中发送一条通知，也就是前台Service。

有方法可以解除这一条通知：

  1.对于 API level < 18 ：调用startForeground(ID， new Notification())，发送空的Notification ，图标则不会显示。
  2.对于 API level >= 18：在需要提优先级的service A启动一个InnerService，两个服务同时startForeground，
    且绑定同样的 ID。Stop InnerService，这样通知栏图标即被移除。
```
```java
public class KeepLiveService extends Service {
    //通知ID
    public static final int NOTIFICATION_ID=0x11;

    public KeepLiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
         //API 18以下，直接发送Notification并将其置为前台
        if (Build.VERSION.SDK_INT <Build.VERSION_CODES.JELLY_BEAN_MR2) {
            startForeground(NOTIFICATION_ID, new Notification());
        } else {
            //API 18以上，发送Notification并将其置为前台后，启动InnerService
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            startService(new Intent(this, InnerService.class));
        }
    }

    //对于API>18的做法
    public  static class  InnerService extends Service{
        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
        @Override
        public void onCreate() {
            super.onCreate();
            //发送与KeepLiveService中ID相同的Notification，然后将其取消并取消自己的前台显示
            Notification.Builder builder = new Notification.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            startForeground(NOTIFICATION_ID, builder.build());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    stopForeground(true);
                    NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    manager.cancel(NOTIFICATION_ID);
                    stopSelf();
                }
            },100);
        }
    }
}
```
#### 杀死进程后拉活
##### 在Service的onStart中返回START_STICK
```
关于onStart中的返回值：
  
  1.START_STICKY_COMPATIBILITY：
    START_STICKY的兼容版本，但不保证服务被kill后一定能重启。
  2.START_STICKY：
    系统就会重新创建这个服务并且调用onStartCommand()方法，但是它不会重新传递最后的Intent对象，
    这适用于不执行命令的媒体播放器（或类似的服务），它只是无限期的运行着并等待工作的到来。
    
    注意：不会重新传递Intent对象
  3.START_NOT_STICKY：
    直到接受到新的Intent对象，才会被重新创建。
    这是最安全的，用来避免在不需要的时候运行你的服务。
    
    注意：接收到新的Intent才会被重新创建。
 4.START_REDELIVER_INTENT：
    系统就会重新创建了这个服务，并且用最后的Intent对象调用。等待中的Intent对象会依次被发送。这适用于如下载文件。

我们在onStartCommand中返回：START_STICKY。

注意：

  1.Service 第一次被异常杀死后会在5秒内重启，第二次被杀死会在10秒内重启，第三次会在20秒内重启，
    一旦在短时间内 Service 被杀死达到5次，则系统不再拉起。
  2.进程被取得 Root 权限的管理工具或系统工具通过 forestop 停止掉，无法重启。
```
##### 系统级App
```
做法：
  
   1.在Apk的AndroidManifest.xml文件中设置android:persistent=true
   2.此apk需要放入到system/app目录下，成为一个system app。
   
 app.persistent = true不仅仅标志着此apk不能轻易的被kill掉，亦或在被kill掉后能够自动restart。
```
##### 复写Service的onDestroy方法
```
原因：
  在设置中的正在运行的服务，点击关闭。Service会走onDestroy方法。所以咱们还可以在这里将自己拉活。
  但是，一些清理软件或者force close则不会走。
```
##### 监听一些系统级或者其他应用的广播将进程拉活
```
系统级的常用的广播：
```
![image2](https://upload-images.jianshu.io/upload_images/2658728-c814b7a4670dd788.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/700)
```
注意：
  
  1.广播接收器被管理软件、系统软件通过“自启管理”等功能禁用的场景无法接收到广播，从而无法自启。
  2.系统广播事件不可控，只能保证发生事件时拉活进程，但无法保证进程挂掉后立即拉活。
```
##### 使用时钟AlarmManager唤醒
```
主要是实现也一个监听开机的广播，和一个周期性的闹钟，不过比较致命的是耗电量是很高的。
```
##### 双服务守护
```
这个是android里面一个特性，跨进程bind一个service之后，如果被bind的service挂掉，bind他的service会把他拉起来。
```
##### 多个App之间拉起
```
比较常见的就是家族 app 之间互相调起，你监听到我死了，我把你拉起，之间互相拉活对方。
```
##### JobSchedule机制拉活
```
public class MyJobService extends JobService {
    @Override
    public void onCreate() {
        super.onCreate();
        startJobSheduler();
    }

    public void startJobSheduler() {
        try {
            JobInfo.Builder builder = new JobInfo.Builder(1, newComponentName(getPackageName(),MyJobService.class.getName()));
            //间隔500毫秒调用一次onStartJob函数
            builder.setPeriodic(500);
            builder.setPersisted(true);
            JobScheduler jobScheduler = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(builder.build());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }
}
```
***
## 参考文章：
* [Android进程保活](https://www.cnblogs.com/fuyaozhishang/p/6667301.html) 
* [Android 进程保活手段分析](https://blog.csdn.net/omnispace/article/details/80935204)










































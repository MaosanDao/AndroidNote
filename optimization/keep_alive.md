# Android中进程保活的一些策略
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
  2.对于 API level >= 18：在需要提优先级的service A启动一个InnerService，两个服务同时startForeground，且绑定同样的 ID。
    Stop InnerService，这样通知栏图标即被移除。
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











































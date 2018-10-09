# App启动流程
## App基础理论
* 每一个Android App都在一个独立的空间中，意味着运行在一个单独的进程中，拥有自己的VM，且被系统分配一个唯一的ID
* 每一个Apk都运行在自己的Linux进程中，默认进程中只有一个主线程，这个主线程中有个Looper实例，通过调用Looper.loop()从Message队列里面取出并处理

## 启动流程分析
### 启动系统时
* bootloader --> 内核 --> init进程 --分裂--> daemons(守护进程)
* init --> Zygote（进程）--> 初始化第一个VM（预加载Framework和App的一些通用资源）--> 开启一个Socket接口监听请求 --> 根据请求孵化新的VM来管理新的App进程

### 从Home上点击App的启动流程
#### 点击Launcher图标 --> IPC机制 --> Actvity Manager Service
ActivityManagerService这一步要做的工作：
* 通过PackageManager的resolveIntent()方法来手机这个点击Intent的指向信息
* 将指向信息存放在一个Intent中
* 通过grantUriPermissionLocked()来验证是否有权限调用该Intent指向的Activity
* 如果有权限则会在新的Task中启动该Activity
* 最后使用ProcessRecord来检查是有该Activity的进程存在
* 如果不存在，则会创建一个新的进程来启动activity
#### ActivityManagerService启动新的进程
* ActivityManagerService会使用startProcessLocked()方法来创建一个新的进程
* 上面的方法会使用socket将参数传递为Zygote进程
* Zygote会进程自我孵化并调用ZygoteInit.main()方法来实例化ActivityThread对象最终返回新进程的pid
* ActivityThread会依次调用Looper.prepareLoop()和Looper.loop()来开启消息循环
#### 将进程和Application进行绑定
* 使用ActivityThread的bindApplication()方法进行绑定
* bindApplicaiton发送一个BIND_APPLICATION给消息队列
* 通过handleBindApplication()方法处理该消息
* 最后通过makeApplication()方法将App的class到内存中
#### 拥有了进程后，如何从进程中启动新的Activity
* 使用realStartActivity()方法调起application线程对象中的sheduleLaunchActivity(）方法
* 使用sheduleLaunchActivity(）发送一个LAUNCH_ACTIVITY消息到消息队列中
* handleLaunchActivity()通过performLaunchActiivty()方法回调Activity的onCreate()方法和onStart()方法
* 然后通过handleResumeActivity()方法，回调Activity的onResume()方法，最终显示Activity界面。
## 相关图示
![图示1](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/start_process_1.jpg)
![图示2](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/start_process_2.png)
## 参考文章
>* [[译]Android Application启动流程分析](https://www.jianshu.com/p/a5532ecc8377)
>* [一个APP从启动到主页面显示经历了哪些过程?](https://www.jianshu.com/p/a72c5ccbd150)



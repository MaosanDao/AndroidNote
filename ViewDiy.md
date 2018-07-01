# 造成卡顿的相关原因以及解决办法
## 相关原因
### UI线程中的耗时操作
>UI线程中有I/O读写、数据库访问等耗时操作
### 复杂、不合理的布局以及OverDraw
>不合理的布局虽然可以完成功能，但随着控件数量越多、布局嵌套层次越深，展开布局花费的时间几乎是线性增长，性能也就越差。
>避免OverDraw导致的性能损耗。
### 内存异常也可能导致卡顿
>内存抖动、内存泄漏都会导致：GC的次数越多、消耗在GC上的时间越长，CPU花在界面绘制上的时间相应越短。
### 错误的异步方式也可以导致卡顿
>对线程开启方式的不同选择以及不同配置都可能导致卡顿的发生。
## 检测卡顿的工具
>第三方的卡顿检测工具
### [AndroidPerformanceMonitor](https://github.com/markzhai/AndroidPerformanceMonitor/blob/master/README_CN.md)
### 工具原理
>利用了Looper.loop()中每个Message被分发前后的Log打印，而我们设置自己的Printer就可以根据Log的不同的处理
* Message分发前，使用HandlerThread延时发送一个Runnable，这个时间可自己设置
* Message在规定的时间内完成分发，则会取消掉这个Runnable
* Message没有在规定的时间内（实际上是规定时间的0.8）完成分发，那这个Runnable就会被执行，可以获取到当前的堆栈信息
```java
Printer logging = me.mLogging;
if (logging != null) {
    logging.println(">>>>> Dispatching to " + msg.target + " " +
            msg.callback + ": " + msg.what);
}

msg.target.dispatchMessage(msg);

if (logging != null) {
    logging.println("<<<<< Finished to " + msg.target + " " + msg.callback);
}
```
## 如何解决
### 将耗时操作移到异步中去做
>类如I/O读写、数据库访问等都应该采用异步的方式，不能有“只是一个很小的文件”之类的想法，防微杜渐。
### 合理优化布局，避免OverDraw
### 合理优化内存
### 正确使用异步
>再次强调一遍：耗时操作不能都直接随意交给异步，不正确的异步使用方式反而会加剧卡顿。

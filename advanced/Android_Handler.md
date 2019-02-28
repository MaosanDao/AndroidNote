# 通过问题学习Android Handler机制([摘](http://www.apkbus.com/blog-977631-79570.html))
***
## 剖析主线程Looper的创建和循环
>Android的程序入口是在main函数中，主线程Looper的创建也是在这里完成的
#### ActivityThread -> Main()
```java
public static void main(){
        // step1: 创建主线程Looper对象
        Looper.prepareMainLooper();
        
        ActivityThread thread = new ActivityThread();
        // 绑定应用进程，布尔标记是否为系统进程
        thread.attach(false);
        // 实例化主线程 Handler
        if(sMainThreadHandler == null){
           sMainThreadHandler = thread.getHandler();
        }
        // step2: 开始循环
        Loop.loop();

        throw new RuntimeException("Main thread loop unexpectedly exited");
}
```
>其中，prepareMainLooper()方法就是用来创建Looper的。
#### 开始创建主线程Looper
>prepareMainLooper的具体实现
```java
private static Looper sMainLooper;  // guarded by Looper.class

public static void prepareMainLooper(){
        // step1: 调用本类 prepare 方法
        prepare(false);
        // 线程同步，如果变量 sMainLooper 不为空抛出主线程 Looper 已经创建
        synchronized (Looper.class) {
            if (sMainLooper != null) {
                throw new IllegalStateException("The main Looper has already been prepared.");
            }
            // step2: 调用本类 myLooper 方法
            sMainLooper = myLooper();
        }
}
```
>有上述代码看出，prepare()方法就是创建Looper的方法，而myLooper()则是获得Looper的方法
#### 如何创建Looper
```java
// ThreadLocal 为每个线程保存单独的变量
static final ThreadLocal<Looper> sThreadLocal = new ThreadLocal<Looper>();
// Looper 类的 MessageQueue 变量
final MessageQueue mQueue;
// quitAllowed 是否允许退出，这里是主线程的 Looper 不可退出
private static void prepare(boolean quitAllowed) {
        // 首先判定 Looper 是否存在
        if(sThreadLocal.get() != null){
                throw new RuntimeException("Only one Looper may be created per thread");
        }
        // 保存线程的副本变量
        sThreadLoacal.set(new Looper(quitAllowed));
}

private Looper(boolean quitAllowed) {
    mQueue = new MessageQueue(quitAllowed);
    mThread = Thread.currentThread();
}
```
>在上述代码中，系统通过ThreadLocal来存储主线程的Looper对象，而ThreadLocal本身可以看做是一个存储线程的变量。

>ThreadLocal通过get/set方法来获取主线程Looper，而在上述代码中，具体是通过：
```java
//来保存主线程的 Looper 对象
sThreadLoacal.set(new Looper(quitAllowed));

//实际调用了 sThreadLocal.get() 方法来获得Looper线程
myLooper()；
```
#### 创建Looper整个过程
> ActivityThread --> main() --> prepareMainLooper() --> prepare(false) --> sThreadLoacal.set(new Looper(quitAllowed)) --> myLooper()
* 入口ActivityThread --> main()
* prepareMainLooper开始准备创建Looper
* 使用prepare(false)来创建Looper
* 使用ThreadLoacal来存储Looper对象
* 存储/使用，sThreadLoacal.set(new Looper(quitAllowed))/myLooper()
#### 问题解答
* 为何在主线程能够使用Handler？
>因为主线程创建了Looper对象并开启了消息循环
* Looper如何绑定MessageQueue的？Looper创建MessageQueue的过程?
>在上述代码中，会有一个Looper的成员变量mQueue，它是Looper默认保存的MessageQueue对象，而在Looper的构造方法中，会将直接创建的MessageQueue赋值为mQueue对象，那么它们就进行了绑定。
#### 开始循环处理消息
>开始循环则是通过上述的main方法中的Loop.loop()开始的
```java
public static void loop() {
    // step1: 获取当前线程的 Looper 对象
    final Looper me = myLooper();
    if (me == null) {
        throw new RuntimeException("No Looper; Looper.prepare() wasn't called on this thread.");
    }
    // step2: 获取 Looper 保存的 MessageQueue 对象，这就是上述问题2中的MessageQueue
    final MessageQueue queue = me.mQueue;

    ...
    // step3: 循环读取消息，如果有则调用消息对象中储存的 handler 进行发送
    for (;;) {
        Message msg = queue.next(); // 提取下一个消息，想想消息从哪里来的？怎么插入的？
        if (msg == null) {
            // No message indicates that the message queue is quitting.
            return;
        }
        ...
        try {
            // step4: 使用 Message 对象保存的 handler 对象处理消息
            //dispatchMessage这个方法最终会调用Handler的handleMessage(msg)方法
            msg.target.dispatchMessage(msg);//msg.target想想何时被赋值的，也就是Handler和Message是什么时候绑定的？
            end = (slowDispatchThresholdMs == 0) ? 0 : SystemClock.uptimeMillis();
        } finally {
            if (traceTag != 0) {
                Trace.traceEnd(traceTag);
            }
        }
        ...
        msg.recycleUnchecked();
    }
}
```
>如何分发的？(dispatchMessage方法)
```java
public void dispatchMessage(Message msg) {
    if (msg.callback != null) {
        handleCallback(msg);
    } else {
        if (mCallback != null) {
            if (mCallback.handleMessage(msg)) {
                return;
            }
        }
        handleMessage(msg);
    }
}

private static void handleCallback(Message message) {
   message.callback.run();
}

//这就是我们需要实现的
public void handleMessage(Message msg) {
}
```
>由上述代码中可以看出分发优先级：
* Message 的回调方法：message.callback.run(); 优先级最高
* Handler 的回调方法：mCallback.handleMessage(msg)优先级次于上方
* Handler 的回调方法：handleMessage() 优先级最低
#### Handler的创建和作用
##### Handler发送消息的过程
>引出问题？
* MessageQueue的消息从哪里来？Handler如何往MessageQueue中插入消息？
* msg.target是




































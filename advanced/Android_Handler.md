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
* msg.target是是什么时候被赋值的？message怎么和handler进行绑定的？
##### Handler 发送消息
>Handler --> sendMessage(Message msg)
```java
final MessageQueue mQueue;

public final boolean sendMessage(Message msg){
    return sendMessageDelayed(msg, 0);
}
// 发送延时消息
public final boolean sendMessageDelayed(Message msg, long delayMillis){
    if (delayMillis < 0) {
        delayMillis = 0;
    }
    return sendMessageAtTime(msg, SystemClock.uptimeMillis() + delayMillis);
}
// 指定时间发送消息
public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
    MessageQueue queue = mQueue;
    if (queue == null) {
        RuntimeException e = new RuntimeException(
                this + " sendMessageAtTime() called with no mQueue");
        Log.w("Looper", e.getMessage(), e);
        return false;
    }
    return enqueueMessage(queue, msg, uptimeMillis);
}
// 处理消息，赋值 Message 对象的 target，消息队列插入消息
private boolean enqueueMessage(MessageQueue queue, Message msg, long uptimeMillis) {
    msg.target = this;//这个的this就是handler，也就是将handler赋值给了message的target
    if (mAsynchronous) {
        msg.setAsynchronous(true);
    }
    return queue.enqueueMessage(msg, uptimeMillis);//这里是将msg插入到了messagequeue中
}
```
>以上代码中，解决的上述两个问题
* msg.target = this; 这个的this就是handler，也就是将handler赋值给了message的target
* queue.enqueueMessage(msg, uptimeMillis); 这里是将msg插入到了messagequeue中
##### Handler的创建
>引出问题
* Handler是如何绑定MessageQueue的？
```java
// 无参构造器
public Handler() {
     this(null, false);
}

public Handler(Callback callback, boolean async) {
    if (FIND_POTENTIAL_LEAKS) {
        final Class<? extends Handler> klass = getClass();
        if ((klass.isAnonymousClass() || klass.isMemberClass() || klass.isLocalClass()) &amp;&amp;
                (klass.getModifiers() &amp; Modifier.STATIC) == 0) {
            Log.w(TAG, "The following Handler class should be static or leaks might occur: " +
                    klass.getCanonicalName());
        }
    }
    // step1:获取当前线程 Looper
    //myLooper()方法其实调用了sThreadLocal.get()来获取looper，也就侧方面说明了Handler必须在Looper线程中使用
    mLooper = Looper.myLooper();
    if (mLooper == null) {
        throw new RuntimeException(
                "Can't create handler inside thread that has not called Looper.prepare()");
    }
    // step2:获取 Looper 对象绑定的 MessageQueue 对象并赋值给 Handler 的 mQueue
    mQueue = mLooper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}
```
>上述代码中，在Handler的初始化中，就是使用mQueue = mLooper.mQueue来将Looper线程的MessageQueue和Handler进行绑定

>由于Handler和Looper可以看做使用的同一个MessageQueue，所以Handler和Looper可以共享消息队列。具体流程如下：
* Handler发送消息(用mQueue往消息里面插入msg)
* Looper可以方便的循环在mQueue中查询消息
* 如果查询到消息，那么久使用Message对象绑定的Handler对象target处理消息。否则堵塞。
##### 新的问题
>关于Handler，在任何地方使用new Handler那么它是处于哪一个线程下？
```java
//关于这个问题，在上述代码中可以看出，如果这样创建：
Handler handler = new Handler();
//那么就是通过Looper.myLooper()来获取Looper对象，也就是说，在哪个线程创建handler，就是在哪个线程上进行的
```
>如果传递Looper对象呢？
```java
//这是传递Looper的Handler构造韩式
public Handler(Looper looper) {
    this(looper, null, false);
}
public Handler(Looper looper, Callback callback) {
    this(looper, callback, false);
}
// 第一个参数是 looper 对象，第二个 callback 对象，第三个消息处理方式（是否异步）
public Handler(Looper looper, Callback callback, boolean async) {
    mLooper = looper;//这里可以看出，传递的looper在哪个线程，handler就在哪个线程上进行
    mQueue = looper.mQueue;
    mCallback = callback;
    mAsynchronous = async;
}
```
##### 一个完整的大体流程
```java
new Thread() {
    @Override
    public void run() {
        // step1
        Looper.prepare();
         // step2
        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"HandlerTest",Toast.LENGTH_SHORT).show();
                        }
                    });
                     // step5
                    Looper.myLooper().quit();
                }
            }
        };
         // step3
        handler.sendEmptyMessage(1);
         // step4
        Looper.loop();
    }
}.start();
```
* Step1
>调用 Looper.prepare(); 为当前线程创建 Looper 对象，同时也就创建了 MessageQueue，之后将该线程的 Looper 对象保存在 ThreadLocal 中。注意这里的一切操作都在子线程中，如果不调用 Looper.prepare() 就使用 Handler 会报错
* Step2
>创建 Handler 对象，覆写 handleMessage 处理消息，等待该 Handler 发送的消息处理时会调用该方法
* Step3
>使用 handler 发送消息，这里只是示例，毕竟自己给自己发送消息没啥必要。发送的过程中会将自己赋值给 msg.target，然后再将消息插入到 Looper 绑定的 MessageQueue 对象中
* Step4
>调用 Looper.loop(); 首先获取当前线程的 Looper 对象，根据 Looper 对象就可以拿到 Looper 保存的 MessageQueue 对象 mQueue。有了 MessageQueue 对象就可以 for 循环获取它保存的消息 Message 对象，如果消息不存在就返回 null 阻塞，反之则使用 Message 中保存的 Handler：msg.target 来处理消息，最终调用 handleMessage 也就是之前覆写的方法来处理消息
* Step5
>逻辑处理完毕以后，应在最后使用 quit 方法来终止消息循环，否则这个子线程就会一直处于等待的状态，而如果退出Looper以后，这个线程就会立刻终止，因此建议不需要的时候终止Looper
## 总结
#### Handler、Looper、MessageQueue、Message
* Handler
>Handler 用来发送消息，创建时先获取默认或传递来的 Looper 对象()，并持有 Looper 对象包含的 MessageQueue，发送消息时使用该 MessageQueue 对象来插入消息并把自己封装到具体的 Message 中；
```java

// step1:获取当前线程 Looper
mLooper = Looper.myLooper();

// step2:获取 Looper 对象绑定的 MessageQueue 对象并赋值给 Handler 的 mQueue
mQueue = mLooper.mQueue;

//插入消息和封装自己到msg中去
msg.target = this;
if (mAsynchronous) {
    msg.setAsynchronous(true);
}
return queue.enqueueMessage(msg, uptimeMillis);
```
* Looper
>用来为某个线程作消息循环。Looper 持有一个 MessageQueue 对象 mQueue，这样就可以通过循环来获取 MessageQueue 所维护的 Message。如果获取的 MessageQueue 没有消息时，便阻塞在 loop 的queue.next() 中的 nativePollOnce() 方法里，反之则唤醒主线程继续工作，之后便使用 Message 封装的 handler 对象进行处理
* MessageQueue
>是一个消息队列，它不直接添加消息，而是通过与 Looper 关联的 Handler 对象来添加消息。
* Message
>包含了要传递的数据和信息，同时handler也会被赋值在其中（msg.target）
## 说明
>以上均为摘录所得，只为记录平时个人学习记录。不作任何商用。

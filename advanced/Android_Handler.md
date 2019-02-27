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



































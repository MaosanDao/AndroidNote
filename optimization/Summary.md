# Android性能优化大解密
***
## 内存优化
### 释义
```
指程序申请内存后，当该内存不需要再使用的时候，但是又无法被释放且归还给程序的现象。

对程序有何种影响？
  因为Android系统为每个程序分配的可用内存有限，所以长时间发生泄漏，内存使用紧张。就会出现OOM的情况。
```
### 什么情况下会发生泄漏？
```
1.本应该被回收的对象，未能被回收。
2.因为某些原因不能被回收。

本质问题：
  持有者的生命周期 > 被引用者生命周期，当后者被结束生命周期的时候，前者无法被回收。
  
注意：
  从机制上的角度来说，由于 Java存在垃圾回收机制（GC），理应不存在内存泄露；
  出现内存泄露的原因仅仅是外部人为原因 = 无意识地持有对象引用，
  使得持有引用者的生命周期 > 被引用者的生命周期。
```
### Android内存管理机制
![AndroidM](https://upload-images.jianshu.io/upload_images/5258053-403e0ecb5737e763.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)
### 针对于对象、变量的内存策略
![img1](https://upload-images.jianshu.io/upload_images/5258053-9c0b664ad81e4345.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1000/format/webp)
#### 实例解析
```java
public class Sample {  
    // 该实例的成员变量s1、mSample1也存放在栈中
    int s1 = 0;
    Sample mSample1 = new Sample();   
    
    // 方法中的局部变量s2、mSample2存放在 栈内存
    public void method() {        
        int s2 = 0;
        // 变量mSample2所指向的对象实例存放在 堆内存
        Sample mSample2 = new Sample();
    }
}
// 变量mSample3所指向的对象实例存放在堆内存
// 该实例的成员变量s1、mSample1也存放在堆内存中
Sample mSample3 = new Sample();
```
***
## 常见的内存泄漏原因和解决方法
```
常见的内存泄漏包括：
  1.集合类
  2.Static关键字修饰的成员变量
  3.非静态内部类和匿名类
  4.资源对象使用后未被关闭
```
### 集合类泄漏
#### 原因
```
集合添加元素后，仍引用这集合元素的对象，导致该集合对象无法被回收，从而导致了内存泄漏
```
```java
// 通过 循环申请Object 对象 & 将申请的对象逐个放入到集合List
//集合对象
List<Object> objectList = new ArrayList<>(); 

for (int i = 0; i < 10; i++) {
    Object o = new Object();
    objectList.add(o);
    // 虽释放了集合元素引用的本身：o=null
    o = null;
}
// 但集合List 仍然引用该对象，故垃圾回收器GC 依然不可回收该对象
```
```
解决方案：
  该集合将元素添加且使用完毕的时候，需要将集合中的元素进行删除
```
```java
// 释放objectList
objectList.clear();
objectList=null;
```
### Static关键字修饰的成员变量
```
注意：
  被Static修饰的成员变量的生命周期 == 应用程序的生命周期

泄漏原因：
  若使被 Static 关键字修饰的成员变量 引用耗费资源过多的实例（如Context），
  则容易出现该成员变量的生命周期 > 引用实例生命周期的情况，当引用实例需结束生命周期销毁时，
  会因静态变量的持有而无法被回收，从而出现内存泄露
  
  也就是说，成员被Static修饰后，生命周期变的很长，那么该成员变量假如引用耗费资源很多的实例，
  那么该引用实例被销毁后，因为Static还在引用，从而导致了无法释放内存，进而导致了内存泄漏。
  
实例讲解：
```
```java
public class ClassName {
 // 定义1个静态变量
 private static Context mContext;
 //...
 //引用的是Activity的context
 mContext = context; 

// 当Activity需销毁时，由于mContext等于静态所以它的生命周期等于应用程序的生命周期，故Activity无法被回收，从而出现内存泄露
}
```
```
解决方案：
  1.尽量避免Static成员变量引用资源比较多的实例。
  2.使用弱引用来持有想要的实例。
```
#### 典型实例（单例模式）
```
泄漏原因：
  若1个对象已不需再使用而单例对象还持有该对象的引用，那么该对象将不能被正常回收，从而导致内存泄漏。
```
```java
// 由于单例一直持有该Activity的引用（直到整个应用生命周期结束），即使该Activity退出，该Activity的内存也不会被回收
// 特别是一些庞大的Activity，此处非常容易导致OOM

public class SingleInstanceClass {    
    //注意此单例为Static修饰的，所以生命周期 = 程序的生命周期
    private static SingleInstanceClass instance;    
    //即使被引用的对象被销毁了后，该单例对象仍持有该实例，故内存泄漏
    private Context mContext;    
    private SingleInstanceClass(Context context) {        
        this.mContext = context; // 传递的是Activity的context
    }  
  
    // 创建单例时，需传入一个Context
    public SingleInstanceClass getInstance(Context context) {        
        if (instance == null) {
            instance = new SingleInstanceClass(context);
        }        
        return instance;
    }
}

//解决方法：
  //将引用的Context转为Application的Context，因为Application的生命周期 = 整个应用的生命周期
```
### 非静态的内部类/匿名类
#### 非静态内部类的实例 = 静态
```
泄漏原因：
  非静态内部类创建一个静态的成员变量，会因为非静态类的内部会持有默认持有外部类的引用，
  当外部类释放的时候，内部类因为是静态成员变量，所以无法得到释放。
  
  注意：是默认！！！非静态内部类持有外部内的引用。
```
```java
// 背景：
   a. 在启动频繁的Activity中，为了避免重复创建相同的数据资源，会在Activity内部创建一个非静态内部类的单例
   b. 每次启动Activity时都会使用该单例的数据

public class TestActivity extends AppCompatActivity {  
    
    // 非静态内部类的实例的引用
    // 注：设置为静态  
    public static InnerClass innerClass = null; 
   
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {        
        super.onCreate(savedInstanceState);   

        // 保证非静态内部类的实例只有1个
        if (innerClass == null)
            innerClass = new InnerClass();
    }

    // 非静态内部类的定义 -- 默认会持有外部类的引用
    private class InnerClass {        
        //...
    }
}

// 造成内存泄露的原因：
    // a. 当TestActivity销毁时，因非静态内部类单例的引用（innerClass）的生命周期 = 应用App的生命周期、持有外部类TestActivity的引用
    // b. 故 TestActivity无法被GC回收，从而导致内存泄漏
```
```
解决方法:
  1.将非静态内部类改为静态内部类（不再默认持有外部类的引用）
  2.将内部类抽离为一个单例
  3.尽量避免创建一个非静态内部类的静态成员变量。
```
#### 多线程（AsyncTask、实现Runable接口、继承Thread类）
```
注意：
  线程类属于非静态内部类或者匿名类。

原因：
  当工作线程正在执行任务的时候，此时外部类需要销毁。但是因为是非静态的内部类，所以默认持有外部类的引用。
  从而导致无法释放，故内存泄漏。
```
```java
//方式一：内部类
public class MainActivity extends AppCompatActivity {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // 通过创建的内部类 实现多线程
            new MyThread().start();

        }
        
        // 自定义的Thread子类 --- 相当于非静态内部类
        private class MyThread extends Thread{
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Log.d(TAG, "执行了多线程");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//方式二：匿名类
public class MainActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 通过匿名内部类实现多线程
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Log.d(TAG, "执行了多线程");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
}

//分析原因：
  1.工作线程Thread属于非静态内部类/匿名类，所以运行时候默认持有外部类的引用。
  2.当线程工作的时候，外部内进行销毁。
  3.由于此时内部类持有外部类的引用，且生命周期较长，故无法被回收，造成了内存泄漏。
```
```
造成的原因：
  1.存在 ”工作线程实例持有外部类引用“ 的引用关系
  2.工作线程实例的生命周期 > 外部类的生命周期，即工作线程仍在运行而外部类需销毁
```
```java
public class MainActivity extends AppCompatActivity {

        public static final String TAG = "carson：";
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // 通过创建的内部类 实现多线程
            new MyThread().start();
        }
        // 方式1：设置为：静态内部类
        private static class MyThread extends Thread{
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    Log.d(TAG, "执行了多线程");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

//方式2，在外部类销毁的时候，强制将线程停止
@Override
protected void onDestroy() {
    super.onDestroy();
    Thread.stop();
    // 外部类Activity生命周期结束时，强制结束线程
}
```
### Handler内存泄漏
```java
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "carson：";
    private Handler showhandler;

    // 主线程创建时便自动创建Looper & 对应的MessageQueue
    // 之后执行Loop()进入消息循环
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. 实例化自定义的Handler类对象->>分析1
        //注：此处并无指定Looper，故自动绑定当前线程(主线程)的Looper、MessageQueue
        showhandler = new FHandler();

        // 2. 启动子线程1
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // a. 定义要发送的消息
                Message msg = Message.obtain();
                msg.what = 1;// 消息标识
                msg.obj = "AA";// 消息存放
                // b. 传入主线程的Handler & 向其MessageQueue发送消息
                showhandler.sendMessage(msg);
            }
        }.start();

        // 3. 启动子线程2
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // a. 定义要发送的消息
                Message msg = Message.obtain();
                msg.what = 2;// 消息标识
                msg.obj = "BB";// 消息存放
                // b. 传入主线程的Handler & 向其MessageQueue发送消息
                showhandler.sendMessage(msg);
            }
        }.start();

    }
    //内部类 --- 默认引用了外部类
    // 分析1：自定义Handler子类
    class FHandler extends Handler {

        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "收到线程1的消息");
                    break;
                case 2:
                    Log.d(TAG, " 收到线程2的消息");
                    break;


            }
        }
    }
    
    //方式2：匿名内部类 --- 默认引用了外部类
    showhandler = new  Handler(){
        // 通过复写handlerMessage()从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        Log.d(TAG, "收到线程1的消息");
                        break;
                    case 2:
                        Log.d(TAG, " 收到线程2的消息");
                        break;
                }
            }
    };

}
```
```
从以上代码可以看出：
  1.当Handler还有正在处理的消息的时候，内部的Message持有Handler实例。
  2.Handler实现的方式为非静态内部类/匿名类，故持有外部类的引用。
  
泄漏原因：
  当外部类被销毁的时候，因为Handler还在处理消息。故Message持有Handler的实例，且Handler又持有外部类的实例。
  所以，导致了外部类无法被回收。故造成了内存泄漏。
  
  Handler在处理消息的时候，生命周期 > 外部类的生命周期。外部内销毁的时候，无法被回收。
```
#### 引用关系图
![imag2](https://upload-images.jianshu.io/upload_images/5258053-7cf66eda1807187c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/770/format/webp)
```
解决方法：
  1.将Handler初始化，改为静态内部类 + Activity的软引用。
  2.外部内销毁的时候，将Handler内的消息全部清空。
```
```java
//静态内部类 + 软引用
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "carson：";
    private Handler showhandler;

    // 主线程创建时便自动创建Looper & 对应的MessageQueue
    // 之后执行Loop()进入消息循环
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //1. 实例化自定义的Handler类对象->>分析1
        //注：
            // a. 此处并无指定Looper，故自动绑定当前线程(主线程)的Looper、MessageQueue；
            // b. 定义时需传入持有的Activity实例（弱引用）
        showhandler = new FHandler(this);

        // 2. 启动子线程1
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // a. 定义要发送的消息
                Message msg = Message.obtain();
                msg.what = 1;// 消息标识
                msg.obj = "AA";// 消息存放
                // b. 传入主线程的Handler & 向其MessageQueue发送消息
                showhandler.sendMessage(msg);
            }
        }.start();

        // 3. 启动子线程2
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // a. 定义要发送的消息
                Message msg = Message.obtain();
                msg.what = 2;// 消息标识
                msg.obj = "BB";// 消息存放
                // b. 传入主线程的Handler & 向其MessageQueue发送消息
                showhandler.sendMessage(msg);
            }
        }.start();

    }

    // 分析1：自定义Handler子类
    // 设置为：静态内部类
    private static class FHandler extends Handler{
        // 定义 弱引用实例
        private WeakReference<Activity> reference;

        // 在构造方法中传入需持有的Activity实例
        public FHandler(Activity activity) {
            // 使用WeakReference弱引用持有Activity实例
            reference = new WeakReference<Activity>(activity); 
        }

        // 通过复写handlerMessage() 从而确定更新UI的操作
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Log.d(TAG, "收到线程1的消息");
                    break;
                case 2:
                    Log.d(TAG, " 收到线程2的消息");
                    break;
            }
        }
    }
}

//外部类销毁的时候，将消息清空
//为了完成Handler所有消息的处理，不建议这种方式
@Override
protected void onDestroy() {
    super.onDestroy();
    mHandler.removeCallbacksAndMessages(null);
    // 外部类Activity生命周期结束时，同时清空消息队列 & 结束Handler生命周期
}
```













































































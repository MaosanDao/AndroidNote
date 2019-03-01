# 内存泄漏的含义以及大部分的处理方式

## 内存泄漏
### 预备知识
#### Java中的内存分配
* 静态存储区：编译时就分配好，在整个程序运行期间都在，它主要存在静态数据和常量
* 栈区：当方法执行时，会在栈去内存中创建方法体背部的局部变量，方法结束后自定释放内存
* 堆区：通常存放new出来的对象，由GC负责回收
#### Java中的四种引用类型
* 强引用
```java
User user = new User();
```
对于强引用的回收，JVM是不会让GC去主动回收具有强引用的对象，而我们需要回收强引用，可以通过置空，object = null；那么GC就会去回收该强引用对象
* 软引用
```java
SoftReference<String> softRef = new SoftReference<String>();
```
当一个对象只具有软引用的时候，内存空间足够的话，GC不会进行回收操作。如果内存不够的话，就会进行回收操作
* 弱引用
```java
WeakReference<String> softRef = new WeakReference<String>();
```
* 虚引用
虚引用必须和引用队列（ReferenceQueue）联合使用。当垃圾回收器准备回收一个对象时，如果发现它还有虚引用，就会在回收对象的内存之前，把这个虚引用加入到与之关联的引用队列中。程序可以通过判断引用队列中是否存在该对象的虚引用，来了解这个对象是否将要被回收
#### 内存泄漏的定义
在android里面引起内存泄漏指的是对象的生命周期结束，而该对象依然被其他对象所持有，导致该对象所占内存无法释放
#### 内存泄漏带来的影响
在android里面，出现内存泄漏会导致系统为应用分配的内存会不断减少，从而造成app在运行时会出现卡断(内存占用高时JVM虚拟机会频繁触发GC)，影响用户体验。同时，可能会引起OOM(内存溢出)，从而导致应用程序崩溃
## 常见的内存泄漏操作汇总
### 集合类泄漏
如果某个集合是全局性的变量（比如 static 修饰），集合内直接存放一些占用大量内存的对象（而不是通过弱引用存放），那么随着集合 size 的增大，会导致内存占用不断上升，而在 Activity 等销毁时，集合中的这些对象无法被回收，导致内存泄露。比如我们喜欢通过静态HashMap做一些缓存之类的事，这种情况要小心，集合内对象建议采用弱引用的方式存取，并考虑在不需要的时候手动释放。
### 单例模式（静态Activity的引用）
```java
public class RequestImpl {

    private Context context;
    private static RequestImpl mInstance;

    public static RequestImpl getInstance(Context context) {//假如这里传入的是Activity的this，那 么就会出现内存泄漏，因为，传入Activity如果销毁了，这里的Context就无法进行回收

        if (mInstance == null) {
            mInstance = new RequestImpl(context);
        }
        return mInstance;
    }

    private RequestImpl(Context context) {
        this.context = context;
    }
}
```
#### 解决方法
只要传入application的Context就可以了。即，使用RequestImpl的构造函数里面调用context.getApplicationContext()
### 非静态内部类
非静态内部类造成的内存溢出常常出现在Handler、Thread、Timer Task。这些耗时操作如果在Activity生命周期结束后还在运行的话，可能会造成内存溢出
>非静态内部类会持有外部类的引用，那么如果非静态内部类的实例是静态的，就会长期的维持着外部类的引用，组织被系统回收。解决办法是使用静态内部类。
#### 线程
```java
@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        testInnner();
    }

    private void testInnner() {
        new Thread(new Runnable() {//模拟线程一直运行，假如activity结束的时候，还在运行的话，则会出现内存泄漏
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
```
#### Handler
>Handler导致的内存泄漏也可以被归纳为非静态内部类导致的，Handler内部message是被存储在MessageQueue中的，有些message不能马上被处理，存在的时间会很长，导致handler无法被回收，如果handler是非静态的，就会导致它的外部类无法被回收
解决办法：
* 使用静态handler，外部类引用使用弱引用处理
* 在退出页面时移除消息队列中的消息
解决方法：
```java
MyHandler mHandler = new MyHandler(this);

    static class MyHandler extends Handler {
        WeakReference<MainActivity> activityReference;

        MyHandler(MainActivity activity) {
            activityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = activityReference.get();
            //判断是因为GC是对弱引用进行回收的
            if (activity != null) {
                activity.getTextView().setText("测试");
            }
        }
    }
```
#### 总结
当书写内部类的时候要确保自己的耗时操作在activity结束后没有引用activity对象

### GetSystemService方法
```java
AudioManager mAudioManager= (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
SensorManager mSensorManager =(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
```
注意：在activity生命周期结束的时候，记得要释放，调用unregisterListener方法。
### 资源未关闭造成的内存泄漏
* BroadcastReceiver、ContentObserver没有解除注册
* Cursor、Stream没有close
* 无限循环的动画在Activity退出前没有停止
* 一些其他的该释放或者回收没有被操作。比如自定义属性的TypeArray需要recycle

## 内存泄漏检测工具
* [LeakCanary](https://github.com/square/leakcanary)

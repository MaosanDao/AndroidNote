# HandlerThread相关知识点
```
HandlerThread本质上就是一个普通Thread,只不过内部建立了Looper.
```
## 常规用法
```java
//创建一个HandlerThread
//“xxx”代表线程的名字
mThread = new HandlerThread("xxx")
//启动
mThread.start()
//退出
mThread.quit()
```
### 退出循环的方法
```
1.quit()
2.quitSafely()

相同点：
  1.将不再接收新的事件加入队列
不同点：
  quit():
    实际上执行了MessageQueue中的removeAllMessagesLocked方法，该方法的作用是把MessageQueue消息池中所有的消息全部清空，
    无论是延迟消息（延迟消息是指通过sendMessageDelayed或通过postDelayed等方法发送的需要延迟执行的消息）还是非延迟消息。
  quitSafely():
    实际上执行了MessageQueue中的removeAllFutureMessagesLocked方法，通过名字就可以看出，
    该方法只会清空MessageQueue消息池中所有的延迟消息，并将消息池中所有的非延迟消息派发出去让Handler去处理，
    quitSafely相比于quit方法安全之处在于清空消息之前会派发所有的非延迟消息。
```
### 小例子
```java
public class MainActivity extends AppCompatActivity {

    private HandlerThread myHandlerThread ;
    private Handler handler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread( "handler-thread") ;
        //开启一个线程
        myHandlerThread.start();
        //在这个线程中创建一个handler对象
        //myHandlerThread.getLooper火获取Looper
        handler = new Handler(myHandlerThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                Log.d( "handler " , "消息： " + msg.what + "  线程： " + Thread.currentThread().getName()  ) ;

            }
        };

        //在主线程给handler发送消息
        handler.sendEmptyMessage(1) ;

        new Thread(new Runnable() {
            @Override
            public void run() {
             //在子线程给handler发送数据
             handler.sendEmptyMessage(2) ;
            }
        }).start() ;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //释放资源
        myHandlerThread.quit() ;
    }
}

//执行结果为：
/com.app D/handler: 消息： 1  线程： handler-thread
/com.app D/handler: 消息： 2  线程： handler-thread
```
### 总结
```
1.HandlerThread将loop转到子线程中处理，减轻了主线程的压力，使界面更加流畅。
2.开启一个线程起到多个线程的作用，但是任务是串行执行的，所以需要一个一个等待的执行。
3.HandlerThread拥有自己的消息队列，不会干扰或者阻塞UI线程
```
### 转载并整理至
* [Android HandlerThread总结使用](https://www.cnblogs.com/zhaoyanjun/p/6062880.html)

# 线程的不同创建方式和优缺点
## AsyncTask
### 定义
AsyncTask是Android的一个轻量级异步类，可以自定义类并继承AsyncTask,实现异步任务处理操作。并且AsyncTask提供了接口返回异步操作的进度，最后会返回结果到UI主线程中
### 优缺点
* 优点：简单快捷，过程可以控制
* 缺点：处理多个异步任务时，比较复杂
### 参数介绍
```java
public abstract class AsyncTask<Params,Process,Result>{}
```
* Params：启动任务时输入的执行参数，一般为一个网络地址
* Process：异步任务执行的进度
* Result：异步任务执行后的结果类型
### 代码实例
```Java
public class MyAsyncTask extends AsyncTask<String,Integer,Bitmap> {
    /**
     * 任务开启前进行调用
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    /**
     * 这个方法中所有的代码都在子线程中进行
     * 不可以进行ui操作
     * 在执行过程中，可以通过publishProgress(Progress... values)更新进度消息
     */
    @Override
    protected Bitmap doInBackground(String... strings) {
        return null;
    }

    /**
     * 在调用publishProgress后，此方法被执行，直接将进度更新到UI组件上
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    /**
     * 收尾工作，可以将doInBackground返回的数据进行呈现，然后将一些不用的组件进行关闭
     */
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }
}
```
## Handler and Message
### Handler的概念
Handler是Android给我们提供来更新UI的一套机制，也是一套消息处理的机制，我们可以发消息，也可以处理消息
### Handler的相关概念
* Message：可以理解为线程间通讯的数据单元，例如后台线程在处理数据完毕后需要更新UI，则可以发送一条包含信息的Message给UI线程
* Message Queue：用来存放Handler发布的消息，按照先进先出的规则
* Handler：Message主要的处理者，负责将Message添加到队列中以及对消息队列中的消息进行处理
* Looper：扮演Message和Handler之间的桥梁，循环取出Message Queue中的Message，并交付为Handler进行处理
### 相关方法
```java
post(Runable)
postAtTime(Runnable,long)
postDelayed(Runnable,long)
sendEmptyMessage(int)
sendMessage(Message)
sendMessageAtTime(Message,long)
sendMessageDelayed(Message,long)
```
### 关于Message
Message可传递的参数:
* arg1、arg2（整数类型）
* obj（传递Object类型）
* what（自定义）：用户自定义的消息代码，这样接受者才知道是代表是哪一个消息。每个Handler各自包含自己的消息代码，互不干扰
* Bundle
#### 注意
创建Message最好用obtain()来创建，因为它创建了Message实例
### 代码实例
#### 发送消息
```java
     class MyThread implements Runnable { 
        public void run() { 
 
            try { 
                Thread。sleep(10000); 
            } catch (InterruptedException e) { 
                // TODO Auto-generated catch block 
                e。printStackTrace(); 
            } 

            Message msg = new Message(); 
            Bundle b = new Bundle();// 存放数据 
            b.putString("color"， "我的"); 
            msg.setData(b); 
 
            MyHandlerActivity.this.myHandler.                                     	   
            sendMessage(msg); // 向Handler发送消息，更新UI 
 
        } 
    } 
```
#### 接收消息
```java
class MyHandler extends Handler { 
        // 子类必须重写此方法，接受数据 
        @Override 
        public void handleMessage(Message msg) { 
            Log.d(“MyHandler"， "handleMessage。。。。。。"); 
            super.handleMessage(msg); 
            // 此处可以更新UI 
            Bundle b = msg.getData(); 
            String color = b.getString("color"); 
            MyHandlerActivity.this.button.append(color); 
        } 
  } 
```
## ThreadPoolExecutor(线程池)
### 含义
ThreadPoolExector提供了一组线程池，可以管理多个线程并发执行
### 方法介绍
```java
Executors.newFixedThreadPool();
```
创建一个定长的线程池，每提交一个任务就创建一个线程，直到达到池的最大长度，这时线程会保持长度不再变化
```java
Executors.newCachedThreadPool();
```
创建一个可以缓存的线程池，如果当前线程池的长度超过了处理的需要时，它可以灵活的回收空闲的线程，
当需要增加线程时，它可以灵活的添加新的线程，而不会对线程池的长度作为限制。
```java
Executors.newScheduledThreadPool();
```
创建一个定长的线程池，而且支持定时的以及周期性的任务执行，类似于timer
```java
Executors.newSingleThreadExecutor();
```
创建一个单线程化的executor，它只创建一个唯一的任务线程来执行任务
### 代码实例
#### FixedThreadPool
特点：只有核心线程数，并且没有超时限制，因此核心线程即使闲置，也不会被回收，因此能更快的响应外界的请求
```java
ExecutorSevice fixedThreadPool = Executors.newFixedThreadPool(num);
fixedThreadPool.executor(runnable对象)；

ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);
for (int i = 0; i < 10; i++) {
	final int index = i;
	fixedThreadPool.execute(new Runnable() {
 
		@Override
		public void run() {
			try {
				System.out.println(index);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
}
```
#### CachedThreadPool
特点：没有核心线程数，非核心线程数量没有限制，超时为60秒。适用于大量耗时较少的任务，当线程闲置超过60秒后就会被系统回收掉，当所有的线程都被系统回收后，它几乎不占系统资源
```java
ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
for (int i = 0; i < 10; i++) {
	final int index = i;
	try {
		Thread.sleep(index * 1000);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
 
	cachedThreadPool.execute(new Runnable() {
 
		@Override
		public void run() {
			System.out.println(index);
		}
	});
}
```
#### ScheduledTreadPool
特点：核心线程是固定的，非核心线程数量没有限制，没有超时机制，主要用于执行定时任务和具有周期性的重复任务
```java
ExecutorService scheduledTreadPool = Executors.newScheduledThreadPool(int corePoolSize);
scheduledTreadPool.execute(runnable对象，2000，TimeUnit. MILLISECONDS)；

//延迟三秒执行
ScheduledExecutorService scheduledThreadPool = Executors.newScheduledThreadPool(5);
scheduledThreadPool.schedule(new Runnable() {
 
	@Override
	public void run() {
		System.out.println("delay 3 seconds");
	}
}, 3, TimeUnit.SECONDS);

//延迟1秒后每3秒执行一次
scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
 
	@Override
	public void run() {
		System.out.println("delay 1 seconds, and excute every 3 seconds");
	}
}, 1, 3, TimeUnit.SECONDS);
```
#### SingleThreadExecutor
特点：只有一个核心线程，并没有超时机制。意义在于同一所有外界任务到一个线程中，这使得在这些任务之间不需要处理线程同步的问题
```java
ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
for (int i = 0; i < 10; i++) {
	final int index = i;
	singleThreadExecutor.execute(new Runnable() {
 
		@Override
		public void run() {
			try {
				System.out.println(index);
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	});
}
```
## IntentService
### 定义
IntentService是Service的子类，根据需要处理异步请求，用Intent来启动
### 使用方法
IntentService类的存在是为了简化这种模式，要使用它，扩展IntentSerivce并实现onHandleIntent(Intent)。IntentService将收到Intents，启动一共工作线程，并根据需要停止该服务
* 继承IntentService
* 实现不带参数的构造方法，并且调用父类的IntentService的构造方法
* 在注册文件中注册该Service
* 实现onHandleIntent方法：在这个方法中可以根据intent来区分任务
### 代码实例
```java
public class MyIntentService extends IntentService {

    private static final String TAG = "wangpei";

    public static final String ACTION_DOWN_IMG = "img";
    public static final String ACTION_DOWN_VID = "vid";

    /**
     * 这个必须要
     */
    public MyIntentService(){
        super("MyIntentService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.i(TAG, "任务开始 thread:"+Thread.currentThread());
        String action = intent.getAction();
        if(action.equals(ACTION_DOWN_IMG)){
            Log.i(TAG, "ACTION_DOWN_IMG");
            for(int i = 0; i < 100; i++){
                try{ //模拟耗时操作
                    Thread.sleep(50);
                }catch (Exception e) {
                }
            }
        }else if(action.equals(ACTION_DOWN_VID)){
            Log.i(TAG, "ACTION_DOWN_VID");
            for(int i = 0; i < 100; i++){
                try{ //模拟耗时操作
                    Thread.sleep(70);
                }catch (Exception e) {
                }
            }
        }
        Log.i(TAG, "任务完成");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }
}
```
### 根据上方代码的log总结
* 如果只启动，不手动销毁的话。那么intentService执行完任务后，会自动销毁
* 如果先启动，在任务未完成的情况下，手动销毁IntentService，会先走onDestroy，但是任务也会相应的完成
* 按顺序相应启动两个任务（第一个完成后，再启动第二个），第一个任务会按顺序完成任务并销毁，第二个任务又会创建IntentService和完成	任务及销毁
* 启动第一个任务在未完成的时候再启动第二个任务，IntentService会按照顺序排队执行onHandleIntent方法。都完成后，会自动销毁
* 先启动第一个任务，在未完成的时候，手动销毁。然后再次穹第二个任务。IntentService会先走onHandleIntent以及onDestroy。然后又会走onHandleIntent，最后任务都完成的时候，会自动销毁
### 总结
IntentService是一个比较便捷的类，它会创建一个线程，多个任务按顺序执行，且不能停止任务。要根据实际情况进行使用。
## 参考文章
>* [Android 消息处理机制（Looper、Handler、MessageQueue,Message)](https://www.jianshu.com/p/02962454adf7)



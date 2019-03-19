# 关于线程的一些常见知识点
## sleep和wait方法的区别
```
异同点：
  
  1.对于sleep()方法，我们首先要知道该方法是属于Thread类中的。而wait()方法，则是属于Object类中的。
  2.sleep()方法导致了程序暂停执行指定的时间，让出cpu该其他线程，但是他的监控状态依然保持者，
    当指定的时间到了又会自动恢复运行状态。
  3.在调用sleep()方法的过程中，线程不会释放对象锁。
  4.调用wait()方法的时候，线程会放弃对象锁，进入等待此对象的等待锁定池。
    只有针对此对象调用notify()方法后本线程才进入对象锁定池准备
```
### 代码示例解析
```java

public class TestD {
    public static void main(String[] args) {
        new Thread(new Thread1()).start();
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Thread2()).start();
    }
    
    private static class Thread1 implements Runnable{
        @Override
        public void run(){
            synchronized (TestD.class) {
            System.out.println("enter thread1...");    
            System.out.println("thread1 is waiting...");
            try {
                //调用wait()方法，线程会放弃对象锁，进入等待此对象的等待锁定池
                TestD.class.wait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("thread1 is going on ....");
            System.out.println("thread1 is over!!!");
            }
        }
    }
    
    private static class Thread2 implements Runnable{
        @Override
        public void run(){
            synchronized (TestD.class) {
                System.out.println("enter thread2....");
                System.out.println("thread2 is sleep....");
                //只有针对此对象调用notify()方法后本线程才进入对象锁定池准备获取对象锁进入运行状态。
                //相当于预备状态
                TestD.class.notify();
                //==================
                //区别
                //如果我们把代码：TestD.class.notify();给注释掉，即TestD.class调用了wait()方法，但是没有调用notify()
                //方法，则线程永远处于挂起状态。
                try {
                    //sleep()方法导致了程序暂停执行指定的时间，让出cpu该其他线程，
                    //但是他的监控状态依然保持者，当指定的时间到了又会自动恢复运行状态。
                    //在调用sleep()方法的过程中，线程不会释放对象锁。
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("thread2 is going on....");
                System.out.println("thread2 is over!!!");
            }
        }
    }
}

//不注释TestD.class.notify()的输出结果
enter thread1...
thread1 is waiting...
enter thread2....
thread2 is sleep....
thread2 is going on....
thread2 is over!!!
thread1 is going on ....
thread1 is over!!!

//如果注释掉notify
enter thread1...
thread1 is waiting...
enter thread2....
thread2 is sleep....
thread2 is going on....
thread2 is over!!!
```
### 结果分析
```
1.wait()方法属于Object类,sleep()属于Thread类；
2.wait()方法释放cpu给其他线程，自己让出资源进入等待池等待；sleep占用cpu，不让出资源；
3.sleep()必须指定时间，wait()可以指定时间也可以不指定；sleep()时间到，线程处于临时阻塞或运行状态；
4.wait()方法会释放持有的锁，不然其他线程不能进入同步方法或同步块，从而不能调用notify(),notifyAll()方法来唤醒线程，产生死锁；
5.sleep方法不会释放持有的锁，设置sleep的时间是确定的会按时执行的；
6.wait()方法只能在同步方法或同步代码块中调用；如果没有设定时间，使用notify()来唤醒；而sleep()能在任何地方调用；

```






























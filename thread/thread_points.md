# 关于线程的一些常见知识点
***
## 目录
* [sleep和wait方法的区别](#sleep和wait方法的区别) 
* [run方法和start方法的区别](#run方法和start方法的区别)
* [volatile关键字的理解](#volatile关键字的理解)
* [如何安全的停止一个线程](#如何安全的停止一个线程) 
  * [使用共享变量来停止线程](#使用共享变量来停止线程)
  * [使用interrupt方法终止线程](#使用interrupt方法终止线程)
  * [为什么不用stop方法终止线程](#为什么不用stop方法终止线程)
  * [interrupt并非会终止线程(处理中断逻辑)](#interrupt并非会终止线程)
***
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
                //进度等待池
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
                //进入预备池
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
***
## run方法和start方法的区别
### 代码实例
```
//线程体内的run方法
public class WelcomThread extends Thread {
    //在该方法中实现线程的任务逻辑
    public void run() {
        //获取当前正在执行的线程名称
        System.out.println(Thread.currentThread().getName());
    }
}

//分别使用线程run方法和start方法
public class TestDemo1 {
    public static void main(String[] args) {
        Thread welcome = new WelcomThread();  //创建线程（动态规划）
        welcome.run();  //直接调用run()方法
        System.out.println(Thread.currentThread().getName());
         Thread welcome1 = new WelcomThread();  //创建线程
         welcome1.start();  //启动线程
    }
}

//结果：
main

main
Thread-1
```
### 结果分析
```
1.如果执行start方法，则会在主线程中重新创建一个新的线程，等得到cpu的时间段后则会执行所对应的run方法体的代码。
2.如果创建了线程对象后，执行run方法，则还是在当前线程中执行，会和调用普通方法一样。按照顺序执行。

所以：

  1.调用线程的start方法是创建了新的线程，在新的线程中执行。
  2.调用线程的run方法是在当前线程中执行该方法，和调用普通方法一样。
  
一句话：
  线程的run()方法是由java虚拟机直接调用的，如果我们没有启动线程（没有调用线程的start()方法）而是在应用代码中直接调用run()方法，
  那么这个线程的run()方法其实运行在当前线程（即run()方法的调用方所在的线程）之中，而不是运行在其自身所创建的线程中，从而违背了创建线程的初衷；
```
***
## volatile关键字的理解
### Java内存模型
#### 可见性
```
可见性，是指线程之间的可见性，一个线程修改的状态对另一个线程是可见的。
也就是一个线程修改的结果。另一个线程马上就能看到。
```
#### 原子性
```
原子是世界上的最小单位，具有不可分割性。比如 a=0；（a非long和double类型）这个操作是不可分割的，那么我们说这个操作时原子操作。
再比如：a++； 这个操作实际是a = a + 1；是可分割的，所以他不是一个原子操作。
非原子操作都会存在线程安全问题，需要我们使用同步技术（sychronized）来让它变成一个原子操作。
```
#### 有序性
```
Java 语言提供了 volatile 和 synchronized 两个关键字来保证线程之间操作的有序性，
volatile 是因为其本身包含“禁止指令重排序”的语义，s
ynchronized 是由“一个变量在同一个时刻只允许一条线程对其进行 lock 操作”这条规则获得的，
此规则决定了持有同一个对象锁的两个同步块只能串行执行。
```
### Volatile原理
```
Java语言提供了一种稍弱的同步机制，即volatile变量，用来确保将变量的更新操作通知到其他线程。
当把变量声明为volatile类型后，编译器与运行时都会注意到这个变量是共享的。

volatile变量不会被缓存在寄存器或者对其他处理器不可见的地方，因此在读取volatile类型的变量时总会返回最新写入的值。
在访问volatile变量时不会执行加锁操作，因此也就不会使执行线程阻塞，因此volatile变量是一种比sychronized关键字更轻量级的同步机制。

当一个变量定义为volatile后，就具备以下的特质：
  1.保证次变量对所有的线程的可见性
    当一个线程修改了这个变量的值，volatile 保证了新值能立即同步到主内存，以及每次使用前立即从主内存刷新。
  
  2.禁止指令重排序优化。
    有volatile修饰的变量，赋值后多执行了一个“load addl $0x0, (%esp)”操作，这个操作相当于一个内存屏障。
    也就是说，指令重排序时不能把后面的指令重排序到内存屏障之前的位置。
```
### Volatile的性能
```
Volatile 的读性能消耗与普通变量几乎相同，但是写操作稍慢，因为它需要在本地代码中插入许多内存屏障指令来保证处理器不发生乱序执行。
```
### 代码实例
```java
public class VolatileTest extends Thread {
    
    boolean flag = false;
    int i = 0;
    
    public void run() {
        while (!flag) {
            i++;
        }
    }
    
    public static void main(String[] args) throws Exception {
        VolatileTest vt = new VolatileTest();
        vt.start();
        Thread.sleep(2000);
        vt.flag = true;
        System.out.println("stope" + vt.i);
    }
}

//上方程序看似没有任何问题，但是？
//当我们执行程序的时候，mian方法。2秒钟以后控制台打印stope-202753974。
//但是程序没有停止下来，为什么？

//咱们修改代码
public class VolatileTest extends Thread {
    
    //这里加上volatile修饰
    volatile boolean flag = false;
    int i = 0;
    
    public void run() {
        while (!flag) {
            i++;
        }
    }
    
    public static void main(String[] args) throws Exception {
        VolatileTest vt = new VolatileTest();
        vt.start();
        Thread.sleep(2000);
        vt.flag = true;
        System.out.println("stope" + vt.i);
    }
}

//在flag前面加上volatile关键字，强制线程每次读取该值的时候都去“主内存”中取值。
//在试试我们的程序吧，已经正常退出了。
```
***
## 如何安全的停止一个线程
### 三种方法停止线程
```
1.使用退出标志，使线程正常退出，也就是当run方法完成后线程终止。
2.使用interrupt方法中断线程。
3.使用stop方法（不推荐）
```
### 使用共享变量来停止线程
```java
public class ThreadFlag extends Thread { 
        
    public volatile boolean exit = false; //共享变量

    public void run() { 
        while (!exit); 
    } 

    public static void main(String[] args) throws Exception { 

        ThreadFlag thread1 = new ThreadFlag(); 
        thread1.start(); 

        sleep(3000);            // 主线程延迟3秒 
        thread1.exit = true;    // 由主线程改变共享变量的值,终止线程thread1 
        thread1.join();         // 主线程等待thread1结束
        System.out.println("thread1线程退出了!"); 
    } 
}

//如上我们使用了共享变量将线程进行了停止
//使用了volatile则是为了线程同步，让同一时间端只能由一个线程修改
```
### 使用interrupt方法终止线程
```java
public class InterruptThread {
    
    public static void main(String[] args) throws InterruptedException {
        MyThread myThread1 = new MyThread();
        System.out.println("启动线程.");
        myThread1.start();
        Thread.sleep(3000);
        System.out.println("中断线程: " + myThread1.getName());
        myThread1.stop = true;  // 设置共享变量为true
        myThread1.interrupt();  // 阻塞时退出阻塞状态
        Thread.sleep(3000);     // 主线程休眠3秒以便观察线程m1的中断情况
        System.out.println("结束.");
    }
}


class MyThread extends Thread {
    public volatile boolean stop = false;   //共享变量

    public void run() {
        while (!stop) {
            System.out.println(getName() + " 正在运行...");
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                System.out.println("抛出异常, 从阻塞中被唤醒.");
                stop = true; // 在异常处理代码中修改共享变量的状态
            }
        }
        System.out.println(getName() + " 线程退出.");
    }
}
```
### 为什么不用stop方法终止线程
```
stop方法太过暴力，会强制终止一个正在运行的线程，这样的话会造成一些数据不一致的问题。
调用stop方法后，会丢弃所有的锁，会导致原子逻辑受损。

该方式是通过立即抛出ThreadDeath异常来达到停止线程的目的，而且此异常抛出可能发生在程序的任何一个地方，
包括catch、finally等语句块中。

由于抛出ThreadDeatch异常，会导致该线程释放所持有的所有的锁，而且这种释放的时间点是不可控制的，
可能会导致出现线程安全问题和数据不一致情况，比如在同步代码块中在执行数据更新操作时线程被突然停止。
```
### 中断线程的三个方法
```java
//中断线程
public void Thread.interrupt()              
//它通知目标线程中断，也是设置中断标志位。中断标志位表示当前线程已经被中断了。

//判断线程是否中断
public boolean Thread.isInterrupted() 
//主要是检查当前线程是否被中断（通过检查中断标志位），返回值是boolean类型

//判断是否被中断，并清除当前中断状态
public static boolean Thread.interrupted()  
//来判断当前线程是否被中断，但同时清除当前线程的中断标志位状态 -- 改为不中断
```
#### interrupt并非会终止线程
```java
public class InterruptedDemo {
  public static void main(String[] args) {
      Thread t1 = new Thread() {

          @Override
          public void run() {
              while(true) {
                  System.out.println("The thread is waiting for interrupted!");
                  //Thread.yield();
              }
          }
      };
      t1.start();
      t1.interrupt();//中断线程
      System.out.println("The Thread is interrupted!");
  }
}

//运行上述的代码，发现线程并不能终止，因为只是对线程进行了中断，并没有进行中断的处理


//改版代码 -- 处理中断逻辑
public class InterruptedDemo {
 
  public static void main(String[] args) {
      Thread t1 = new Thread() {

          @Override
          public void run() {
              while(true) {
                  System.out.println("The thread is waiting for interrupted!");
                  //中断处理逻辑
                  if(Thread.currentThread().isInterrupted()) {//这里中断了话，会返回false，所以实现了线程中断
                      System.out.println("The thread is interrupted!");
                      break;
                  }
                  //Thread.yield();
              }
          }
      };
      t1.start();
      t1.interrupt();//中断线程
      //System.out.println("The Thread is interrupted!");
  }
}

//此段代码，可以正常的中断线程
```

























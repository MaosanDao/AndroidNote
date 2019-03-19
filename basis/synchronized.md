# Java中synchronized的知识点（参考并摘录至[原文](http://www.importnew.com/21866.html)）
## 修饰的对象
```
首先Synchronized是Java中的一个同步锁，修饰的对象有以下几种：
  
  1.修饰一个代码块，被修饰的代码称为同步语句块，其作用的范围是大括号包起来的代码。
    作用的对象是调用这个代码块的对象。
    
  2.修饰一个方法，被修饰的方法被称为同步方法，起作用的范围是整个方法，作用的想是调用这个方法的对象。
  
  3.修饰一个静态的方法，起作用范围是整个静态方法，作用的对象是这个类的所有对象
  
  4.修饰一个类，起作用的范围是synchronized后面括号括起来的部分，作用主的对象是这个类的所有对象。
```
## 具体示例
### 修饰一个代码块
```
一个线程访问一个对象中的synchronized(this)同步代码块时，其他试图访问该对象的线程将被阻塞。
```
#### 同步的线程
```java
/**
 * 同步线程
 */
class SyncThread implements Runnable {
   private static int count;
 
   public SyncThread() {
      count = 0;
   }
 
   public  void run() {
      synchronized(this) {
         for (int i = 0; i < 5; i++) {
            try {
               System.out.println(Thread.currentThread().getName() + ":" + (count++));
               Thread.sleep(100);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
   }
 
   public int getCount() {
      return count;
   }
}
```
```java
//调用SyncThread
SyncThread syncThread = new SyncThread();
Thread thread1 = new Thread(syncThread, "SyncThread1");
Thread thread2 = new Thread(syncThread, "SyncThread2");
thread1.start();
thread2.start();

//结果：
SyncThread1:0
SyncThread1:1
SyncThread1:2
SyncThread1:3
SyncThread1:4
SyncThread2:5
SyncThread2:6
SyncThread2:7
SyncThread2:8
SyncThread2:9
```
#### 结果解析
```
  当两个并发线程(thread1和thread2)访问同一个对象(syncThread)中的synchronized代码块时，
  在同一时刻只能有一个线程得到执行，另一个线程受阻塞，必须等待当前线程执行完这个代码块以后才能执行该代码块。
  
  Thread1和thread2是互斥的，因为在执行synchronized代码块时会锁定当前的对象，
  只有执行完该代码块才能释放该对象锁，下一个线程才能执行并锁定该对象。
```
#### 扩展
```java
//如果我们将创建调用代码改为以下
Thread thread1 = new Thread(new SyncThread(), "SyncThread1");
Thread thread2 = new Thread(new SyncThread(), "SyncThread2");
thread1.start();
thread2.start();

//那么它的结果如下：
SyncThread1:0
SyncThread2:1
SyncThread1:2
SyncThread2:3
SyncThread1:4
SyncThread2:5
SyncThread2:6
SyncThread1:7
SyncThread1:8
SyncThread2:9

//上面的调用代码相当于
SyncThread syncThread1 = new SyncThread();
SyncThread syncThread2 = new SyncThread();
Thread thread1 = new Thread(syncThread1, "SyncThread1");
Thread thread2 = new Thread(syncThread2, "SyncThread2");
thread1.start();
thread2.start();
```
#### 为什么会一起执行呢
```
Synchronized锁定的是对象，这时会有两把锁分别锁定syncThread1对象和syncThread2对象，
而这两把锁是互不干扰的，不形成互斥，所以两个线程可以同时执行。
```
***
### 同步代码块和非同步代码块
```
当一个线程访问对象的一个synchronized(this)同步代码块时，另一个线程仍然可以访问该对象中的非synchronized(this)同步代码块。
```
#### 代码示例
```java
class Counter implements Runnable{
 private int count;

 public Counter() {
    count = 0;
 }

 public void countAdd() {
    synchronized(this) {
       for (int i = 0; i < 5; i ++) {
          try {
             System.out.println(Thread.currentThread().getName() + ":" + (count++));
             Thread.sleep(100);
          } catch (InterruptedException e) {
             e.printStackTrace();
          }
       }
    }
 }

 //非synchronized代码块，未对count进行读写操作，所以可以不用synchronized
 public void printCount() {
    for (int i = 0; i < 5; i ++) {
       try {
          System.out.println(Thread.currentThread().getName() + " count:" + count);
          Thread.sleep(100);
       } catch (InterruptedException e) {
          e.printStackTrace();
       }
    }
 }

 public void run() {
    String threadName = Thread.currentThread().getName();
    if (threadName.equals("A")) {
       countAdd();
    } else if (threadName.equals("B")) {
       printCount();
    }
 }
}

//调用
Counter counter = new Counter();
Thread thread1 = new Thread(counter, "A");
Thread thread2 = new Thread(counter, "B");
thread1.start();
thread2.start();

//结果
A:0
B count:1
A:1
B count:2
A:2
B count:3
A:3
B count:4
A:4
B count:5
```
#### 结果分析
```
从上面的结果中可以看出一个线程访问一个对象的synchronized代码块时，别的线程可以访问该对象的非synchronized代码块而不受阻塞。
```
### 指定给某个对象加锁
```java
/**
 * 银行账户类
 */
class Account {
   String name;
   float amount;
 
   public Account(String name, float amount) {
      this.name = name;
      this.amount = amount;
   }
   //存钱
   public  void deposit(float amt) {
      amount += amt;
      try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
   //取钱
   public  void withdraw(float amt) {
      amount -= amt;
      try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
 
   public float getBalance() {
      return amount;
   }
}
 
/**
 * 账户操作类
 */
class AccountOperator implements Runnable{
   private Account account;
   public AccountOperator(Account account) {
      this.account = account;
   }
 
   public void run() {
      synchronized (account) {
         account.deposit(500);//先存再取
         account.withdraw(500);
         System.out.println(Thread.currentThread().getName() + ":" + account.getBalance());
      }
   }
}

//调用代码
Account account = new Account("zhang san", 10000.0f);
AccountOperator accountOperator = new AccountOperator(account);
 
final int THREAD_NUM = 5;
Thread threads[] = new Thread[THREAD_NUM];
for (int i = 0; i < THREAD_NUM; i ++) {
   threads[i] = new Thread(accountOperator, "Thread" + i);
   threads[i].start();//启动5个线程分别存取
}

//结果
Thread3:10000.0
Thread2:10000.0
Thread1:10000.0
Thread4:10000.0
Thread0:10000.0
```
#### 结果分析
```
从上述代码可以看出，最终，结果没有发生变化，因为：

  我们用synchronized 给account对象加了锁。这时，当一个线程访问account对象时，
  其他试图访问account对象的线程将会阻塞，直到该线程访问account对象结束。
  也就是说谁拿到那个锁谁就可以运行它所控制的那段代码。
```
#### 扩展
```java
//当有一个明确的对象作为锁时，就可以用类似下面这样的方式写程序
public void method3(SomeObject obj)
{
   //obj 锁定的对象
   synchronized(obj)
   {
      // todo
   }
}

//当没有明确的对象作为锁，只是想让一段代码同步时，可以创建一个特殊的对象来充当锁
class Test implements Runnable
{
   //零长度的byte数组对象创建起来将比任何对象都经济
   private byte[] lock = new byte[0];  // 特殊的instance变量
   public void method()
   {
      synchronized(lock) {
         // todo 同步代码块
      }
   }
 
   public void run() {
 
   }
}

```
***
### 修饰一个方法
```
Synchronized修饰一个方法很简单，就是在方法的前面加synchronized，public synchronized void method(){//todo}; 

Synchronized修饰方法和修饰一个代码块类似，只是作用范围不一样，修饰代码块是大括号括起来的范围，而修饰方法范围是整个函数。
```
```java
//写法示例
public synchronized void run() {
   for (int i = 0; i < 5; i ++) {
      try {
         System.out.println(Thread.currentThread().getName() + ":" + (count++));
         Thread.sleep(100);
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}

//写法2
public synchronized void method()
{
   // todo
}

//写法3
public void method()
{
   synchronized(this) {//锁定了整个方法的内容，相当于写法1
      // todo
   }
}
```
#### 注意事项
```
Synchronized关键字不能继承:

  1.如果父类是Synchronized方法，那么子类则默认不是Synchronized方法。
  2.如果父类是Synchronized方法，但是子类调用了父类中的同步代码块，那么子类也相当于是Synchronized方法了。
  3.想要子类是Synchronized方法，那么也可以加上Synchronized关键字。
```
```java
//实现子类为Synchronized方法：

//直接在子类方法上加上Synchronized关键字
class Parent {
   public synchronized void method() { }
}
class Child extends Parent {
   public synchronized void method() { }
}

//调用父类的同步代码块
class Parent {
   public synchronized void method() {   }
}
class Child extends Parent {
   public void method() { super.method(); }
}
```
```
另外：
  1.定义接口方法的时候，不能使用Synchronized关键字。
  2.构造方法不能使用synchronized关键字，但可以使用synchronized代码块来进行同步。
```
***
### 修饰一个静态的方法
```
我们知道静态方法是属于类的而不属于对象的。同样的，synchronized修饰的静态方法锁定的是这个类的所有对象。
```
```java
public synchronized static void method() {
   // todo
}
```
#### 代码示例
```java
/**
 * 同步线程
 */
class SyncThread implements Runnable {
   private static int count;
 
   public SyncThread() {
      count = 0;
   }
 
   public synchronized static void method() {
      for (int i = 0; i < 5; i ++) {
         try {
            System.out.println(Thread.currentThread().getName() + ":" + (count++));
            Thread.sleep(100);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }
 
   public synchronized void run() {
      method();
   }
}

//调用代码
SyncThread syncThread1 = new SyncThread();
SyncThread syncThread2 = new SyncThread();
Thread thread1 = new Thread(syncThread1, "SyncThread1");
Thread thread2 = new Thread(syncThread2, "SyncThread2");
thread1.start();
thread2.start();


//结果
SyncThread1:0
SyncThread1:1
SyncThread1:2
SyncThread1:3
SyncThread1:4
SyncThread2:5
SyncThread2:6
SyncThread2:7
SyncThread2:8
SyncThread2:9
```
#### 结果分析
```
为什么不同的对象，能实现线程同步呢？

  因为，Synchronized修饰的一个静态方法，而静态方法相当于是整个类的。所以，synchronized锁
  的就是整个类。那么他们虽然是2个不同的对像，但是是属于整个类的，故能形成线程同步。
```
***
### 修饰一个类
```java
class ClassName {
   public void method() {
      synchronized(ClassName.class) {
         // todo
      }
   }
}
```
#### 代码示例
```java
/**
 * 同步线程
 */
class SyncThread implements Runnable {
   private static int count;
 
   public SyncThread() {
      count = 0;
   }
 
   public static void method() {
      //修饰一个类
      synchronized(SyncThread.class) {
         for (int i = 0; i < 5; i ++) {
            try {
               System.out.println(Thread.currentThread().getName() + ":" + (count++));
               Thread.sleep(100);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }
         }
      }
   }
 
   public synchronized void run() {
      method();
   }
}

```
#### 结果分析
```
修饰一个类的作用效果和修饰一个静态方法的效果一样，参考上方
```
***
## 总结
```
1.无论synchronized关键字加在方法上还是对象上，如果它作用的对象是非静态的，则它取得的锁是对象；

2.如果synchronized作用的对象是一个静态方法或一个类，则它取得的锁是对类，该类所有的对象同一把锁。

3.每个对象只有一个锁（lock）与之相关联，谁拿到这个锁谁就可以运行它所控制的那段代码。

4.实现同步是要很大的系统开销作为代价的，甚至可能造成死锁，所以尽量避免无谓的同步控制。

```





















































# Hanlder和Message的原理概述
>首先认识三个类：Message(消息)、MessageQueue(消息队列)、Looper(消息循环者)
## Message
>Message是定义一个Message包含必要的描述和属性数据，并且此对象可以被发送到Handler中进行处理。
### arg1,arg2
>用来存放整形数据的
### what
>用来保存消息标志的
### obj
>为Object的任意对象
### replyTo
>消息管理器，会关联到一个handler，handler就是处理其中的消息。
## MessageQueue
>MessageQueue的中文翻译是消息队列，顾名思义，它的内部存储了一组消息，以队列的形式对外提供插入和删除的工作。虽然叫消息队列，但是它的内部存储结构并不是真正的队列，而是采用单链表的数据结构来存储消息。它只是用来存储消息，而并不能处理消息，Looper就填补了这个功能。
## Looper
>在MessageQueue里存储了消息之后，Looper就会以无限循环的形式去查是否有新消息，如果有的话就去处理消息，否则就是一直等待着。
### ThreadLocal
>Looper中还有一个特殊的概念就是ThreadLocal，ThreadLocal并不是线程，它是用来在每个线程中存储数据。Handler创建的时候就会采用ThreadLocal获取当前线程的Looper构造消息循环系统。
### 工作线程默认是没有Looper的，而在UI线程则默认是有的
#### 如何在工作线程中初始化Looper
```java
new Thread("Thread#2"){
  @Override
  public void run(){
      Looper.prepared();
      Handler handler = new Handler();
      Looper.loop();
  };
}.start();
```
## 工作流程
子线程通过Handler创建消息 --> Handler将消息放入MessageQueue队列中 --> 对应线程的Looper查询MessageQueue中是否含有消息 --> :
* 含有消息：UI想成调用Handler处理消息（dispatchMessage --> handleMessage()方法）
* 不含有消息：Looper循环等待
## 整理至
[Handler的基本用法和原理简介](https://blog.csdn.net/bingozhang24/article/details/51986152)

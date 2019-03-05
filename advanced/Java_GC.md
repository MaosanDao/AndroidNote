# Java GC and JVM相关知识点学习（整理并记录至[原文](https://blog.csdn.net/tonytfjing/article/details/44278233)）
***
## 列表
* JVM接口
* 内存分配
* 垃圾回收算法
* 垃圾收集器
***
## JVM结构
### 图示
```
JVM基本结构：
```
![JVM1](https://img-blog.csdn.net/20150315165508885?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
```
下图表示从Java源文件到JVM的整个过程：
```
![JVM2](https://img-blog.csdn.net/20150315165523065?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
### 解析
```
1.类加载器（ClassLoader):
  在JVM启动时或者在类运行时将需要的class加载到JVM中。
  
2.执行引擎
  负责执行class文件中包含的字节码指令。
  
3.内存区（也叫运行时数据区）
  是在JVM运行的时候操作所分配的内存区。运行时内存区主要可以划分为5个区域。
```
```
图示为执行时数据区域：
```
![RunTime Data Area](https://img-blog.csdn.net/20150315165615201?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
```
其中上图中的一些名词解释：

1.方法区(Method Area)
  用于存储类结构信息的地方，包括常量池、静态变量、构造函数等。
  
2.java堆(Heap)
  存储java实例或者对象的地方。这块是GC的主要区域。
  
3.java栈(Stack)
  java栈总是和线程关联在一起，每当创建一个线程时，JVM就会为这个线程创建一个对应的java栈。
  在这个java栈中又会包含多个栈帧，每运行一个方法就创建一个栈帧，用于存储局部变量表、操作栈、方法返回值等。
  每一个方法从调用直至执行完成的过程，就对应一个栈帧在java栈中入栈到出栈的过程。所以java栈是现成私有的。
  
4.程序计数器(PC Register)
  用于保存当前线程执行的内存地址。由于JVM程序是多线程执行的（线程轮流切换），所以为了保证线程切换回来后，
  还能恢复到原先状态，就需要一个独立的计数器，记录之前中断的地方，可见程序计数器也是线程私有的。
  
5.本地方法栈(Native Method Stack)
  和java栈的作用差不多，只不过是为JVM使用到的native方法服务的。
  
注意：
  在运行时数据区中，部分内容是线程私有的：
    1.程序计数器
    2.JVM栈
    3.本地方法栈
```
***
## 内存分配
```
我觉得了解垃圾回收之前，得先了解JVM是怎么分配内存的，然后识别哪些内存是垃圾需要回收，最后才是用什么方式回收:

java的内存申请分为2种：
  1.静态内存
    很容易理解，编译时就能够确定的内存就是静态内存，即内存是固定的，系统一次性分配，比如int类型变量。定长的。
    
  2.动态内存
    动态内存分配就是在程序执行时才知道要分配的存储空间大小，比如java对象的内存空间。
    
总之：
  总之Stack的内存管理是顺序分配的，而且定长，不存在内存回收问题； --- 栈
  而Heap则是为java对象的实例随机分配内存，不定长度，所以存在内存分配和回收的问题； --- 堆
```
***
## 垃圾检测、回收算法（Java堆中的内存如何分配出去，然后再回收回来？）
### 如何检测垃圾？
```
一般JVM会使用这2种方法：

  1.引用计数法
    给一个对象添加引用计数器，每当有个地方引用它，计数器就加1；引用失效就减1。
   
    问题：
      如果我有两个对象A和B，互相引用，除此之外，没有其他任何对象引用它们，实际上这两个对象已经无法访问，
      即是我们说的垃圾对象。但是互相引用，计数不为  0，导致无法回收，所以还有另一种方法：
      
  2.可达性分析
    以根集对象为起始点进行搜索，如果有对象不可达的话，即是垃圾对象。
    这里的根集一般包括java栈中引用的对象、方法区常良池中引用的对象。
```
### 回收算法
#### 标记-清除（Mark-sweep)算法
```
算法和名字一样，分为两个阶段：标记和清除。标记所有需要回收的对象，然后统一回收。
这是最基础的算法，后续的收集算法都是基于这个算法扩展的。

不足：
  效率低；标记清除之后会产生大量碎片。效果图如下：
```
![ms](https://img-blog.csdn.net/20150315165800119?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
#### 复制（Copying）算法
```
此算法把内存空间划为两个相等的区域，每次只使用其中一个区域。垃圾回收时，遍历当前使用区域，把正在使用中的对象复制到另外一个区域中
此算法每次只处理正在使用中的对象，因此复制成本比较小，同时复制过去以后还能进行相应的内存整理，不会出现“碎片”问题。

不足
  需要双倍的内存空间。
```
![copy](https://img-blog.csdn.net/20150315165927137?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
#### 标记-整理（Mark-Compact）
```
过程
```
此算法每次只处理正在使用中的对象，因此复制成本比较小，同时复制过去以后还能进行相应的内存整理，不会出现“碎片”问题。




























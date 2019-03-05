# Java GC and JVM相关知识点学习（整理并记录至[原文](https://blog.csdn.net/tonytfjing/article/details/44278233)）
***
## 列表
* [JVM结构](#JVM结构)
* [内存分配](#内存分配)
* [垃圾回收算法](#回收算法)
* [具体的GC图示过程(分代收集算法)](#具体的gc图示过程)
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
![核心区](https://img-blog.csdn.net/20180617161343935?watermark/2/text/aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L2Fpaml1ZHU=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70)
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
过程分为两个阶段：
  1.从根节点开始标记所有被引用对象
  2.遍历整个堆，把清除未标记对象并且把存活对象“压缩”到堆的其中一块，按顺序排放
```
![MC](https://img-blog.csdn.net/20150315170004250?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
#### 分代收集算法（主要！）
```
不同的对象的生命周期是不一样的。因此，不同生命周期的对象可以采取不同的收集方式，以便提高回收效率。
```
##### 为什么要运用分代垃圾回收策略？
```
在java程序运行的过程中，会产生大量的对象，因每个对象所能承担的职责不同所具有的功能不同所以也有着不一样的生命周期，

有的对象生命周期较长，比如Http请求中的Session对象，线程，Socket连接等；
有的对象生命周期较短，比如String对象，由于其不变类的特性，有的在使用一次后即可回收。

并且：
  在不进行对象存活时间区分的情况下，每次垃圾回收都是对整个堆空间进行回收，
  那么消耗的时间相对会很长，而且对于存活时间较长的对象进行的扫描工作等都是徒劳。
```
##### 如何进行划分？
```
咱们根据不同的生命周期划分为3个大类：

  1.年轻代(Young Generation)
  2.年老待(Old Generation)
  3.持久代(Permanent Generation)
  
通俗解释：
  // 年轻代
  假设你是一个普通的 Java 对象，你出生在 Eden 区，在 Eden 区有许多和你差不多的小兄弟、小姐妹，
  可以把 Eden 区当成幼儿园，在这个幼儿园里大家玩了很长时间。Eden 区不能无休止地放你们在里面。
  
  //这是minor GC的作用，“来回折腾”
  所以当年纪稍大，你就要被送到学校去上学，这里假设从小学到高中都称为 Survivor 区。
  开始的时候你在 Survivor 区里面划分出来的的“From”区，读到高年级了，就进了 Survivor 区的“To”区，
  中间由于学习成绩不稳定，还经常来回折腾。直到你 18 岁的时候，高中毕业了，该去社会上闯闯了。
  
  // 年老代
  于是你就去了年老代，年老代里面人也很多。在年老代里，你生活了 20 年 (每次 GC 加一岁)，最后寿终正寝，被 GC 回收。
  有一点没有提，你在年老代遇到了一个同学，他的名字叫爱德华 (慕光之城里的帅哥吸血鬼)， 
  
  //持久代
  他以及他的家族永远不会死，那么他们就生活在永生代。
```
##### 年轻代
```
年轻代:是所有新对象产生的地方。

年轻代被分为3个部分：
  1.Enden区
  2.两个Survivor区（From和To）
  
具体过程：
  当Eden区被对象填满时，就会执行Minor GC。并把所有存活下来的对象转移到其中一个survivor区（假设为from区）。
  Minor GC同样会检查存活下来的对象，并把它们转移到另一个survivor区（假设为to区）。
  
  这样在一段时间内，总会有一个空的survivor区。
  经过多次GC周期后，仍然存活下来的对象会被转移到年老代内存空间。（通常这是在年轻代有资格提升到年老代前通过设定年龄阈值来完成的）
  
  需要注意，Survivor的两个区是对称的，没先后关系，from和to是相对的。--- “来回倒腾”
```
##### 年老代
```
年老代：在年轻代中经历了N次回收后仍然没有被清除的对象，就会被放到年老代中。

对于年老代和永久代，就不能再采用像年轻代中那样搬移腾挪的回收算法，因为那些对于这些回收战场上的老兵来说是小儿科。
通常会在老年代内存被占满时将会触发Full GC,回收整个堆内存。
```
##### 持久代
```
持久代：用于存放静态文件，比如java类、方法等。持久代对垃圾回收没有显著的影响。 
```
### 具体的GC图示过程
![GC](https://img-blog.csdn.net/20150315170424355?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvdG9ueXRmamluZw==/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)
```
具体分析：
  MinorGC:
    负责将Eden区的内存进行回收，然后在S0和S1之间“来回倒腾”。如果在年龄阈值后，它们之间还存在活动的对象，那么就会将该
    对象放置到年老区中。
  
  MajorGC:
    当年老区的内存被占满时，会将整个堆内存进行GC。
    
  年轻代：
    包括，Eden区，S0和S1（Survivor区，From和To，相当于每个年龄周期时候MinorGC后的暂存区）。
```
#### 上述算法和分代收集的关系
```
年轻代采用了复制算法。

年老代采用了标记-整理（Mark-Compact）算法。
```
## 推荐阅读
```
下面链接是一个比较完整的GC分析流程图，可以搭配着一起看
```
* [Java gc(垃圾回收机制)小结，以及Android优化建议](https://www.jianshu.com/p/214e42fc0d37)
## 说明
```
以上部分均是采集[原文](https://blog.csdn.net/tonytfjing/article/details/44278233)所得。
```

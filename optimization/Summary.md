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
集合添加
```
### 集合类泄漏














































































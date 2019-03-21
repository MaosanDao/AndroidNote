# Android App开发人员需要知道的一些Android底层知识
***
## Binder
### 什么是Binder
```
Binder是为了解决跨进程通信。

首先，Binder分为Client和Server两个进程。

注意，Client和Server是相对的。谁发消息，谁就是Client，谁接收消息，谁就是Server。

举个例子，两个进程A和B之间使用Binder通信，进程A发消息给进程B，那么这时候A是Binder Client，B是Binder Server；
进程B发消息给进程A，那么这时候B是Binder Client，A是Binder Server——其实这么说虽然简单了，但还是不太严谨，我们先这么理解着。
```
### Binder的组成结构
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170516223325025-1448613892.png)
```
图中的ServiceManager，负责把Binder Server注册到一个容器中。
```
### Binder的通信过程
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170516223354650-984999229.png)
```
由上图可见：

Client想要直接调用Server的add方法，是不可以的，因为它们在不同的进程中，这时候就需要Binder来帮忙了。

具体过程为：

1.首先是Server在SM这个容器中注册。
2.Client想要调用Server的add方法，就需要先获取Server对象， 但是SM不会把真正的Server对象返回给Client，
  而是把Server的一个代理对象返回给Client，也就是Proxy。
3.Client调用Proxy的add方法，SM会帮他去调用Server的add方法，并把结果返回给Client。
```
***
## AIDL
```
Android系统中很多系统服务都是aidl，比如说剪切板。
```
### AIDL
```
AIDL中的几个类：

IBinder
IInterface
Binder
Proxy
Stub
```
### 他们如何进行IPC的通信
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170516223504650-228437964.png)
```
由上图分析：

1.先从Client看起，对于AIDL的使用者，我们这么写程序：
  MyAidl.Stub.asInterface(某IBinder对象).sum(1, 2);
  
  asInterface方法的作用是判断参数——也就是IBinder对象，和自己是否在同一个进程：
    是，则直接转换、直接使用，接下来就跟Binder跨进程通信无关啦。
    否，则把这个IBinder参数包装成一个Proxy对象，这时调用Stub的sum方法，间接调用Proxy的sum方法。
      return new MyAidl.Stub.Proxy(obj);
      
2.Proxy在自己的sum方法中，会使用Parcelable来准备数据，把函数名称、函数参数都写入_data，让_reply接收函数返回值。
  最后使用IBinder的transact方法，把数据就传给Binder的Server端了。
  
  mRemote.transact(Stub.TRANSACTION_addBook, _data, _reply, 0); //这里的mRemote就是asInterface方法传过来的obj参数

3.Server则是通过onTransact方法接收Client进程传过来的数据，包括函数名称、函数参数，找到对应的函数，这里是sum，把参数喂进去，得到结果，返回。
   所以onTransact函数经历了读数据-->执行要调用的函数-->把执行结果再写数据的过程。
```


































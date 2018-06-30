# Android进程间、线程间通信的方式总结
## 进程间通信
### Bundle/Intent传递数据
>可传递基本类型，String，实现了Serializable或Parcellable接口的数据结构。Serializable是Java的序列化方法，Parcellable是Android的序列化方法，前者代码量少（仅一句），但I/O开销较大，一般用于输出到磁盘或网卡；后者实现代码多，效率高，一般用于内存间序列化和反序列化传输。
### File
>对同一个文件先后写读，从而实现传输，Linux机制下，可以对文件并发写，所以要注意同步。顺便一提，Windows下不支持并发读或写。
### Messenger
>Messenger是基于AIDL实现的，服务端（被动方）提供一个Service来处理客户端（主动方）连接，维护一个Handler来创建Messenger，在onBind时返回Messenger的binder。双方用Messenger来发送数据，用Handler来处理数据。Messenger处理数据依靠Handler，所以是串行的，也就是说，Handler接到多个message时，就要排队依次处理。
### AIDL
>AIDL通过定义服务端暴露的接口，以提供给客户端来调用，AIDL使服务器可以并行处理，而Messenger封装了AIDL之后只能串行运行，所以Messenger一般用作消息传递。
通过编写aidl文件来设计想要暴露的接口，编译后会自动生成响应的java文件，服务器将接口的具体实现写在Stub中，用iBinder对象传递给客户端，客户端bindService的时候，用asInterface的形式将iBinder还原成接口，再调用其中的方法。
### ContentProvider
>系统四大组件之一，底层也是Binder实现，主要用来为其他APP提供数据，可以说天生就是为进程通信而生的。自己实现一个ContentProvider需要实现6个方法，其中onCreate是主线程中回调的，其他方法是运行在Binder之中的。自定义的ContentProvider注册时要提供authorities属性，应用需要访问的时候将属性包装成Uri.parse(“content://authorities”)。还可以设置permission，readPermission，writePermission来设置权限。 ContentProvider有query，delete，insert等方法，看起来貌似是一个数据库管理类，但其实可以用文件，内存数据等等一切来充当数据源，query返回的是一个Cursor，可以自定义继承AbstractCursor的类来实现。
### Socket
>学过计算机网络的对Socket不陌生，所以不需要详细讲述。只需要注意，Android不允许在主线程中请求网络，而且请求网络必须要注意声明相应的permission。然后，在服务器中定义ServerSocket来监听端口，客户端使用Socket来请求端口，连通后就可以进行通信。
### 广播(BroadcastReceiver)
>广播是一种被动跨进程通讯的方式。当某个程序向系统发送广播时，其他的应用程序只能被动地接收广播数据。这就象电台进行广播一样，听众只能被动地收听，而不能主动与电台进行沟通。在应用程序中发送广播比较简单。只需要调用sendBroadcast方法即可。该方法需要一个Intent对象。通过Intent对象可以发送需要广播的数据。
## 线程间通信
* runOnUiThread
* Message/Handler
* AsyncTask

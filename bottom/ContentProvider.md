# Android ContentProvider的底层通信和相关知识点（[原文](https://www.cnblogs.com/Jax/p/6910699.html)）
## App中如何使用CP
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222000935-1998749550.png)
### 定义CP的App1
```
在App1中定义一个CP的子类MyContentProvider，并在Manifest中声明，为此要在MyContentProvider中实现CP的增删改查四个方法：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222020138-495218737.png)
```
继承CP，然后实现增删查改方法：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222029575-1530590653.png)
### 使用CP的App2
```
在App2访问App1中定义的CP，为此，要使用到ContentResolver，它也提供了增删改查4个方法，用于访问App1中定义的CP：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222040325-1806998309.png)
### 小总结
```
其实CP的增删查改四个方法的底层实现，都是和AMS进行的通信，最终使用了App1里面CP的四个方法。

其次，URI是CP的身份证，是它的唯一标识。
从上面可以看出，我们在App1里面声明CP的时候，有一个authorities值，且为“BaoBao”，
那么在App2中使用的是，就需要指定它了。即，ContentResolver的URI：
  uri = Uri.parse("content://baobao/");
```
## CP的本质
```
CP的本质就是把数据存储在了SQLite数据库中。

各种数据源，有各种格式，比如短信、通信录，它们在SQLite中就是不同的数据表，但是对外界的使用者而言，就需要封装成统一的访问方式。
比如说对于数据集合而言，必须要提供增删改查四个方法，于是我们在SQLite之上封装了一层，也就是CP。
```
## 匿名共享内存（ASM）
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222101591-2145340358.png)
```
交互流程：
  
  1.Client内部有一个CursorWindow对象，发送请求的时候，把这个CursorWindow类型的对象传过去，这个对象暂时为空。
  2.Server收到请求，搜集数据，填充到这个CursorWindow对象。
  3.Client读取内部的这个CursorWindow对象，获取到数据。
  
由此可见，这里的CursorWindow就是匿名共享内存。

举个生活中的例子就是，你定牛奶，在你家门口放个箱子，送牛奶的人每天早上往这个箱子放一袋牛奶，
你睡醒了去箱子里取牛奶。这个牛奶箱就是匿名共享内存。
```
## CP和AMS的通信流程
```
App2想访问App1中定义的CP为例子。我们就看CP的insert方法:
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222112216-35003614.png)
```
上面这5行代码，包括了启动CP和执行CP方法两部分，分水岭在insert方法，insert方法的实现，
前半部分仍然是在启动CP，当CP启动后获取到CP的代理对象，后半部分是通过代理对象，调用insert方法。

它的具体交互流程如下：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170526222122529-365974622.png)
```
流程：
  
  1.App2发送消息给AMS，想要访问App1中的CP。
  2.AMS检查发现，App1中的CP没启动过，为此新开一个进程，启动App1，然后获取到App1启动的CP，把CP的代理对象返回给App2。
  3.App2拿到CP的代理对象，也就是IContentProvider，就调用它的增删改查4个方法了，接下来就是使用ASM来传输数据或者修改数据了。
    也就是上面提到的CursorWindow这个类，取得数据或者操作结果即可
```













































































# Android Context家族史
```
Activity、Service、Application都有Context。他们是亲戚。
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230746869-193964502.png)
```
图示解析：
  
  1.Activity因为有一层Theme，所以它有一层ContextThemeWrapper。
  2.ContextWrapper只是一个包装类，没有任务具体的实现，真正的逻辑在ContextImpl中。

总结：

  1.一个应用中，一共有多少个Context？
    Activity + Service + 1（Application本身也是一个Context）
  2.App中包含了多个ContextImpl对象，但是内部变量mPackageInfo都指向一个PackInfo独享。

```
## 区别
```
跳转一个新的Activity的写法有这么两种：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230800588-1214836011.png)

![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230811869-1855137329.png)
```
从上图可以发现，这两种方法都可以进行跳转，那么他们有什么异同呢？

  1.getApplicationContext方法拿取到的context，是从ActivityThread中取出的Instrumentation，
    然后执行execStartActivity方法，进而进行跳转的。
  2.和Activity的跳转是一样的逻辑。
  
如下图：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230823603-908308741.png)
## 思考
```
我们在之前分析App启动流程的时候，说过这个Application的生命周期是和整个App一起走的，
那么，通过getApplicationContext得到的Context，就是从ActivityThread中取出来的Application对象。
所以，这个Context要格外注意，不能进行泄漏，因为它很容易发生泄漏。

startActivity内部方法：
```
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230840885-574938888.png)






























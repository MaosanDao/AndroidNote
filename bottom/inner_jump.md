# Android内部的页面跳转
```
在介绍完App的启动流程后，我们发现，其实就是启动一个App的首页。
接下来我们看App内部页面的跳转。

从ActivityA跳转到ActivityB，其实可以把ActivityA看作是Launcher，那么这个跳转过程：

1.ActivityA向AMS发送一个启动ActivityB的消息
2.AMS保存ActivityB的信息，然后通知App，你可以休眠了（onPaused）
3.ActivityA进入休眠，然后通知AMS，我休眠了
4.AMS发现ActivityB所在的进程就是ActivityA所在的进程，所以不需要重新启动新的进程，所以它就会通知App，启动ActivityB
5.App启动ActivityB
```
## 图示
![](https://images2015.cnblogs.com/blog/13430/201705/13430-20170519230735275-563343566.png)

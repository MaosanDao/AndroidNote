# MVC、MVP、MVVM介绍以及相关知识点
****
## MVC
通用解释
```
MVC，(Model View Controller)，是软件架构中最常见的一种框架，简单来说就是通过controller的控制去操作model层的数据，
并且返回给view层展示。
```
在Android中的解释：
```
针对于原生的App则可以这样去理解MVC：

Model：各种java bean，以及一些数据库的类型，存储之类的。
View：xml中的一些布局代码，则对应view层。
Controller：activity中的一些逻辑代码。
```
存在的一些问题：
```
1.在原生App中，xml作为view层，控制能力实在太弱了。试想，如果想要将一个按钮进行隐藏，那么就需要在activity中使用
  逻辑代码来进行控制。那么，就会造成activity既是controller又是view的局面，进而造成c层拥挤和堵塞。
  
2.view层和model层是相互可知的，这就说明了它们之间有着一种耦合。这是程序非常致命的弱点。
```
****
## 进阶MVP
通用解释：
```
用于操作view层发出的事件传递到presenter层中，presenter层去操作model层，并且将数据返回给view层，
整个过程中view层和model层完全没有联系。
```
那View层和Presenter层又是耦合了？
```
其实不是的，对于view层和presenter层的通信，我们是可以通过接口实现的;具体的意思就是说我们的activity，
fragment可以去实现实现定义好的接口，而在对应的presenter中通过接口调用方法。

不仅如此，我们还可以编写测试用的View，模拟用户的各种操作，从而实现对Presenter的测试。
这就解决了MVC模式中测试，维护难的问题。
```
## 完全

# 点击事件分发机制总结
## Activity为最底部的根视图
## 实例：
下图是一个我们模拟的情况，其中显示的颜色为层级，GroupA为最底层（Activity才是最底层，我们这里说的是控件，所以说是A为最底层），依次是GroupB,myView。

### 1.现在我们点击myView这个控件，来解析事件分发的过程：

![示例6](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_11.png)

### 2.首先是默认不处理的情况下，来点击myView控件，则整个分发流程为下图：

![示例6](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_7.png)

### 3.然后我们在**GroupB**中去拦截事件的分发，那么，在最上层的myView则不会收到这个事件的消息，即事件分发是*不会经过myView*这一层的。相反，谁拦截了这个事件，那么谁就会进行处理。在这里就是GroupB来进行处理了。如图所示，GroupB会触发*onTouchEvent*方法。最后，再向下层传递这个消费事件，直到Activity这一层就结束了整个事件。

![示例6](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_8.png)

### 4.123123
![示例6](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_9.png)
![示例6](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_10.png)
![示例6](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_6.png)
## ViewGroup 
* Android中touch事件的传递，绝对是先传递到ViewGroup，再传递到View的
* 当你点击了某个控件，首先会去调用该控件所在布局的dispatchTouchEvent方法，然后在布局的dispatchTouchEvent方法中找到被点击的相应控件，再去调用该控件的dispatchTouchEvent方法。

如下图所示：
![事件分发](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_1.jpg)

### 总结
* Android事件分发是先传递到ViewGroup，再由ViewGroup传递到View的
* 在ViewGroup中可以通过onInterceptTouchEvent方法对事件传递进行拦截，onInterceptTouchEvent方法返回true代表不允许事件继续向子View传递，返回false代表不对事件进行拦截，默认返回false
* 子View中如果将传递的事件消费掉，ViewGroup中将无法再对事件不再监听

## 例子1
### 假设最高层View叫OuterLayout，中间层View叫InnerLayout，最底层View叫MyVIew。调用顺序是这样的：
```java
OuterLayout.onInterceptTouchEvent -> InnerLayout.onInterceptTouchEvent -> MyView.onTouchEvent -> InnerLayout.onTouchEvent
 -> OuterLayout.onTouchEvent
```
## 例子2
### 当父控件中有子控件的时候，并且父控件和子空间都有事件处理（比如单击事件）。这时，点击子控件，父控件的单击事件就无效了
### 假如：比如一个LinearLayout里面有一个子控件TextView，但是TextView的大小没有LinearLayout大：
#### 如果LinearLayout和TextView都设置了单击事件：
* 点击TextView区域的时候，触发的是TextView的事件
* 点击TextView以外的区域的时候，还是触发的LinearLayout的事件
#### 如果LinearLayout设置了单击事件，而TextView没有设置单击事件的话：
* 不管单击的是TextView区域，还是TextView以外的区域，都是触发的LinearLayout的单击事件
### 假如：如果LinearLayout的大小和TextView一样的话
#### 如果LinearLayout和TextView都设置了单击事件：
* 只有TextView的单击事件有效
#### 如果LinearLayout设置了单击事件，而TextView没有设置单击事件的话：
* 触发的是LinearLayout的单击事件
 ## 图示
 ### 事件分发和传递
 ![示例3](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_3.jpg)


 

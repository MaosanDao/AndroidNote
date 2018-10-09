# 点击事件分发机制总结
## ViewGroup 
* Android中touch事件的传递，绝对是先传递到ViewGroup，再传递到View的
* 当你点击了某个控件，首先会去调用该控件所在布局的dispatchTouchEvent方法，然后在布局的dispatchTouchEvent方法中找到被点击的相应控件，再去调用该控件的dispatchTouchEvent方法。

如下图所示：
![事件分发](https://github.com/MaosanDao/AndroidQuickCheckList/blob/master/touch_event_1.jpg)

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
## 方法解析
* dispatchTouchEvent:分发事件（发苹果）— 如果下一级不想要这个苹果，则给上一级自己吃
* onInterceptTouchEvent:是否阻断事件（是否给下一级发苹果）— 如果阻断了，则给自己吃
* onTouchEvent:是否消费事件（是否吃了该苹果）
 ## 图示
 ### 事件分发和传递
 ![示例2](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_2.jpg)
 ![示例3](https://github.com/MaosanDao/AndroidNote/blob/master/advanced/touch_event_3.jpg)


 

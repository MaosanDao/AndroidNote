# 转场动画和元素共享转换
>一般分为两种:
* 分解、滑动进入、淡入淡出
* 共享元素动画
## 分解、滑动进入、淡入淡出
>激活窗口转换动画：
```xml
<item name="android:windowContentTransitions">true</item>
```
### 分解
1.跳转按钮需要这样做：
```java
startActivity(new Intent(this, ExplodeActivity.class)
, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
```
2.在所跳转的Activity中增加以下代码：
```java
getWindow().setEnterTransition(new Explode().setDuration(2000));  
getWindow().setExitTransition(new Explode().setDuration(2000)); 
```
### 滑动
1.跳转按钮需要这样做：
```java
startActivity(new Intent(this, ExplodeActivity.class)
, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
```
2.在所跳转的Activity中增加以下代码:
```java
getWindow().setEnterTransition(new Slide().setDuration(2000));  
getWindow().setExitTransition(new Slide().setDuration(2000)); 
```
### 淡入淡出
1.跳转按钮需要这样做：
```java
startActivity(new Intent(this, ExplodeActivity.class)
, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
```
2.在所跳转的Activity中增加以下代码:
```java
getWindow().setEnterTransition(new Fade().setDuration(2000));  
getWindow().setExitTransition(new Fade().setDuration(2000));
```
## 共享元素转换
> 在第一个Activity和第二个Activity里边都有一个Button，只不过一个大一个小，从第一个Activity跳转到第二个Activity时，我并没有感觉到Activity的跳转，只是觉得好像第一个页面的Button放大了，同理，当我从第二个页面回到第一个页面时，也好像Button变小了。OK，这就是我们的Activity共享元素。

1.首先在第一个Activity和目标Activity中的布局中，给需要共享的的两个元素同时增加以下属性：
```java
android:transitionName=”myButton1”//属性值需要一致，否则系统会找不到
```
2.启动端的跳转代码：
单个：
```java
startActivity(new Intent(this, SharedElementsActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation
                                (this, view, "myButton1")
                                .toBundle());
```
多个：
```java
 startActivity(new Intent(this, SharedElementsActivity.class),
                        ActivityOptions.makeSceneTransitionAnimation
                                (this,  //通过Pair.create方法来设置多个共享元素
                                        Pair.create(view, "myButton2"),//这里的myButton2只的是SharedElementsActivity中的共享元素
                                        Pair.create(view, "myButton3"))
                                .toBundle());
```
3.目标Activity代码：
跳转到原来的Activity,用共享元素的方式跳转回去
```java
startActivity(new Intent(this, MainActivity.class),
                ActivityOptions.makeSceneTransitionAnimation
                        (this, button, "myButton1")//这里的myButton1只的是MainActivity中的共享元素
                        .toBundle());
```
### 注意
#### 如果在共享元素进入和退出的时候，界面会发生一次闪烁，则可以通过在双方的Acitity中增加以下代码来解决：
```java
getWindow().setEnterTransition(null);
getWindow().setExitTransition(null);
```
#### 如果在跳转的的时候，finish掉跳转的activity，则会出现闪出的现象，请注意。
#### 如果出现页面之间跳转闪黑的情况，则可以使用以下方法进行屏蔽：
```xml
  <!-- Activity主题 -->
    <style name="ActivityTheme" parent="AppTheme">
        <item name="android:windowIsTranslucent">true</item>
    </style>

//然后给每个Activity加上该主题就行了
```



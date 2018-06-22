# 转场动画和元素共享转换等
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
1.首先在第一个Activity和目标Activity中的布局中，给需要共享的的两个元素同时增加以下
## 共享元素转换
## 共享元素转换

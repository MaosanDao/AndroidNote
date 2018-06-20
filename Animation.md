# Android Animation知识汇总
>在Android的体系中，共分为几种动画：
>* View Animation（视图动画）
>* Drawble Animation（帧动画）
>* Property Animation（属性动画）
## View Animation（未改变真实的坐标位置）
>只支持简单的缩放、平移、旋转、透明度基本的动画
### Alpha --> 渐变
```java
<?xml version="1.0" encoding="utf-8"?>
    <set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/accelerate_interpolator">
    <!-- fromAlpha和toAlpha是起始透明度和结束时透明度 -->
    <alpha
        android:fromAlpha="1.0"
        android:toAlpha="0.0"
        android:startOffset="500"
        android:duration="500"/>
    </set>
```
### rotate --> 旋转
```java
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/accelerate_interpolator">
    <!--
        fromDegrees:开始的角度
        toDegrees：结束的角度，+表示是正的
        pivotX：用于设置旋转时的x轴坐标
        例
           1)当值为"50"，表示使用绝对位置定位
           2)当值为"50%"，表示使用相对于控件本身定位
           3)当值为"50%p"，表示使用相对于控件的父控件定位
        pivotY：用于设置旋转时的y轴坐标
      -->
    <rotate
        android:fromDegrees="0"
        android:toDegrees="+360"
        android:pivotX="50%"
        android:pivotY="50%"
        android:duration="1000"/>
</set>
```
### scale --> 缩放
```java
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/accelerate_interpolator">
   <!--
       起始x轴坐标
           止x轴坐标
           始y轴坐标
           止y轴坐标
           轴的坐标
           轴的坐标
     -->
   <scale
       android:fromXScale="1.0"
       android:toXScale="0.0"
       android:fromYScale="1.0"
       android:toYScale="0.0"
       android:pivotX="50%"
       android:pivotY="50%"
       android:duration="1000"/>
</set>
```
### translate --> 位移
```java
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    android:interpolator="@android:anim/accelerate_interpolator">
    <!--
           始x轴坐标
           止x轴坐标
           始y轴坐标
           止y轴坐标
      -->
    <translate
        android:fromXDelta="0%"
        android:toXDelta="100%"
        android:fromYDelta="0%"
        android:toYDelta="100%"
        android:duration="2000"/>
</set>
```
### 调用并启动
```java
Animation animation = AnimationUtils.loadAnimation(Animation1Activity.this, R.anim.alpha);
// 启动动画
image.startAnimation(animation);
```
### 直接在代码中设置
```java
// 这里都指定了Animation.RELATIVE_TO_SELF这个参数，相对于自己，如果不指定，默认是相对父控件。
TranslateAnimation translateAnimation =
              new TranslateAnimation(
                  Animation.RELATIVE_TO_SELF,0f,
                  Animation.RELATIVE_TO_SELF,100f,
                  Animation.RELATIVE_TO_SELF,0f,
                  Animation.RELATIVE_TO_SELF,100f);
           translateAnimation.setDuration(1000);
           view.startAnimation(translateAnimation);
```
## Drawble Animation（帧动画）
>Drawable Animation 可以让我们按顺序加载一系列的资源来创建一个动画。动画的创建和传统意义上电影胶卷的播放一样，是通过加载不同的图片，然后按顺序进行播放来实现的。
### 建立一个animation list
```java
<?xml version="1.0" encoding="utf-8"?>
<animation-list xmlns:android="http://schemas.android.com/apk/res/android"
   android:oneshot="true">

   <item android:drawable="@drawable/scan1" android:duration="100" />
   <item android:drawable="@drawable/scan2" android:duration="100" />
   <item android:drawable="@drawable/scan3" android:duration="100" />
   <item android:drawable="@drawable/scan4" android:duration="100" />

</animation-list>
```
>android:oneshot='false'//true则为循环一次后会停留在最后一帧，false则相反
### 使用
```java
view.setBackgroundResource(R.drawable.scan);
animationDrawable= (AnimationDrawable) view.getBackground();
animationDrawable.start();

animationDrawable.stop();
```
## Property Animation（属性动画,改变了真实的坐标位置）
>* 沿着一定的时间顺序，通过改变View的属性，从而得到的动画的效果
>* 对于ViewAnimation，动画的移动和缩放并没有真正的改变控件的位置和热区，而属性动画则可以做到这一点。

### 相关重要方法和监听
>[点击查看](http://www.android-doc.com/reference/android/animation/package-summary.html)
#
`
```

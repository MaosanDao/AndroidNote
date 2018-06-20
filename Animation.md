# Android Animation知识汇总
>在Android的体系中，共分为几种动画：
>* View Animation（视图动画）
>* Drawble Animation（帧动画）
>* Property Animation（属性动画）
## View Animation
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


# Android Animation知识汇总
>在Android的体系中，共分为几种动画：
>* View Animation（视图动画）
>>主要使用set xml来定义平移、缩放、旋转、透明度等变换。然后使用AnimationUtils的loadAnimation调用xml。
>* Drawble Animation（帧动画）
>>主要使用xml的animation-set标签，然后在xml中定义不同状态的图片的item。
>* Property Animation（属性动画）
>>主要使用ValueAnimator和ObjectAnimator这两个类来使用，当然也可以使用xml的objectanimator标签来做。
>* 集合动画（AnimationSet和AnimatorSet）
>>是通过来调用一系列动画来完成的动画组。他们的区别和上述的一样，Animator是真实改变了坐标点，而Animation则没有改变
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
```xml
<?xml version="1.0" encoding="utf-8"?>
<set xmlns:android="http://schemas.android.com/apk/res/android"
    a ndroid:interpolator="@android:anim/accelerate_interpolator">
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
```xml
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
```xml
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
```xml
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
### 重要的几个类
* ValueAnimator 属性的核心类
* ObjectAnimator 继承 ValueAnimator 对ValueAnimator进行了一层封装 
* AnimatorSet 可以同时运行一组动画
* PropertyValuesHolder 他代表一个在动画运行中需要过度到的值
* TypeEvaluator 实现此接口的实例，将决定AnimatorUpdateListener接收到的值
## ObjectAnimator
```java
private void runAnimator(View view){
        // 旋转
        String rotationX = "rotationX";
        String rotationY = "rotationY";
        // 渐变
        String alpha = "alpha";
        // 缩放
        String scale = "scale";
        String scaleX = "scaleX";
        String scaleY = "scaleY";
        // 移动
        String translationX= "translationX";
        String translationY= "translationY";
        // 第二个参数传入相应的动画名称就OK了
        ObjectAnimator.ofFloat(view , rotationX , 0.0f , 360f)
                .setDuration(2000)
                .start();
        //动画作用的元素、动画名称、动画开始、结束、以及中间的任意个属性值

    }
```
## ValueAnimator
```java
    // 空构造方法
    ValueAnimator animator = new ValueAnimator();

    // 创建一个实例 ，开始点0f，结束点位100f，期间经过50
    ValueAnimator animator = ValueAnimator.ofFloat(0, 50 ,100);

    // 创建一个实例 ，开始点0f，结束点位100
    ValueAnimator animator = ValueAnimator.ofInt(0, 100);

    //创建一个实例 ，开始点0，结束点位100，自定义返回的类型
    ValueAnimator animator = ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                // 这里的三个值分别代表了：一个0~1的float， 开始点， 结束点
                return new XXX()
            }
        } , 0 , 100);
```
### AnimatorUpdateListener
>实现这个接口的实例，在每个动画帧都会收到回调，在回调中，你可以得到ValueAnimator在当前帧的值。这样就能在每一帧的时候做出对应的操作，得到一个我们想要的动画效果
```java
    animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Log.i("animator" , animation.getAnimatedValue()+ "");
            }
        });
```
### PropertyValuesHolder
>可以看到，你传进去的int数值也是转成了PropertyValuesHolder，再通过setValue();方法设置给了ValueAnimator
```java
    public static ValueAnimator ofInt(int... values) {
        ValueAnimator anim = new ValueAnimator();
        // 调用了setIntValues();
        anim.setIntValues(values);
        return anim;
    }
    public void setIntValues(int... values) {
        if (values == null || values.length == 0) {
            return;
        }
        if (mValues == null || mValues.length == 0) {
            // 在这里，同样是调用了setValues(PropertyValuesHolder)方法
            setValues(PropertyValuesHolder.ofInt("", values));
        } else {
            PropertyValuesHolder valuesHolder = mValues[0];
            valuesHolder.setIntValues(values);
        }
        // New property/values/target should cause re-initialization prior to starting
        mInitialized = false;
    }
```
## 沿贝塞尔曲线移动的动画
```java
    ValueAnimator animator = ValueAnimator.ofObject(new TypeEvaluator() {
            @Override
            public Object evaluate(float fraction, Object startValue, Object endValue) {
                // 得到了与时间相关的从0~1的数，以及开始点和结束点。
                // 就可以通过贝塞尔曲线的公式计算出不同时间XY所对应的点
                // 让空间沿着这些点移动就是一条曲线移动的动画了
                final float t = fraction;
                float oneMinusT = 1.0f - t;
                PointF point = new PointF();
                PointF point0 = (PointF)startValue;
                PointF point1 = new PointF();
                point1.set(width, 0);
                PointF point2 = new PointF();
                point2.set(0, height);
                PointF point3 = (PointF)endValue;
                point.x = oneMinusT * oneMinusT * oneMinusT * (point0.x)
                        + 3 * oneMinusT * oneMinusT * t * (point1.x)
                        + 3 * oneMinusT * t * t * (point2.x)
                        + t * t * t * (point3.x);
                point.y = oneMinusT * oneMinusT * oneMinusT * (point0.y)
                        + 3 * oneMinusT * oneMinusT * t * (point1.y)
                        + 3 * oneMinusT * t * t * (point2.y)
                        + t * t * t * (point3.y);
                return point;
            }
        } , 0 , 100);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF point = (PointF) animation.getAnimatedValue();
                // 接收到了在TypeEvaluator中计算出的PointF对象
                view.setX(point.x);
                view.setY(point.y);
            }
        });
        animator.setDuration(2000);
        animator.start();
```
## 使用XML定义属性动画 
>在res下建立animator文件夹，然后建立res/animator/scalex.xml
```xml
<?xml version="1.0" encoding="utf-8"?>  
<objectAnimator xmlns:android="http://schemas.android.com/apk/res/android"  
    android:duration="1000"  
    android:propertyName="scaleX"  
    android:valueFrom="1.0"  
    android:valueTo="2.0"  
    android:valueType="floatType" >  
</objectAnimator>
```
### 使用
```java
// 加载动画  
Animator anim = AnimatorInflater.loadAnimator(mContext,R.animator.scalex);  
anim.setTarget(mMv);  
anim.start(); 
```
### 多个动画使用xml加载
```xml
<?xml version="1.0" encoding="utf-8"?>  
<set xmlns:android="http://schemas.android.com/apk/res/android"  
    android:ordering="together" >  

    <objectAnimator  
        android:duration="1000"  
        android:propertyName="scaleX"  
        android:valueFrom="1"  
        android:valueTo="0.5" >  
    </objectAnimator>  
    <objectAnimator  
        android:duration="1000"  
        android:propertyName="scaleY"  
        android:valueFrom="1"  
        android:valueTo="0.5" >  
    </objectAnimator>  

</set> 
```
### 使用
```java
Animator anim = AnimatorInflater.loadAnimator(mContext, R.animator.scale);  
view.setPivotX(0);  
view.setPivotY(0);  
//显示的调用invalidate  
view.invalidate();  
anim.setTarget(view);  
anim.start();
```
## 组合动画
>有些场景会需要多中类型的动画一起播放，或者按照顺序播放，怎么搞？就要用到组合/顺序动画了。 
Android提供了一套非常丰富的API，让我们可以将多个动画按照指定的顺序来播放，这里需要借助AnimatorSet和AnimationSet. 
AnimatorSet 和 AnimationSet 都是动画集合。这里简单介绍下他们的异同，了解这些后在设计动画实现时才能得心应手。
### AnimatorSet和AnimationSet的异同点
* AnimationSet我们最常用的是调用其addAnimation将一个个不一样的动画组织到一起来，然后调用view的startAnimation方法触发这些动画执行。功能较弱不能做到把集合中的动画按一定顺序进行组织然后在执行的定制
* AnimatorSet我们最常用的是调用其play、before、with、after 等方法设置动画的执行顺序，然后调用其start触发动画执行
* Animation是针对视图外观的动画实现，动画被应用时外观改变但视图的触发点不会发生变化，还是在原来定义的位置
* Animator是针对视图属性的动画实现，动画被应用时对象属性产生变化，最终导致视图外观变化
### AnimationSet
>
```java
// 传入一个boolean值，他决定了你使用animationSet的插值器还是动画自身的
AnimationSet set = new AnimationSet(true);
set.setDuration(2000);
TranslateAnimation translate = new TranslateAnimation(0,100,0,100);
AlphaAnimation alpha = new AlphaAnimation(0 , 1);
set.addAnimation(translate);
set.addAnimation(alpha);
view.startAnimation(set);
```
### AnimatorSet
>这个类提供了一个play()方法，如果我们向这个方法中传入一个Animator对象(ValueAnimator或ObjectAnimator)将会返回一个AnimatorSet.Builder的实例，AnimatorSet.Builder中包括以下四个方法:
* after(Animator anim) 现有动画在传入动画之后执行
* after(long delay) 将现有动画延迟指定毫秒后执行
* before(Animator anim) 现有动画在传入动画之前执行
* with(Animator anim) 现有动画和传入动画同时执行
```java
private void runAnimatorSet(View view){
        ObjectAnimator moveIn = ObjectAnimator.ofFloat(view, "translationX", -500f, 0f);
        ObjectAnimator rotate = ObjectAnimator.ofFloat(view, "rotation", 0f, 360f);
        ObjectAnimator fadeInOut = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f, 1f);
        AnimatorSet animSet = new AnimatorSet();
        animSet.play(rotate).with(fadeInOut).after(moveIn);
        animSet.setDuration(5000);
        animSet.start();

        // 这里需要注意的是，如果是多个动画，强烈推荐这么写，否则会错乱。
        set.play(animator1).before(animator2);
        set.play(animator3).with(animator3_);
        set.play(animator3).after(animator2);
        set.play(animator4).after(animator3);
        set.play(animator5).after(animator4);
        set.start();
    }
```
## 插值器
* AccelerateDecelerateInterpolator 慢 - 快 - 慢
* AccelerateInterpolator 慢 - 快
* DecelerateInterpolator 快 - 慢
* AnticipateInterpolator 开始的时候向后然后向前甩
* AnticipateOvershootInterpolator 开始的时候向后然后向前甩一定值后返回最后的值
* BounceInterpolator 动画结束的时候弹起
* CycleInterpolator 动画循环播放特定的次数，速率改变沿着正弦曲线
* LinearInterpolator 以常量速率改变
* OvershootInterpolator 向前甩一定值后再回到原来位置
### 使用
```java
TranslateAnimation translate = new TranslateAnimation(0,100,0,100);
AccelerateInterpolator interpolator = new AccelerateInterpolator();
translate.setInterpolator(interpolator);
```
### 自定义差值器
>实现至Interpolator接口
```java
public class LongFaceInterpolator implements Interpolator{
    @Override
    public float getInterpolation(float input) {
        return 0;
    }
}
```
#### 参数说明
* input:取值范围是0到1，表示当前动画的进度，取0时表示动画刚开始，取1时表示动画结束，取0.5时表示动画中间的位置，其它类推
* 表示当前实际想要显示的进度。取值可以超过1也可以小于0，超过1表示已经超过目标值，小于0表示小于开始位置
## 链式动画Animate（Api>12）
```java
view.animate()
          .alpha(1)
          .translationX(100)
          .y(30)
          .setDuration(300)
          // API 16
          .withStartAction(new Runnable() {
              @Override
              public void run() {
                  // 在动画开始之前，做你想做的，他是在主线程中的
              }
          })
          // API 16
          .withEndAction(new Runnable() {
              @Override
              public void run() {
                  // 在动画结束时，做你想做的，他是在主线程中的
              }
          })
          .start();
```
### 注意
#### animator和cancel()和end()方法
>cancel动画立即停止，停在当前的位置；end动画直接到最终状态；
>如果你调用了cancel()方法停止动画，那么在开始前你必须调用reset()方法将动画还原再调用start()继续你的动画
#### 在动画正在播放的时候（如果你的动画跟界面的坐标点有关），界面失去隐藏，或者失去了焦点，如果没有处理，就会报这个空指针异常
#### 如果你用AnimationSet执行动画，并想在动画结束时remove掉其中的View，那么其他正在执行的动画可能会闪烁，无解……
#### 在属性动画中，你传递的任何值都是偏移量，像这样：
```java
// 这里传递的第一个值和第二个值都是相对于View原点的，就是X从原点增加100
PropertyValuesHolder translationX = 
PropertyValuesHolder.ofFloat("translationX", 0 ,100);

// 这句的意思就是，X从原点+100的地方 移动到 原点+500的地方，实际是移动的400的距离
PropertyValuesHolder translationX = 
PropertyValuesHolder.ofFloat("translationX", 100 ,500);
```
## 转载并修改至
[Android 动画,看完这些还不够](https://blog.csdn.net/u012984054/article/details/50841476)
























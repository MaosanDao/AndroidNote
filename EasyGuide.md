# 简易步骤参考表
这是一篇整理了日常工作中常见的问题的速查表。

## 1.View的绘制流程
* measure：判断是否需要重新计算view的大小，需要则重新计算
* layout：判断是否需要重新计算view的位置，需要则重新计算
* draw：判断是否需要重新绘制该view，需要则重新计算

## 2.自定义属性的使用方法
### 首先在style或者attr文件中定义属性：
```java
<declare-styleable name="TestView"> 
			<attr name="attrone" format="dimension"/> 
			<attr name="attrtwo" format="string" > 
				<enum name="one" value="0"/> 
				<enum name="two" value="1"/>
			 </attr> 
</declare-styleable>
```
#### 其中attr为属性的单位，而format为属性的类型
### 然后在view的自定义类中进行使用：
```Java
TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TestView); 
float attrone = ta.getDimension(R.styleable.TestView_attrone,0); 
String attrTwo = ta.getString(R.styleable.TestView_attrtwo);

//使用完毕后，需要回收：
ta.recycle();
```
### 补充
```java
<attr name="textStyle"> 
		<flag name="normal" value="0" /> 
		<flag name="bold" value="1" /> 
		<flag name="italic" value="2" /> 
</attr>
```
其中，enum和flag的区别在于，enum只能选择其一，而flag则可以累加选择。比如：bold|italic表示既加粗也变成斜体
## 3.attr、style、theme的区别
* attr是属性的最小单元
* style是通过attr组合而得到的样式，比如height、width等
* theme则是用作于一个actvity或者整个应用
### 注意
大小优先顺序：View的Style > Activity的Theme > Application的Theme
## 4.Activity的四种启动模式
* standard：标准的启动模式，无论是否在activity的栈中是否含有该实例，系统都会进行再次创建
* singalTop：如果该实例已经在栈的顶端，则不会创建新的实例，否则会进行创建
* singalTask：如果该实例已经在栈的顶端，则不会创建新的实例，如果含有该实例，否则会该实例上方的activity进行移除。
* singalInstance：无论是否含有该实例，都对进行创建。且该栈中只有这样一个实例
## 5.bitmap的三级缓存
优先顺序：内存缓存 > 本地缓存 > 网络缓存
## Andriod中存储数据的方式
* SharedPreferences
* File
* ContentProvider
* SQLite
* 网络存储
## 6.横竖屏相关知识
### 横竖屏的切换
```Java
//横屏
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//竖屏
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
```
### 判断横竖屏
```
//获取屏幕的方向  ,数值1表示竖屏，数值2表示横屏
int screenNum = getResources().getConfiguration().orientation;
```
### 横竖屏的生命周期
* 如果在注册文件中配置了以下选项：
```
android:configChanges="orientation|keyboardHidden|screenSize"
```
则生命周期不会发生改变
* 如果没有进行配置则：
第一次启动：oncreate -> onStart -> onResume -> onPause -> onStop -> onDestory
切换的时候：onPause -> onStop -> onDestory -> onCreate -> onStart ->onResume
### 监听横竖屏的切换方法：onCofigurationChanged
注意：必须要在注册文件中定义
```
android:configChanges="orientation”
```
否则，不会回调
### 默认屏幕的方向，不可更改
```
android:screenOrientation="portrait"
```
## 7.进程保活手段
### 开启一个1像素的Activity（不推荐）
基本思想，系统一般是不会杀死前台进程的。所以要使得进程常驻，我们只需要在锁屏的时候在本进程开启一个Activity，为了欺骗用户，让这个Activity的大小是1像素，并且透明无切换动画，在开屏幕的时候，把这个Activity关闭掉，所以这个就需要监听系统锁屏广播。
### 使用前台服务（推荐）
### 相互唤醒
### 粘性服务和系统服务捆绑
这个是系统自带的，onStartCommand方法必须具有一个整形的返回值，这个整形的返回值用来告诉系统在服务启动完毕后，如果被Kill，系统将如何操作，这种方案虽然可以，但是在某些情况or某些定制ROM上可能失效。
```
@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
    		return START_REDELIVER_INTENT;
	}
```
* START_STICKY
如果系统在onStartCommand返回后被销毁，系统将会重新创建服务并依次调用onCreate和onStartCommand
* START_NOT_STICKY 
如果系统在onStartCommand返回后被销毁，如果返回该值，则在执行完onStartCommand方法后如果Service被杀掉系统将不会重启该服务
* START_REDELIVER_INTENT
START_STICKY的兼容版本，不同的是其不保证服务被杀后一定能重启
### 双进程守护
## 8.热启动和冷启动的含义和区别
### 热启动
当启动应用时，后台已有该应用的进程（例：按back键/home键，应用虽然会退出，但是该应用的进程是依然会保留在后台，可进入任务列表查看），所以在已有进程的情况下，这种启动会从已有的进程中来启动应用，这个方式叫热启动
### 冷启动
当启动应用时，后台没有该应用的进程，这时系统会重新创建一个新的进程分配给该应用，这个启动方式就是冷启动
### 无进程启动顺序
Application的构造器方法 ——>attachBaseContext() ——>onCreate() ——>Activity的构造方法 ——>onCreate() ——>配置主题中背景等属性 ——>onStart() ——>onResume() ——>测量布局绘制显示在界面上
## 9.ANR和OOM
### ANR
在Android上,如果你的应用程序有一段时间内响应不够灵敏,系统会向用户显示一个对话框,这个对话框称作为应用程序无响应(ANR: Application Not Responding)对话框.用户可以选择”等待”而让程序继续运行,也可以选择”强制关闭”
#### 导致ANR的原因
* 在5秒内没有相应输入的事件
* 广播没有在10秒内处理完毕
#### 怎么避免
* 尽可能不要在主线程中处理复杂的逻辑
* 避免在广播处理中做耗时的操作
* 避免在Intent的Recive中启动一个activity
#### 总结
不要在activity中做耗时的任务，多用handler和message。在广播中不要做耗时任务，如果要做，则可以启动服务去做。（广播中可以启动服务，但是服务中无法启动广播）
### OOM（内存溢出）
#### 出现的原因
* 加载的对象过大
* 相应资源过多，来不及释放
#### 如何解决
* 在内存引用上做处理
* 在内存中加载图片时，直接在内存中做处理









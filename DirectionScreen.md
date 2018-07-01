# 横竖屏的切换
```Java
//横屏
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//竖屏
setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
```
## 判断横竖屏
```
//获取屏幕的方向  ,数值1表示竖屏，数值2表示横屏
int screenNum = getResources().getConfiguration().orientation;
```
## 横竖屏的生命周期
* 如果在注册文件中配置了以下选项：
```
android:configChanges="orientation|keyboardHidden|screenSize"
```
则生命周期不会发生改变
* 如果没有进行配置则：
第一次启动：onCreate -> onStart -> onResume -> onPause -> onStop -> onDestory
切换的时候：onPause -> onStop -> onDestory -> onCreate -> onStart ->onResume
## 监听横竖屏的切换方法：onCofigurationChanged
注意：必须要在注册文件中定义
```
android:configChanges="orientation”
```
否则，不会回调
## 默认屏幕的方向，不可更改
```
android:screenOrientation="portrait"
```

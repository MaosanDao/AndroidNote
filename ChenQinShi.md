# 沉侵式的具体做法
>一个Android应用程序的界面上其实有很多系统元素的，比如，状态栏、ActionBar、导航栏等。而打造沉侵式的用户体验就是将这些元素隐藏或者透明掉。
## 注意事项
* 低版本的（Android 4.1以下）不提供沉侵式的体验
## 隐藏状态栏和ActionBar
### 效果
* 隐藏状态栏
* 隐藏ActionBar
* 底部导航栏存在
>在onCreate中，setContentView后调用
```java
//隐藏ActionBar和状态栏来达到沉侵式的效果
View decorView = getWindow().getDecorView();
int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
decorView.setSystemUiVisibility(option);
ActionBar actionBar = getSupportActionBar();
if(actionBar!=null) {
    actionBar.hide();
}
```
>注意：不会隐藏底部导航栏，以及在顶部下滑的时候会出现状态栏的显示
### 图示
![ChenQin1](https://github.com/MaosanDao/AndroidNote/blob/master/chenqin_1.png)
## 隐藏ActionBar和将状态栏透明化
### 效果
* 状态栏透明化
* 隐藏ActionBar
* 底部导航栏存在
>在onCreate中，setContentView后调用
```java
if (Build.VERSION.SDK_INT >= 21) {
    View decorView = getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    decorView.setSystemUiVisibility(option);
    //状态栏透明化
    getWindow().setStatusBarColor(Color.TRANSPARENT);
}
//隐藏ActionBar
ActionBar actionBar = getSupportActionBar();
if(actionBar!=null) {
    actionBar.hide();
}
```
### 图示
![ChenQin2](https://github.com/MaosanDao/AndroidNote/blob/master/chenqin_2.png)
## 透明状态栏以及底部导航栏和ActionBar
### 效果
* 状态栏透明化
* 隐藏ActionBar
* 底部导航栏透明
>在onCreate中，setContentView后调用
```java
if (Build.VERSION.SDK_INT >= 21) {
    View decorView = getWindow().getDecorView();
    int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
    decorView.setSystemUiVisibility(option);
    getWindow().setNavigationBarColor(Color.TRANSPARENT);
    getWindow().setStatusBarColor(Color.TRANSPARENT);
}
ActionBar actionBar = getSupportActionBar();
if(actionBar!=null) {
    actionBar.hide();
}
```
### 图示
![ChenQin1](https://github.com/MaosanDao/AndroidNote/blob/master/chenqin_3.png)
## 全部都隐藏，且在上下边缘滑动的时候出现导航栏和状态栏
```java
@Override
public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus && Build.VERSION.SDK_INT >= 19) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }
}
```
### 图示
![ChenQin4](https://github.com/MaosanDao/AndroidNote/blob/master/chenqin_gif.gif)
## 修改整理至
[Android状态栏微技巧，带你真正理解沉浸式模式](http://www.androidchina.net/8943.html)


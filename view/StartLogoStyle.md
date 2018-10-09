# 启动时显示默认的图片背景的方法

在你应用的整个App的style中增加以下代码：
```xml
<!-- Base application theme. -->
<style name="AppTheme" parent="@style/Theme.AppCompat.Light.NoActionBar">
    <item name="windowActionBar">false</item>//取消Actionbar
    <item name="windowNoTitle">true</item>
    <item name="android:windowFullscreen">false</item>//设置全屏
    <item name="android:windowBackground">@mipmap/welcome_bg</item>//设置背景
</style>
```

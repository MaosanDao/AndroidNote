# UI布局的时候，常见的问题集合

## Android 5.0以上版本去掉Button自带阴影效果的方法
设置该属性就可以去掉自带的阴影。 
该属性在API 11及以上可用。
```xml
<Button
android:layout_width="wrap_content"
android:layout_height="wrap_content"
android:text="@string/button_send"
android:onClick="sendMessage"
style="?android:attr/borderlessButtonStyle" />
```
## [透明度的设置](https://blog.csdn.net/hewuzhao/article/details/78821954)
## Android中修改弹出dialog背景无色透明，弹出时有遮罩
```xml
<style name="dialog" parent="@android:style/Theme.Dialog">
  <item name="android:windowFrame">@null</item>
  <item name="android:windowIsFloating">true</item>
  <item name="android:windowIsTranslucent">true</item>
  <item name="android:windowNoTitle">true</item>
  <item name="android:background">@android:color/transparent</item>
  <item name="android:windowBackground">@android:color/transparent</item>
  <item name="android:backgroundDimEnabled">true</item>
  <item name="android:backgroundDimAmount">0.6</item>
</style>
```

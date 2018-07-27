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

# 自定义Dialog的一般实现步骤

## 首先定义一个类继承自Dialog
```java
public class CustomDialog extends Dialog
```
## 在类中加载style样式
```java
public CustomDialog(@NonNull Context context) {
      super(context, R.style.ErrorDialog);
  }
```
### style样式
```xml
<!-- 提醒弹出框 -->
<style name="ErrorDialog" parent="@android:style/Theme.Dialog">
    <item name="android:windowFrame">@null</item>
    <item name="android:windowIsFloating">true</item>
    <item name="android:windowIsTranslucent">true</item>
    <item name="android:windowNoTitle">true</item>
    <item name="android:background">@android:color/transparent</item>
    <item name="android:windowBackground">@android:color/transparent</item>
    <item name="android:backgroundDimEnabled">true</item>
    <item name="android:backgroundDimAmount">0.3</item>
</style>
```
## 在onCreate中初始化布局和View
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.dialog_confirm_cancel_layout);
    LogTrack.v("CustomDialog onCreate");
    initView();
}

//初始化View
private void initView() {
    mDialogTopImage = findViewById(R.id.dialog_top_image);
    mDialogTitleText = findViewById(R.id.dialog_title_text);
    mDialogSubTitleText = findViewById(R.id.dialog_sub_title_text);
    mDialogCancelBtn = findViewById(R.id.dialog_cancel_btn);
    mDialogConfirmBtn = findViewById(R.id.dialog_confirm_btn);
}
```
## 使用方法
```java
CustomDialog dialog = new CustomDialog(appCompatActivity);
dialog.show();//先show，然后再设置其中的一些属性方法。不然会报空指针
dialog.setTitleText(title)
        .setSubTitleText(subTitle)
        .setButtonClickListener(new CustomDialog.ButtonClickListener() {
            @Override
            public void confirmClick() {
                appCompatActivity.finish();
            }

            @Override
            public void cancelClick() {

            }
        });
```
## *注意*
>>关于设置View的一些状态和调用的时候，需要先将dialog.show()调用后，才能进行设置。










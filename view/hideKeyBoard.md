# 点击界面其他部分，隐藏虚拟键盘

## 判断是否能隐藏
```java
/**
 * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时则不能隐藏
 */
public static boolean isShouldHideKeyboard(View v, MotionEvent event) {
    if (v != null && (v instanceof EditText)) {
        int[] l = {0, 0};
        v.getLocationInWindow(l);
        int left = l[0],
                top = l[1],
                bottom = top + v.getHeight(),
                right = left + v.getWidth();
        return !(event.getX() > left && event.getX() < right
                && event.getY() > top && event.getY() < bottom);
    }
    // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditText上，和用户用轨迹球选择其他的焦点
    return false;
}
```
## 隐藏软键盘
```java
/**
 * 获取InputMethodManager，隐藏软键盘
 */
public static boolean hideKeyboard(Context context, IBinder token) {
    if (token != null) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        return im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
    }
    return false;
}
```
## 最后使用：
* 在Activity中重写dispatchTouchEvent方法即可
```java
//点击其他区域隐藏键盘
override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
    if (ev.action == MotionEvent.ACTION_DOWN) {
        val v = currentFocus
        if (ViewUtil.isShouldHideKeyboard(v, ev)) {
            val res = ViewUtil.hideKeyboard(this, v.windowToken)
            if (res) {
                //隐藏了输入法，则不再分发事件
                return true
            }
        }
    }
    return super.dispatchTouchEvent(ev)
}
```

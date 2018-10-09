# 常用的动画方法集合
## 内容列表
* [从控件所在位置移动到控件的底部](#从控件所在位置移动到控件的底部)
* [从控件的底部移动到控件所在位置](#从控件的底部移动到控件所在位置)
* [从控件所在位置移动到控件的顶部](#从控件所在位置移动到控件的顶部)
* [翻转卡片](#翻转卡片)
* [按键防抖方法（防止用户快速不停的点击按键）](#按键防抖方法)
* [设置edittext的hint字体的大小](#设置edittext的hint字体的大小)
* [扩大点击区域](#扩大点击区域)
* [界面中点击其他区域隐藏键盘](#界面中点击其他区域隐藏键盘)
* [导航栏状态栏透明(沉侵式)](#导航栏状态栏透明)
* [动态隐藏和显示密码](#动态隐藏和显示密码)

## 从控件所在位置移动到控件的底部
### [👆](#内容列表)
```java

private boolean ismHiddenActionstart = false;

/**
 * 从控件所在位置移动到控件的底部
 *
 * @param v        目标View
 * @param duration 动画时间
 */
public void moveToViewBottom(final View v, long duration, final MyAnimationListener listener) {
    if (v.getVisibility() != View.VISIBLE) {
        return;
    }
    if (ismHiddenActionstart) {
        return;
    }
    TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
    mHiddenAction.setDuration(duration);
    v.clearAnimation();
    v.setAnimation(mHiddenAction);
    mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            ismHiddenActionstart = true;
            if (listener != null) {
                listener.animationStart();
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            v.setVisibility(View.GONE);
            ismHiddenActionstart = false;
            if (listener != null) {
                listener.animationEnd();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
}
```
## 从控件的底部移动到控件所在位置
### [👆](#内容列表)
```java
/**
 * 从控件的底部移动到控件所在位置
 *
 * @param v        目标view
 * @param duration 动画时间
 */
public void bottomMoveToViewLocation(final View v, long duration, final MyAnimationListener listener) {
    if (v.getVisibility() == View.VISIBLE) {
        return;
    }
    v.setVisibility(View.VISIBLE);
    TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
    mShowAction.setDuration(duration);
    v.clearAnimation();
    v.setAnimation(mShowAction);

    mShowAction.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            if (listener != null) {
                listener.animationStart();
            }
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (listener != null) {
                listener.animationEnd();
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
}
```
## 从控件所在位置移动到控件的顶部
### [👆](#内容列表)
```java
private boolean ismHiddenActionstart = false;
/**
 * 从控件所在位置移动到控件的顶部
 *
 * @param v        目标view
 * @param duration 动画时间
 */
public void moveToViewTop(final View v, long duration) {
    if (v.getVisibility() != View.VISIBLE) {
        return;
    }
    if (ismHiddenActionstart) {
        return;
    }
    TranslateAnimation mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
            Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
            0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
    mHiddenAction.setDuration(duration);
    v.clearAnimation();
    v.setAnimation(mHiddenAction);
    mHiddenAction.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            ismHiddenActionstart = true;
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            v.setVisibility(View.GONE);
            ismHiddenActionstart = false;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    });
}
```
## 翻转卡片
### [👆](#内容列表)
* 需要引入第三方库：[YoYo](https://github.com/daimajia/AndroidViewAnimations)
* 需要设置镜头深度
```java
/**
 * 改变视角距离, 贴近屏幕 
 * 参数：目标的两个View
 */
public void setCameraDistance(View... views) {
    int distance = 16000;
    float scale = BaseApplication.getContext().getResources()
            .getDisplayMetrics().density * distance;

    for (View view :
            views) {
        view.setCameraDistance(scale);
        view.setCameraDistance(scale);
    }
}
```
```java
/**
 * 翻转卡片
 *
 * @param fromView 初始View
 * @param toView   目标View
 * @param isSelect 是否翻转 -- 解决了点击了遥控按钮后，退出界面，再次进入界面会发生卡片展示错乱的问题
 */
public void filpCardByYoYo(final View fromView, final View toView, boolean isSelect) {
    if (!isSelect) {
        YoYo.with(Techniques.FlipOutY)
                .duration(FILP_DURATION_TIME)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        toView.setVisibility(View.INVISIBLE);
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.FlipInY)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        toView.setVisibility(View.VISIBLE);
                                    }
                                })
                                .duration(FILP_DURATION_TIME)
                                .playOn(toView);
                    }
                })
                .playOn(fromView);
    } else {
        YoYo.with(Techniques.FlipOutY)
                .duration(FILP_DURATION_TIME)
                .onStart(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        fromView.setVisibility(View.INVISIBLE);
                    }
                })
                .onEnd(new YoYo.AnimatorCallback() {
                    @Override
                    public void call(Animator animator) {
                        YoYo.with(Techniques.FlipInY)
                                .onStart(new YoYo.AnimatorCallback() {
                                    @Override
                                    public void call(Animator animator) {
                                        fromView.setVisibility(View.VISIBLE);
                                    }
                                })
                                .duration(FILP_DURATION_TIME)
                                .playOn(fromView);
                    }
                })
                .playOn(toView);
    }
}
```
## 按键防抖方法
### [👆](#内容列表)
```java
private static long firstTime = 0;
/**
 * 防抖
 *
 * @param limitTime 极限响应时间
 * @return 是否处理
 */
public static boolean limitInput(long limitTime) {
    long secondTime = System.currentTimeMillis();
    if (secondTime - firstTime > limitTime) {
        long time = secondTime - firstTime;
        firstTime = secondTime;
        LogTrack.v("防抖通过");
        return true;
    } else {
        long time = secondTime - firstTime;
        LogTrack.v("防抖不通过");
        return false;
    }
}
```
## 设置edittext的hint字体的大小
### [👆](#内容列表)
```java
/**
 * ========================================================
 * 设置EditText的hint字体的大小
 * ========================================================
 */
public static void setEditTextHintSize(EditText editText, String hintText, int size) {
    //定义hint的值
    SpannableString ss = new SpannableString(hintText);
    //设置字体大小 true表示单位是sp
    AbsoluteSizeSpan ass = new AbsoluteSizeSpan(size, true);
    ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    editText.setHint(new SpannedString(ss));
}
```
## 扩大点击区域
### [👆](#内容列表)
```java
public static void setTouchDelegate(final View view, final int expandTouchWidth) {
    final View parentView = (View) view.getParent();
    parentView.post(new Runnable() {
        @Override
        public void run() {
            final Rect rect = new Rect();
            // view构建完成后才能获取，所以放在post中执行
            view.getHitRect(rect);
            // 4个方向增加矩形区域
            rect.top -= expandTouchWidth;
            rect.bottom += expandTouchWidth;
            rect.left -= expandTouchWidth;
            rect.right += expandTouchWidth;

            parentView.setTouchDelegate(new TouchDelegate(rect, view));
        }
    });
}
```
## 界面中点击其他区域隐藏键盘
### [👆](#内容列表)
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
#### 使用
在activity中：
```java
/**
 * 点击其他区域隐藏键盘
 */
@Override
public boolean dispatchTouchEvent(MotionEvent ev) {
    if (ev.getAction() == MotionEvent.ACTION_DOWN) {
        View v = getCurrentFocus();
        if (ViewUtil.isShouldHideKeyboard(v, ev)) {
            boolean res = ViewUtil.hideKeyboard(this, v.getWindowToken());
            if (res) {
                //隐藏了输入法，则不再分发事件
                return true;
            }
        }
    }
    return super.dispatchTouchEvent(ev);
}

/**
 * 获取InputMethodManager，隐藏软键盘
 */
public static boolean hideKeyboard(Context context, IBinder token) {
    if (token != null) {
        InputMethodManager im = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (im != null) {
            return im.hideSoftInputFromWindow(token, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    return false;
}
```
## 导航栏状态栏透明
### [👆](#内容列表)
```java
/**
 * 导航栏，状态栏透明
 *
 * @param activity Activity
 */
public static void setNavigationBarStatusBarTranslucent(Activity activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        View decorView = activity.getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        decorView.setSystemUiVisibility(option);
        activity.getWindow().setNavigationBarColor(Color.TRANSPARENT);
        activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
    }
    ActionBar actionBar = activity.getActionBar();
    if (actionBar != null) {
        actionBar.hide();
    }
}

/**
 * 注意：配合上方的“setNavigationBarStatusBarTranslucent”一起使用可以将状态栏字体颜色改为黑色且透明
 * <p>
 * Flag只有在使用了FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
 * 并且没有使用 FLAG_TRANSLUCENT_STATUS的时候才有效，也就是只有在状态栏全透明的时候才有效。
 */
public static void setStatusBarMode(Activity activity, boolean bDark) {
    //6.0以上
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        View decorView = activity.getWindow().getDecorView();
        if (decorView != null) {
            int vis = decorView.getSystemUiVisibility();
            if (bDark) {
                vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(vis);
        }
    }
}
```
## 动态隐藏和显示密码
### [👆](#内容列表)
```java
/**
 * 显示WiFi输入密码
 */
private void showWifiPassword(EditText editText) {
    editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    // 使光标始终在最后位置
    Editable etable = mAddDevicePasswordEdit.getText();
    Selection.setSelection(etable, etable.length());
}

/**
 * 隐藏WiFi输入密码
 */
private void dismissWifiPassword(EditText editText) {
    editText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    // 使光标始终在最后位置
    Editable etable = mAddDevicePasswordEdit.getText();
    Selection.setSelection(etable, etable.length());
}
```

# 快速搭建应用的步骤

### 首先设置过滤文件：gitignore
>Android gitignore文件参考：[android gitignore](https://github.com/github/gitignore/blob/master/Android.gitignore)

## 常用框架地址
* [ButterKnife](https://github.com/JakeWharton/butterknife)
* [GreenDao](https://github.com/greenrobot/greenDAO)
* [Glide](https://github.com/bumptech/glide)
* [RxPermissions](https://github.com/tbruyelle/RxPermissions)
* [RxJava](https://github.com/ReactiveX/RxJava)
>另外可能需要用到：
```gradle
//RxJava Android Scheduler
implementation 'io.reactivex.rxjava2:rxandroid:2.0.2'
```
* [Hawk](https://github.com/orhanobut/hawk)
* [Dagger(Google)](https://github.com/google/dagger)
>另外可能需要用到：
```gradle
// if you use the support libraries
annotationProcessor 'com.google.dagger:dagger-android-processor:2.16'
annotationProcessor 'com.google.dagger:dagger-compiler:2.16'
```
* [Logger](https://github.com/orhanobut/logger)
```java
private void initLogger() {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(false)
                .methodCount(1)
                .methodOffset(0)
                .tag("RoBotN1")
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }
```
* [Toast](https://github.com/GrenderG/Toasty)

## 适配
* [屏幕适配](https://github.com/MaosanDao/AndroidNote/blob/master/ScreenAdaptation.md)

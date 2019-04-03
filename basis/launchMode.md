# Activity的四种启动模式
## 如何设置Activity的启动模式
```
1.在AndroidManifest.xml中给对应的Activity设定属性:
android:launchMode="standard|singleInstance|single Task|singleTop"。

2.通过标记位设定，方法是intent.addFlags(Intent.xxx)。
```
## 四个LaunchMode
### 标准模式之standrd
```
默认模式，允许多个实例。

每次启动一个Activity就会创建一个新的实例，无论是否在activity的栈中是否含有该实例，系统都会进行再次创建。

注意：使用ApplicationContext去启动standard模式Activity就会报错。
     因为standard模式的Activity会默认进入启动它所属的任务栈，但是由于非Activity的Context没有所谓的任务栈。
```
### 栈顶复用模式之singleTop
```
相比于standard，有新的启动请求时，只有在目标Activity处于当前栈顶时，
才会调用onNewIntent()而不创建新实例，其他情况都和standard一致。
```
### 栈内复用模式之singleTask
```
只要该Activity在一个任务栈中存在，都不会重新创建，并回调onNewIntent(intent)方法。

如果不存在，系统会先寻找是否存在需要的栈，如果不存在该栈，就创建一个任务栈，并把该Activity放进去；
如果存在，就会把位于这个栈中的Activity实例上面的Activity全部结束掉，即最终这个Activity实例会位于任务的堆栈顶端中。
```
### 单实例模式之singleInstance
```
具有此模式的Activity只能单独位于一个任务栈中，且此任务栈中只有唯一一个实例。
```
## 推荐阅读
* [Android的启动模式：singleTask与singleTop的使用](https://www.jianshu.com/p/f32b40db6141)

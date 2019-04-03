# Activity的四种启动模式及IntentFilter匹配规则
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
***
## IntentFilter匹配规则
### 匹配原则
```
1.一个intent只有同时匹配某个Activity的intent-filter中的action、category、data才算全部匹配，
  故才能启动这个Activity。
  
2.一个Activity可以有多个 intent-filter，一个 intent只要成功匹配任意一组 intent-filter，
  就可以启动该Activity。
```
#### action匹配规则
```
1.要求intent中的action 存在且必须和intent-filter中的其中一个 action相同。

2.区分大小写。
```
#### category的匹配规则
```
1.intent中的category可以不存在，这是因为此时系统给该Activity默认加上了
<category android:name="android.intent.category.DEAFAULT"/>属性值。

2.除上述情况外，有其他category，则要求intent中的category和intent-filter中的所有category相同。
```
#### data的匹配规则
```
1.如果intent-filter中有定义data，那么Intent中也必须也要定义date。

2.data主要由mimeType(媒体类型)和URI组成。
  在匹配时通过intent.setDataAndType(Uri data, String type)方法对date进行设置。
```






































# 原生Java和H5之间的交互
* WebView如果加载H5页面
* Android如何调用H5中的方法
* H5如何调用Android中的方法

## WebView如何加载H5页面
### 加载本地的HTML
```java
mWebView.loadUrl("file:///android_asset/test.html");
```
### 加载网页
```java
mWebView.loadUrl("http://www.baidu.com");
```
## Android如何调用H5中的方法
>想要调用js方法那么就必须让webView支持
```java
WebSettings webSettings = mWebView.getSettings();
//设置为可调用js方法
webSettings.setJavaScriptEnabled(true);
```
### 如何进行调用
>Android调用H5中的方法，其实很简单，直接调用就可以了，不需要额外的操作

1.调用H5中无参无返回值的方法
```java
mWebView.loadUrl("JavaScript:show()");
```
2.调用H5中带返回值的方法
```java
//可以调用mWebView.evaluateJavascript()方法，该方法只在安卓4.4以上版本适用
mWebView.evaluateJavascript("sum(1,2)",new ValueCallback() {        
       @Override
       public void on ReceiveValue(String value) {           
            Log.e(TAG,"onReceiveValue value=" + value);       
       }   
 });
```
3.调用H5中带参数的方法
>当调用H5中带参数的方法时，势必要传入一个字符串。
```java
//当传入固定字符串时，用单引号括起来即可
mWebView.loadUrl("javascript:alertMessage('哈哈')")

//传入变量名时，需要用到转义符
String content="9880";
mWebView.loadUrl("javascript:alertMessage(\" "     +content+     "\")")
```
## H5如何调用Android中的方法
* 建立一个类，并规定别名为JsInteration
```java
public  class  JsInteration  {
      @JavascriptInterface
      public String back() {
             return "hello world";   
       }
}
```
* 定义完这个类后，只需要使用webview进行设置
```java
mWebView.addJavascriptInterface(newJsInteration(),"android");
```
* 如何进行调用
```js
function s() {
      //这里使用了back方法
      var result=window.android.back();
      document.getElementById("p").innerHTML=result;
}
```
## 注意事项
* 当自己写html文件时，可能会出现显示乱码，我们需要指定格式
* H5调用我们的方法时，我们需要把规定的别名传给H5（切记一定不能错），而且我们要在自己的方法里执行H5想要的操作
* 给H5调用的方法一定要加@JavascriptInterface，不然H5调不到我们的方法
* 只有Android 4.4以上能webView.evaluateJavascript方法直接拿到返回值
* 当版本低于Android 4.4时，常规的思路为：

1.Java调用js代码
```java
String call = "javascript:sumToJava(1,2)";
webView.loadUrl(call);
```
2..js函数处理，并将结果通过调用java方法返回
```java
function sumToJava(number1, number2){
       window.control.onSumResult(number1 + number2)
}
```
3.Java在回调方法中获取js函数返回值
```java
@JavascriptInterface
public void onSumResult(int result) {
  Log.i(LOGTAG, "onSumResult result=" + result);
}
```
* 加载本地assets里的H5界面，要写成android_asset,而不是assets
## 摘录自
[安卓混合开发——原生Java和H5交互，保证你一看就懂！](https://www.jianshu.com/p/0b986d6e2e17)













































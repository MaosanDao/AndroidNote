# Retrofit 2.0使用指南
```
Retrofit 是一个RESTful的HTTP网络请求框架的封装，网络请求的工作本质上是OkHttp完成，
而 Retrofit仅负责网络请求接口的封装。
```
![img1](http://upload-images.jianshu.io/upload_images/944365-b5194f1d16673589.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
***
## 使用步骤
* 添加Retrofit库的依赖
* 创建 接收服务器返回数据 的类 
* 创建 用于描述网络请求 的接口 
* 创建 Retrofit 实例
* 创建 网络请求接口实例 并 配置网络请求参数
* 发送网络请求（异步 / 同步）
* 处理服务器返回的数据
### 添加库的依赖
```java
dependencies {
    compile 'com.squareup.retrofit2:retrofit:2.0.2'
    // Retrofit库
    compile 'com.squareup.okhttp3:okhttp:3.1.2'
    // Okhttp库
  }
  
//网络权限
<uses-permission android:name="android.permission.INTERNET"/>
```
### 接收服务器返回数据(实体类)
```
用于Json解析或者xml解析的数据实体类。
```
### 创建接口
```java
public interface GetRequest_Interface {

    @GET("openapi.do?keyfrom=Yanzhikai&key=2032414398&type=data&doctype=json&version=1.1&q=car")
    Call<Translation>  getCall();
    // @GET注解的作用:采用Get方法发送网络请求

    // getCall() = 接收网络请求数据的方法
    // 其中返回类型为Call<*>，*是接收数据的类（即上面定义的Translation类）
    // 如果想直接获得Responsebody中的内容，可以定义网络请求返回值为Call<ResponseBody>
}
```
这里查看常用的注解：[链接](https://github.com/MaosanDao/AndroidNote/tree/master/retrofit)
### 创建Retrofit实例
```java
Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fanyi.youdao.com/") // 设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) // 设置数据解析器
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) // 支持RxJava平台
                .build();
```
#### 数据解析器

# Retrofit 2.0使用指南（直接使用，未使用RxJava配合）
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
```
Gson	    com.squareup.retrofit2:converter-gson:2.0.2
Jackson	    com.squareup.retrofit2:converter-jackson:2.0.2
Simple XML	com.squareup.retrofit2:converter-simplexml:2.0.2
Protobuf	com.squareup.retrofit2:converter-protobuf:2.0.2
Moshi	    com.squareup.retrofit2:converter-moshi:2.0.2
Wire	    com.squareup.retrofit2:converter-wire:2.0.2
Scalars	    com.squareup.retrofit2:converter-scalars:2.0.2
```
### 创建网络请求接口实例
```java
// 创建 网络请求接口 的实例
GetRequest_Interface request = retrofit.create(GetRequest_Interface.class);

//对 发送请求 进行封装
Call<Reception> call = request.getCall();
```
### 发送请求
```java
//发送网络请求(异步)
call.enqueue(new Callback<Translation>() {
    //请求成功时回调
    @Override
    public void onResponse(Call<Translation> call, Response<Translation> response) {
        //请求处理,输出结果
        response.body().show();
    }

    //请求失败时候的回调
    @Override
    public void onFailure(Call<Translation> call, Throwable throwable) {
        System.out.println("连接失败");
    }
});

// 发送网络请求（同步）
Response<Reception> response = call.execute();
```





























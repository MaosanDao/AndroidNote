# Retrofit2原理探析
***
## 如何快速使用？
```
比如要请求这个接口：
  https://zhuanlan.zhihu.com/api/columns/{user}
```
### 首先创建一个Retrofit实例对象
```java
//指定BaseUrl
public static final String API_URL = "https://zhuanlan.zhihu.com";
 
//Retrofit实例
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl(API_URL)//指定BaseUrl
    .addConverterFactory(GsonConverterFactory.create())//数据解析器，这里是Gson
    .build();
```
### 根据Api创建一个接口
```java
public interface ZhuanLanApi {
    @GET("/api/columns/{user} ")
    Call<ZhuanLanAuthor> getAuthor(@Path("user") String user)
}
```
### 使用这个实例创建一个Api的对象
```java
ZhuanLanApi api = retrofit.create(ZhuanLanApi.class);
Call<ZhuanLanAuthor> call = api.getAuthor("qinchao");
```
### 开始请求
```java
// 请求数据，并且处理response
call.enqueue(new Callback<ZhuanLanAuthor>() {
    @Override
    public void onResponse(Response<ZhuanLanAuthor> author) {
        System.out.println("name： " + author.getName());
    }
    @Override
    public void onFailure(Throwable t) {
    }
});
```
```
enqueue方法是异步发送http请求的，如果你想用同步的方式发送可以使用execute()方法。
call对象还提供cancel()、isCancel()等方法获取这个Http请求的状态
```
## 原理探究
```java
ZhuanLanApi api = retrofit.create(ZhuanLanApi.class);
```
```
Retrofit就是充当了一个适配器的角色：
  将一个Java接口转换为一个Http请求，然后通过Okhttp去发送这个请求。
  
那么Retrofit是如何做到的呢？
```
### 动态代理
```
Java动态代理：
  当你要调用某个Class的方法前或后，插入你想要执行的代码。
  
比如：
  比如你要执行某个操作前，你必须要判断这个用户是否登录，或者你在付款前，你需要判断这个人的账户中存在这么多钱。
```
```java
/** Create an implementation of the API defined by the {@code service} interface. */
public <T> T create(final Class<T> service) {
  Utils.validateServiceInterface(service);
  if (validateEagerly) {
     eagerlyValidateMethods(service);
  }
  //返回了一个Proxy.newProxyInstance的动态代理对象
  return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[] { service },
    new InvocationHandler() {
      private final Platform platform = Platform.get();
 
      @Override public Object invoke(Object proxy, Method method, Object... args)
          throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
          return method.invoke(this, args);
        }
        if (platform.isDefaultMethod(method)) {
          return platform.invokeDefaultMethod(method, service, proxy, args);
        }
        //通过反射拿取到方法名字和参数
        ServiceMethod serviceMethod = loadServiceMethod(method);
        OkHttpCall okHttpCall = new OkHttpCall<>(serviceMethod, args);
        return serviceMethod.callAdapter.adapt(okHttpCall);
      }
    });
```
#### 为什么使用动态代理
```

```




































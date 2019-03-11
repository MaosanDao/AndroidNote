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
```java
Call<ZhuanLanAuthor> call = api.getAuthor("qinchao");
```
```
如上代码：
  其实api并非是ZhuanLanApi接口所产生的对象，当api调用getAuthor方法的时候，会被动态代理拦截掉。
  然后Retrofit会调用Proxy.newProxyInstance方法中的InvocationHandler对象。
  它的invoke方法会传递3个参数：
    Object proxy：代理对象
    Method method：方法
    Object... args：参数
 
  接下来，Retrofit会通过反射拿取到方法名字和参数。创建一个ServiceMethod对象，ServiceMethod就像
  是一个中央处理器，传入Retrofit对象和Method。最终生成一个Request。
  
  最后通过Okhttp3中的OkHttpCall返回Call对象，通过用户call.enqueue或者exceute去请求网络。
```
## 具体流程分析
### Retrofit接口
```
Retrofit定义了四种接口：

Retrofit请求数据返回的接口
  Callback<T>：
    void onResponse(Response<T> response);
    void onFailure(Throwable t);

将Http返回的数据解析成Java对象
  Converter<F, T>：就是上文中的GsonConverterFactory字样。
   
发送一个Http请求：
  Call<T>：Retrofit默认的实现是OkHttpCall<T>。
  
为RxJava设计的：
  CallAdapter<T>：这个方法的主要作用就是将Call对象转换成另一个对象，实现类就一个DefaultCallAdapter。
```
### Retrofit运行过程




































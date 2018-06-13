# 使用Retrofit上传Json数据

## 定义接口
```java
public interface PostRoute {
   @Headers({"Content-Type: application/json","Accept: application/json"})//需要添加头
   @POST("api/FlyRoute/Add")
   Call<FlyRouteBean> postFlyRoute(@Body RequestBody route);//传入的参数为RequestBody
}
```
## 使用方法
```java
FlyRouteBean flyRouteBean=new FlyRouteBean();
flyRouteBean=initdata(flyRouteBean);//根据Bean类初始化一个需要提交的数据类
Gson gson=new Gson();
String route= gson.toJson(flyRouteBean);//通过Gson将Bean转化为Json字符串形式  

Retrofit retrofit=new Retrofit.Builder()
        .baseUrl(URL)
        .addConverterFactory( GsonConverterFactory.create())
        .build();
        
PostRoute postRoute=retrofit.create(PostRoute.class);
RequestBody body=RequestBody
                 .create(okhttp3.MediaType.parse("application/json; charset=utf-8"),route);
Call<FlyRouteBean> call=postRoute.postFlyRoute(body);

call.enqueue(new Callback<FlyRouteBean>() {
  @Override
  public void onResponse(Call<FlyRouteBean> call, Response<FlyRouteBean> response) {
    Log.e("sssss","-----------------------"+response.body().getDeviceId());//这里是用于测试，服务器返回的数据就是提交的数据。
  }

  @Override
  public void onFailure(Call<FlyRouteBean> call, Throwable t) {
    Log.e("sssss",t.getMessage());
  }
});
```

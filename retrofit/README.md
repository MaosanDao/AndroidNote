# Retrofit常用注解介绍
>>@Query，@QueryMap，@Field，@FieldMap，@FormUrlEncoded，@Path，@Url这几个为常用的注解。下面会以实际的接口例子进行讲解。

## Get方式请求静态url地址
```Java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build();
    
public interface GitHubService {
    //无参数
    @GET("xxx")
    Call<List<Repo>> listRepos();
    //少数参数
    @GET("xxx")
    Call<List<Repo>> listRepos(@Query("time") long time);
    //参数较多
    @GET("xxx")
    Call<List<Repo>> listRepos(@QueryMap Map<String, String> params);//使用QueryMap参数
}
```

## Post方式请求静态url地址
```Java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build()
    
public interface GitHubService {
    //无参数
    @POST("users/stven0king/repos")
    Call<List<Repo>> listRepos();
    //少数参数
    @FormUrlEncoded
    @POST("users/stven0king/repos")
    Call<List<Repo>> listRepos(@Field("time") long time);
    //参数较多
    @FormUrlEncoded
    @POST("users/stven0king/repos")
    Call<List<Repo>> listRepos(@FieldMap Map<String, String> params);//使用FieldMap参数
}
```
## 半静态的url地址请求
```Java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build()

public interface GitHubService {
  @GET("users/{user}/repos")
  Call<List<Repo>> listRepos(@Path("user") String user);
}

拼接结果：https://api.github.com/users/MaoSanDao/repos(这个MaoSanDao则为参数“user”传入)
```
## 动态的url地址请求
```java
Retrofit retrofit = new Retrofit.Builder()
    .baseUrl("https://api.github.com/")
    .build()

public interface GitHubService {
  @GET
  Call<List<Repo>> listRepos(@Url String user);
}

说明：这里在接口使用的时候，直接传入url即可请求。
```

## Header(直接使用Header注解即可)
```Java
@GET("/")
Call<ResponseBody> foo(@Header("Accept-Language") String lang);
```
## 注意
* 当@GET或@POST注解的url为全路径时（可能和baseUrl不是一个域），会直接使用注解的url的域。
* 如果请求为post实现，那么最好传递参数时使用@Field、@FieldMap和@FormUrlEncoded。因为@Query和或QueryMap都是将参数拼接在url后面的，而@Field或@FieldMap传递的参数时放在请求体的。
* 使用@Path时，path对应的路径不能包含”/”，否则会将其转化为%2F。在遇到想动态的拼接多节url时，还是使用@Url吧。

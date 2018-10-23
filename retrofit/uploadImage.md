# 使用Retrofit上传图片到服务器中

## 接口定义
```java
/**
 * 头像上传
 *
 * @param token Token
 * @param timestamp Timestamp
 * @param sign Sign
 * @param parts Parts
 * @return Observable<UpLoadAvatarResponseModel>
 */
@Multipart //注意这个注解，需要添加
@POST("/images/upload?submit=1")
Observable<UpLoadAvatarResponseModel> upLoadAvatar(@Header("token") String token
        ,@Header("timestamp") String timestamp,@Header("sign") String sign
        , @Part List<MultipartBody.Part> parts);//这里使用@Part注解
```
## 接口使用
```java
/**
 * 头像上传
 *
 * @param token Token
 * @param timestamp Timestamp
 * @param sign Sign
 * @param avatarPath 图片名字
 * @return Observable<UpLoadAvatarResponseModel>
 */
public Observable<UpLoadAvatarResponseModel> upLoad(String token, String timestamp, String sign
        , String avatarPath) {

    String path1 = Environment.getExternalStorageDirectory() + File.separator + avatarPath;//这里传入本地图片的地址
    List<File> fileList = new ArrayList<>();
    fileList.add(new File(path1));//可以传入多个图片
    List<MultipartBody.Part> partList = filesToMultipartBodyParts(fileList);//使用辅助方法将文件转为Part

    return observe(mMakeOnecService.upLoadAvatar(token, timestamp, sign, partList))
            .map(new Func1<UpLoadAvatarResponseModel, UpLoadAvatarResponseModel>() {
                @Override
                public UpLoadAvatarResponseModel call(UpLoadAvatarResponseModel upLoadAvatarResponseModel) {
                    return upLoadAvatarResponseModel;
                }
            });
}
```

## 辅助方法（将File转为Part）
```java
/**
 * 转换文件至Part
 *
 * @param files List<File> files
 * @return List<MultipartBody.Part>
 */
private List<MultipartBody.Part> filesToMultipartBodyParts(List<File> files) {
    List<MultipartBody.Part> parts = new ArrayList<>(files.size());
    for (File file : files) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("images", file.getName(), requestBody);
        parts.add(part);
    }
    return parts;
}
```
## 注意事项：
1.可能传入和文件一起传入的参数会在服务端收到双引号。采用下面的链接去解决

[解决办法](https://blog.csdn.net/u012391876/article/details/52913805)

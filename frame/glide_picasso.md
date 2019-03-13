# Android图片加载图库Glide vs Picasso
***
## 导入方式
```xml
//Picasso
compile 'com.squareup.picasso:picasso:2.5.2'

//Glide
compile 'com.github.bumptech.glide:glide:3.7.0'
```
## 包的大小和方法数
```
Glide的方法和包的大小都较Picasso都高：

  包的大小差不多是Piscass的3.5倍
  方法数也是以2678比849相对较多。
```
## 语法差别
```java
//Picasso
Picasso.with(context)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.user_placeholder)
    .error(R.drawable.user_placeholder_error)
    .into(imageView);
    
//Glide
Glide.with(myFragment)
    .load(url)
    .centerCrop()
    .placeholder(R.drawable.loading_spinner)
    .crossFade()
    .into(myImageView);
```
```
从上述代码中，看出：
  
  Glide可以和Activity和Fragment进行生命周期的相互协调。
```
## 图片缓存
### Picasso
```
缓存的时候，会将整个图片不经过压缩就直接缓存到本地，然后再次使用的时候，会先直接返回真个图片，然后在实际加载的时候，
进行大小的适配。
```
### Glide
```
缓存的时候，会根据当时加载的图片的大小来进行缓存到本地。因此如果你使用了不同大小的图片来加载。那么Glide就会缓存两份不同
大小的图片在本地。

当然这是可以配置的，在Glide中，使用diskCacheStrategy来配置。
```
### 内存占用大小
```
加载同样的图片时，明显感觉Picasso的内存占用比Glide多。因为，Piscasso是将整个完整大小的图片加载到了内存中，
但是Glide却是先进行适配后才缓存的。所以Glide用的内存较小。
```
### Glide的独有特性
```
1.对Gif图片的支持。可以直接使用Glide.with.load的方式加载Gif。
2.由于Glide和Activity或者Fragment的生命周期相关，所以在不需要加载图片的时候，可以更快的停止动画，从而减小了后台的电量和内存消耗。
```
*** 
## 优缺点分析
### Picasso
```
优点：

1.自带统计监控功能
  支持图片缓存使用的监控，包括缓存命中率，已使用的内存大小，节省的流量等。
2.支持有限级处理
  每次任务调度前会选择优先级高的任务，比如App中的Banner优先级比Icon的高。
3.支持根据不同的网络条件，来调控线程数（最大并发数）

缺点：

1.不支持GIF
2.缓存的图片是未经过缩放调整的，即为原图缓存，且使用了ARGB_8888格式，缓存的体积过大。
```
### Glide
```
优点：

  1.不仅仅是图片缓存
    可以是GIF、Webpage、缩略图、Video等。
  2.支持优先级处理
  3.与Activity或者Fragment的生命周期一致。方便控制
  4.内存友好
    4.1 第三点中，支持trimMemory。
    4.2 内存缓存更小图片
    4.3 默认使用RGB555，虽然清晰度差，但是图片更小，可配置到ARGB_8888

```






























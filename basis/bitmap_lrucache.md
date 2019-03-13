# Android Bitmap高效加载和图片三级缓存机制
***
## 初探三级缓存
```
三级缓存的原理就是当 App 需要引用缓存时，首先到内存缓存中读取，读取不到再到本地缓存中读取，
还获取不到就到网络异步读取，读取成功之后再保存到内存和本地缓存中。

内存缓存：
  优先加载，读取速度最快。
本地缓存：
  次优先加载，速度一般，可以使用DiskLruCache
网络缓存：
  加载优先级最低，消耗流量，需要异步加载。
```
## BitMap高效加载
### 加载Bitmap的方法汇总
```
1.decodeFile 
  从文件系统中加载Bitmap对象
2.decodeResource
  从资源文件中加载Bitmap对象
3.decodeStream
  从输入流加载Bitmap对象
4.decodeByteArray
  从字节数组流中加载Bitmap对象
  
注意：
  decodeFile和decodeResource间接调用了decodeStream方法
```
### 高效加载思路
```
主要方法：

  使用控制采样率的方式来加载所需要的尺寸。即：
    使用BitmapFactory.Options中的inSampleSize来设置采样率
    
    1.inSampleSize 为1，原始图片
    2.inSampleSize 为2，宽高均为原来的 1/2，像素为原来的 1/4
    3.inSampleSize 为4，宽高均为原来的 1/4，像素为原来的 1/16
    
具体步骤：

  1.将BitmapFactory.Options 的 inJustDecodeBounds 参数设置为 true；
  2.从BitmapFactory.Options 中取出图片的原始宽高信息，也就是 outWidth 和 outHeight 参数；
  3.结合目标 View 所需大小来计算所需采样率 inSampleSize；
  4.将BitmapFactory.Options 的 inJustDecodeBounds 设置为 false，重新加载图片。

示例代码：
```
```java
public static Bitmap decodeSampledBitmapFromResoruce(Resources res,int resId,
                                                     int reqWidth,int reqHeight){
    // 获取 BitmapFactory.Options，这里面保存了很多有关 Bitmap 的设置
    final BitmapFactory.Options options = new BitmapFactory.Options();
    // 设置 true 轻量加载图片信息
    options.inJustDecodeBounds = true;
    // 由于上方设置false，这里轻量加载图片
    BitmapFactory.decodeResource(res,resId,options);
    // 计算采样率
    options.inSampleSize = calculateInSampleSize(options,reqWidth,reqHeight);
    // 设置 false 正常加载图片
    options.inJustDecodeBounds = false;
    return BitmapFactory.decodeResource(res,resId,options);
}

//根据图片的长宽计算出采样率
public static int calculateInSampleSize(BitmapFactory.Options options,
                                        int reqWidth,int reqHeight){
    final int width = options.outWidth;
    final int height = options.outHeight;
    int inSampleSize = 1;
    // 宽或高大于预期就将采样率 *=2 进行缩放
    if(width > reqWidth || height > reqHeight){
        final int halfHeight = height/2;
        final int halfWidth = width/2;
        while((halfHeight / inSampleSize) >= reqHeight &&
                (halfWidth / inSampleSize) >= reqWidth){
            inSampleSize *= 2;
        }
    }
    return inSampleSize;
}

//使用
ImageView iv_decode = (ImageView)findViewById(R.id.iv_decode);
iv_decode.setImageBitmap(BitmapUtils.decodeSampledBitmapFromResoruce(getResources(),R.drawable.mz,160,200));

```
*** 
## Android缓存策略
### LRU
```
目前最常用的一种缓存算法是 LRU(Least Recently Userd)，是近期最少使用算法，当缓存满时，优先淘汰近期最少使用的缓存对象。

LruCache：内存缓存
DisckLruCache：磁盘缓存
```
#### LruCache内存缓存
```
LruCache 是一个泛型类，内部采用 LinkedHashMap 以强引用的方式储存缓存对象，并提供 get/put 来完成获取和添加操作。
当缓存满时，会移除较早使用的缓存对象，然后添加新的缓存对象。
```
```java
public class LruCache<K, V> {
    private final LinkedHashMap<K, V> map;
}
```
```
扩展知识：
  Java Map的四个实现类：
    1.Map
      用于储存键值对，允许重复值，但不允许重复键值，覆盖键值就是替换了。
    2.HashMap
      使用hashcode值存储数据，根据键可以直接获取它的键值，所以具有很快的访问速度，但是遍历是随机的。
      它不是线程同步的。
    3.LinkedHashMap
      是Hashmap的子类，不同的是它记录插入的顺序，因此可以使用iterator来尽心顺序的遍历。
    4.HashTable
      和HashMap相似，但是它不允许键或者值为null，且支持线程同步，写入速度较慢。
    5.TreeMap
      实现至SortMap，能够把它保存的记录根据键来排序，且默认是升序排序。
      

总结:
  HashMap 和 Hashtable ：
    1.HashMap 不允许键重复但允许值重复，所以可以存在键或值为 null 的情况。但是 Hashtable 不允许键或值为 null。
    2.HashMap 不支持同步，Hashtable 线程安全。
    3.HashMap 读取速度较快，Hashtable 由于需要同步所以较慢。
    
  HashMap 和 LinkedHashMap :
    主要区别是 LinkedHashMap 是有序的，HashMap 遍历时无序。
```
#### LruCache的使用
```
LruCache 通常被用来缓存图片，但是也可以缓存其它内容到内存中。市面上主流的第三方图片加载库都有相应的缓存策略，
但是 LruCache 作为一种广泛使用的缓存算法，研究学习它有着重要意义。
```
```
public class BitmapLruCache extends LruCache<String,Bitmap> {

    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    // maxSize 一般为可用最大内存的 1/8.
    // int maxSize = (int) (Runtime.getRuntime().totalMemory()/1024/8);
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    @Override
    protected int sizeOf(String key, Bitmap value) {
        // 重写获取某个节点的内存大小，不写默认返回1
        return value.getByteCount() / 1024;
    }

    // 某节点被移除后调用该函数
    @Override
    protected void entryRemoved(boolean evicted, String key, Bitmap oldValue, Bitmap newValue) {
        super.entryRemoved(evicted, key, oldValue, newValue);
    }
}

//缓存中储存一张图片
mLruCache.put(key, bitmap);
//从缓存中获取一张图片
mLruCache.get(key);
//删除一张图片
mLruCache.remove(key);

```
##### LruCache原理初探
```
1.创建 LruCache 子类时设定储存对象的键和值类型(<K,V>)、最大可用内存，重写 sizeOf() 方法用来获取储存的对象大小。

2.子类创建时调用 LruCache 的构造函数，这个函数里储存了最大内存值以及创建 LinkedHashMap 来储存缓存对象。

  LinkedHashMap 的特性是最近使用的键值对会放置到队尾，使用 iterator 遍历时首先获取的是队首也就是最早放置并没有使用的键值对。
  
3.放置(put)缓存对象时，调用到第一步重写的 sizeOf() 方法来计算当前储存的对象大小并记录。

  放置缓存成功之后死循环进行计算，如果当前储存的对象没有超过可用最大内存，跳出。
  反之则使用 map.entrySet().iterator().next() 来获取队首的键值对并移除，然后减去移除的对象大小并记录。

4.获取(get)缓存对象，成功后直接返回，找不到则进行储存并记录当前缓存对象的总大小。

5.无论是放置还是获取缓存对象都会调用内部维护的 LinkedHashMap 进行处理，所以使用过的对象会被移动到位，最后删除。

```
***
#### DiskLruCache的使用
```
通过将缓存对象写入到文件系统从而实现缓存效果。
```
##### DiskLruCache的创建
```java
public static DiskLruCache open(File directory, int appVersion, int valueCount, long maxSize)
```
```
参数介绍：
  
  1.directory
    缓存在文件系统中的存储路径，可以放在 App 缓存中（App 删除时移除）。也可以自定义其它文件夹下。
  2.appVersion
    应用版本号，一般为 1，有时无作用意义不是太大。
  3.valueCount
    单个节点所对应的数据的格式，一般为 1.
  4.maxSize
    缓存的总大小，当超过时会清除一些缓存。
```
#### DiskLruCache缓存存储
```
DiskLruCache 的缓存添加操作是通过 Editor 完成的。
例如缓存图片，首先获取 url 所对应的 key（使用加密算法处理避免 url 字符违法），
然后通过 edit() 来获取 Editor 再获取输出流将文件输出到磁盘上，最后一定记得 commit()。
```
***
## 整理并摘录至
* [Android Bitmap 的高效加载和三级缓存](https://www.jianshu.com/p/ee7b943a6d41)























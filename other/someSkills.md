# 记录一些开发中常用的技巧

## 目录
* [ndk报错解决](#ndk报错解决) 
* [TextView缩进](#textview缩进)
* [局部刷新Recyclerview中item](#局部刷新recyclerview中item)
### 内容
#### ndk报错解决
```xml
packagingOptions {
    doNotStrip '*/mips/*.so'
    doNotStrip '*/mips64/*.so'
}
```
#### 局部刷新Recyclerview中item
```java
RecyclerView.ViewHolder viewHolder = mRecyclerView.findViewHolderForAdapterPosition(i);
if (viewHolder != null && viewHolder instanceof ItemHolder) {
    ItemHolder itemHolder = (ItemHolder) itemHolder 
        itemHolder.mButton.togglestate();
    }
}
```
##### 相关文章
[推荐阅读](https://blog.csdn.net/OneDeveloper/article/details/79721284)
#### TextView缩进
```java
 //缩进
 //text为要显示的文字
 //span为最后缩进的文字
val span = SpannableStringBuilder("缩进" + text)
span.setSpan(ForegroundColorSpan(Color.TRANSPARENT), 0, 2,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

```

# Android Apk瘦身方法小结
>众所周知，APP包体的大小，会影响推广的难度，用户不太喜欢下载太大的APP，同类型同等功能的APP中往往是包体小的更受用户的青睐，所以降低包体是一项非常必要的事情，也是最近公司的APP需要降低包体，所以总结下自己知道的降低包体的方法。
## 压缩图片
>众所周知，APP包体的大小，会影响推广的难度，用户不太喜欢下载太大的APP，同类型同等功能的APP中往往是包体小的更受用户的青睐，所以降低包体是一项非常必要的事情，也是最近公司的APP需要降低包体，所以总结下自己知道的降低包体的方法。

使用这个网站进行图片压缩：
### [TinyPNG](https://tinypng.com/)
## 使用webp图片格式
>具体可以看下webp探寻之路，里面有对webp的详细介绍，这里简单说下webp其实是谷歌开发的一种新的图片格式，它跟PNG有点相似，最大优点在于压缩率高，支持有损和无损压缩，但是Android4.0及以上才支持webp格式，4.0以下想使用webp就需要其他辅助支持库了

使用这个网站便可以在线转换:
### [智图](http://zhitu.isux.us/)
## 使用Android Lint分析器去除无用的资源文件
* Analyze --> Run Inspection by Name  -->  输入unused resource之后敲下回车Android Lint就会开始分析项目中哪里存在着无用资源
* 根据Android Lint给出的分析结果，确认资源的使用情况，确认为无用资源后（一般来说全局搜索下资源名，除了在R.java中外其他地方都没引用就是无用资源）删除即可
## 使用第三方工具 -- AndResGuard
GitHub主页：
https://github.com/shwenzhang/AndResGuard 
## 清除代码，提取公共类
## 合并重复的资源
## 依赖库的优化
* 使用更轻量级的库代替，或者优化library的大小，不然自己写更好
* 使用H5编写界面，图片云端获取
* 资源缓存库不放在assets下，云端获取更新
* 删除armable-v7包下的so、删除x86包下的so，基本上armable的so也是兼容armable-v7的，armable-v7a的库会对图形渲染方面有很大的改进，不过最好的是根据上面我们说的提供多版本APK，对不同的平台精简，再或者动态的加载so
## 开启minifyEnabled混淆代码
```gradle
android {
    buildTypes {
        release {
            minifyEnabled true
        }
    }
}
```
## 开启shrinkResources去除无用资源
```gradle
android {
    buildTypes {
        release {
            shrinkResources true
        }
    }
}
```
## 删除无用的语言资源
```gradle
android {
    defaultConfig {
        resConfigs "zh"
    }
}
```
## 使用shape背景
>特别是在扁平化盛行的当下，很多纯色的渐变的圆角的图片都可以用shape实现，代码灵活可控，省去了大量的背景图片。
## 使用着色方案
>相信你的工程里也有很多selector文件，也有很多相似的图片只是颜色不同，通过着色方案我们能大大减轻这样的工作量，减少这样的文件。






# Apk打包流程(摘录并整理至[原文](https://www.jianshu.com/p/a134d00e81ab))
***
## 目录
* [Apk文件中的包含类容](#apk文件中的包含类容) 
* [Apk的打包流程图示](#apk的打包流程) 
* [打包的详细流程图示](#打包的详细流程) 
* [Apk打包的七个步骤](#apk打包的七个步骤) 
  * [appt打包资源文件生成R文件](#appt打包资源文件生成r文件) 
  * [处理aidl文件生成相应的java文件](#处理aidl文件生成相应的java文件) 
  * [编译工程源代码生成class文件](#编译工程源代码生成class文件) 
  * [转换所有的class文件为dex文件](#转换所有的class文件为dex文件) 
  * [开始打包生成Apk文件](#打包生成Apk文件) 
    * [扩展之res和assets的打包异同点](#扩展之res和assets的打包异同点) 
  * [对Apk文件进行签名(Release环境)](#对Apk文件进行签名)  
  * [对签名后的apk文件进行对齐处理，优化apk读取速度](#对签名后的apk文件进行对齐处理)  
***
## Apk文件中的包含类容
![内容](https://upload-images.jianshu.io/upload_images/1813550-fcce9c7a4f843278?imageMogr2/auto-orient/strip%7CimageView2/2/w/752/format/webp)
```
如上图所示，分别介绍下他们的含义：

  1.lib
    存放的是so动态链接库，它是不需要做一系列的压缩处理的。
    
  2.META-INF
    这个是存放签名的文件夹，包含了签名，公钥证书等。
    
  3.res
    存放Android工程的资源文件夹
    
  4.AndroidManifest.xml
    这个是Android项目的注册文件，它位于整个项目的根目录，描述了package中暴露的组件（activities, services, 等等），
    他们各自的实现类，各种能被处理的数据和启动位置。
    
  5.classes.dex
    Android平台上的可执行文件，是由Java的class文件重新编排而来。
    
  6.resources.arsc
    这个相当于一个资源的索引表，能够快速定位资源文件夹中的资源位置。
```
## Apk的打包流程
![](https://upload-images.jianshu.io/upload_images/1813550-bc40133c18135e56?imageMogr2/auto-orient/strip%7CimageView2/2/w/950/format/webp)
### 打包的详细流程
![](https://upload-images.jianshu.io/upload_images/1813550-784ab198a68b166c?imageMogr2/auto-orient/strip%7CimageView2/2/w/536/format/webp)
```
关键工具介绍：
  1.aapt 
    Android资源打包工具	
  
  2.aidl
    Android接口描述语言转化为.java文件的工具	
    
  3.javac
    Java Compiler java代码转class文件	
    
  4.dex
    转化.class文件为Davik VM能识别的.dex文件	
    
  5.apkbuilder
    生成apk包	
  
  6.jarsigner
    .jar文件的签名工具	
  
  7.zipalign
    字节码对齐工具	
```
### Apk打包的七个步骤
#### appt打包资源文件生成R文件
```
使用aapt来打包res资源文件，生成R.java、resources.arsc和res文件，R.java文件是所有res资源的id列表。

resources.arsc的作用：

  在R文件中只是每个资源的ID不一样，但是我们怎么分辨不同分辨率下的资源呢？
  
  这时候我们就需要resources.arsc文件了，esources.arsc里面会对所有的资源id进行组装，
  在apk运行时获取资源的时候会根据设备的情况获得不同的资源。
  
  它的作用就是：过一样的ID，根据不同的配置索引到最佳的资源显示在UI中。
```
#### 处理aidl文件生成相应的java文件
```
AIDL （Android Interface Definition Language）， Android接口定义语言，
Android提供的IPC （Inter Process Communication，进程间通信）的一种独特实现。

这个阶段处理.aidl文件，生成对应的Java接口文件
```
#### 编译工程源代码生成class文件
```
通过Java Compiler编译R.java、Java接口文件、Java源文件，生成.class文件。
如果有配置混淆的话，会编译成混淆的class文件，方便源代码不被偷看。
```
#### 转换所有的class文件为dex文件
```
Android系统的Dalvik虚拟机的可执行文件为DEX格式，程序运行所需的class.dex就是在这一步生成的。
```
#### 打包生成Apk文件
```
将classes.dex、resources.arsc、res文件夹、assets文件夹、AndroidManifest.xml打包成apk文件。

res/raw资源被原装不动地打包进APK之外，其它的资源都会被编译或者处理。
```
##### 扩展之res和assets的打包异同点
```
res/raw和assets的相同点:

  1.两者目录下的文件在打包后会原封不动的保存在apk包中，不会被编译成二进制

res/raw和assets的不同点:

  1.res/raw中的文件会被映射到R.java文件中，访问的时候直接使用资源ID即可。
    assets文件夹下的文件不会被映射到R.java中，访问的时候需要AssetManager类。
    
  2.res/raw不可以有目录结构。而assets可以有目录结构。
```
#### 对Apk文件进行签名
```
对apk进行签名，可以进行Debug和Release 签名。

Debug签名是Android Studio默认的，Release 签名是需要我们自己配置的。
```
#### 对签名后的apk文件进行对齐处理
```
Release模式下使用aipalign进行align，即对签名后的apk进行对齐处理。
Zipalign是一个android平台上整理APK文件的工具，它对apk中未压缩的数据进行4字节对齐，
对齐后就可以使用mmap函数读取文件，可以像读取内存一样对普通文件进行操作。
如果没有4字节对齐，就必须显式的读取，这样比较缓慢并且会耗费额外的内存。
```



































































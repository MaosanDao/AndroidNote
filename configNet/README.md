
# 通过WiFi为智能设备配网的工具类

* [DeviceConfigUtil](https://github.com/MaosanDao/AndroidNote/blob/master/configNet/DeviceConfigUtil.kt)
* [ConnectThread](https://github.com/MaosanDao/AndroidNote/blob/master/configNet/ConnectThread.kt)
* [ListenerThread](https://github.com/MaosanDao/AndroidNote/blob/master/configNet/ListenerThread.kt)

### 简介
>作用：通过连接设备端的热点建立起TCP/IP通信，并将各自想要的数据传输给对方，设备端需要的WiFi名字和密码，手机端则需要知道该设备的一些基础信息。
>前提：设备端发送的热点最好不要使用加密，相反直接使用不加密的热点。

### 使用方法
#### 初始化
```kotlin
initWifiConfig()
```
#### 开启连接线程
```kotlin
startConnectDeviceAP()
```
#### 释放资源
```kotlin
releaseConfigResource()
```
#### 监听
>待整理...

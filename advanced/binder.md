# Android Binder原理
***
```
由于参考的问团长的生涩内容过多，所以就摘录一些结构图展示。原文地址列表为：
```
* [Binder学习指南](http://weishu.me/2016/01/12/binder-index-for-newer/)
* [写给 Android 应用工程师的 Binder 原理剖析](https://zhuanlan.zhihu.com/p/35519585) 
***
## Linux下传统的进程间通信的原理
![](https://pic3.zhimg.com/80/v2-38e2ea1d22660b237e17d2a7f298f3d6_hd.jpg)
### 原理
![](https://pic1.zhimg.com/80/v2-aab2affe42958a659ea8a517ffaff5a0_hd.jpg)
## 进程空间划分
![](https://pic2.zhimg.com/80/v2-3c719337413b9c5c4ad0b6c6b8eb0291_hd.jpg)
## Android Binder IPC通信模型
![](https://pic3.zhimg.com/80/v2-729b3444cd784d882215a24067893d0e_hd.jpg)
### 他们的关系就如同：
![](https://pic4.zhimg.com/80/v2-7c68928e26f5b96b8b3471ebb1927107_hd.jpg)
### 对应的Binder通信图示
![](https://pic4.zhimg.com/80/v2-67854cdf14d07a6a4acf9d675354e1ff_hd.jpg)
## Binder通信中的代理模式
![](https://pic2.zhimg.com/80/v2-13361906ecda16e36a3b9cbe3d38cbc1_hd.jpg)

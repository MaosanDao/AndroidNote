# Http基础协议详解([摘](https://www.jianshu.com/p/7573cc22fdb7))

## 基础知识
### IP协议
>网络层协议，保证了计算机之间可以相互发送和接收数据。
### TCP协议
>传输层协议，一种端到端的协议，建立了一个虚拟链路用于发送和接收数据，基于重发机制，提供可靠的通信连接。为了方便通信，将报文分割成东哥报文段发送。
### UDP协议
>传输层协议，一种无连接的协议，每个数据报都是一个独立的信息，包括完成的源地址和目的地址，在网络上以任何可能的路径传往目的地，因此能否到达不知道，正确性和时效性不能被保证。
*** 
## TCP/IP，四层/七层模型
![](http://p3.pstatp.com/large/40390000406ff95d429e)
***
### 过程解析
>服务器（守护进程一直运行）-> 客户端需要服务的时候请求服务器 -> 服务器收到后，建立连接 -> 两个Socket连接后，进行双向传输

## TCP，3次握手，4次挥手过程
### 3次握手
* （第一次）客户端发送请求报文 -> 服务端（**能听到么？**）
* （第二次）服务端回应确认和请求报文 -> 客户端（**我能听到，你能听到嘛？**）
* （第三次）客户端收到请求报文后回复 —> 服务端（**我能听到你，咱们开始连接吧**）
### 图解
![](https://upload-images.jianshu.io/upload_images/9821298-e2a63a7ef8dcb0ad.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/551/format/webp)
![](http://p9.pstatp.com/large/403d000012004c8f42e0)
#### 为什么3次握手
##### 假如不采用三次握手会出现什么情况？
>只要server发出确认后，就会建立一个新的连接。但是client没有发出建立连接的请求，因此不会理会server的确认，也不会向server发送数据。但是server此时已经建立连接了，并且会一直等待client发来消息。这样就会一直耗用资源。
## TCP连接终止协议
### 4次挥手
* （第一次）客户端发送FIN，用来关闭到server的数据连接
* （第二次）server收到FIN后，发回一个ACK到client，确认序号为收到序号+1
* （第三次）server关闭client的连接，发送一个FIN给client
* （第四次）client发送ACK确认，并将ACK序号+1返回给server
![](https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=2093785618,4182910024&fm=173&app=25&f=JPEG?w=590&h=402&s=05B0ED333D1B40CA0E6921D2000050B3)
## DNS的含义
```
DNS（Domain Name System）服务是和HTTP协议一样位于应用层的协议。它提供域名到IP地址之间的解析服务。
计算机既可以被赋予IP地址，也可以被赋予主机名和域名。比如www.baidu.com。
因为域名更加直观，所以用户通常使用主机名或域名来访问对方的计算机，而不是直接通过IP地址访问。
但要让计算机去理解名称，相对而言就变得困难了。因为计算机更擅长处理一长串数字。为了解决上述的问题，DNS服务应运而生。
DNS协议提供通过域名查找IP地址，或逆向从IP地址反查域名的服务。
```
![](http://p3.pstatp.com/large/40380002aab0610a1c07)

# Android Http、Tcp、Https、Socket相关知识点汇总[(摘)](https://www.cnblogs.com/nathan909/p/5355477.html)
## Android通信方式
### Http通信(请求—响应方式)
>http连接使用的是“请求—响应方式”，即在请求时建立连接通道，当客户端向服务器发送请求后，服务器端才能向客户端返回数据。
### Socket通信(TCP/IP 或者 UDP)
* TCP/IP:基于TCP/IP协议的通信则是在双方建立起连接后就可以直接进行数据的传输，在连接时可实现信息的主动推送，而不需要每次由客户端想服务器发送请求.
* UDP:提供无连接的数据报服务，UDP在发送数据报前不需建立连接，不对数据报进行检查即可发送数据包
***
## TCP/IP（主要解决数据在网络中的传输）
>输入传输层/网络层协议。手机能够使用联网功能是因为手机底层实现了TCP/IP协议，可以使手机终端通过无线网络建立TCP连接。TCP协议可以对上层网络提供接口，使上层网络数据的传输建立在“无差别”的网络之上。
## Http（主要解决数据的包装与识别应用）
>即超文本传送协议(Hypertext Transfer Protocol )，属于应用层协议，是Web联网的基础，也是手机联网常用的协议之一，HTTP协议是建立在TCP协议之上的一种应用。HTTP协议详细规定了浏览器与服务器之间相互通信的规则，是万维网交换信息的基础。HTTP是基于“请求-响应”形式并且是短连接，并且是无状态的协议。针对其无状态特性，在实际应用中又需要有状态的形式，因此一般会通过session/cookie技术来解决此问题。
## Scoket（调用接口）
>可对TCP/IP协议进行封装和应用，可视为TCP/IP的编程接口。
***
## 特点分析
### TCP
>只要双方一旦建立建立，那么通信中任何一方关闭连接之前，TCP都会保持下去。
### HTTP连接
>客户端每次请求都需要服务器的返回，在请求结束后，会主动释放连接，从建立连接到关闭连接的过程称为“一次连接”。
### Socket（通信的基石）
>包含五中信息：连接使用的协议，本地主机的IP地址、端口，远程主机的IP地址、端口
#### 连接过程
>服务器监听(1) -> 客户端请求(2) -> 连接确认(3)
1 服务器并不为定位某一个具体的客户端Socket，而是一直处于等待状态
2 客户端提出连接请求，服务端通过拿取到客户端的一些连接信息，开始去连接服务端
3 一旦服务端收到了客户端的连接信息，那么就会正式建立连接。且此时服务端仍在监听状态，继续接受其他客户端的请求
#### TCP/UDP连接上的区别
##### TCP
* 服务端：创建套接字 -> 绑定端口号 -> 监听连接 -> 接受连接请求 -> 用新返回的套接字进行收发操作 -> 关闭套接字
* 客户端：创建套接字 -> 发起建立连接请求 -> 发送接受数据 -> 关闭套接字
##### UDP
* 服务端：创建套接字 -> 绑定端口号 -> 接受发送消息 -> 关闭套接字
* 客户端：创建套接字 -> 发送接受消息 -> 关闭套接字
***
## 具体的Socket通信技术
### 基于TCP协议的Sokcet
#### 服务器端伪代码
```java
//首先声明一个服务端对象：ServerSocket
ServerSocket svr = new ServerSocket(PORT);
//调用accept()方法去接收客户端的数据，这个方法在没有数据的时候，一直处于堵塞状态
Socket s = svr.accept();
//一旦接收到了数据，那么就可以从inputStream中去读取数据
DataInputStream dis = new DataInputStream(s.getInputStream());
DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//Socket 的交互通过流来完成，即是说传送的字节流，因此任何文件都可以在上面传送。谁打开的记得要关上。
```
#### 客户端伪代码
```java
//新建Socket
Socket s = new Socket(IP,PROT);
//输入和输出
DataInputStream dis = new DataInputStream(s.getInputStream());
DataOutputStream dos = new DataOutputStream(s.getOutputStream());
//记得关闭流
```
### 基于HTTP协议的HttpUrlConnection
一般是发送请求到某个应用服务器。
```java
//先取得HttpUrlConnection
HttpURLConnection urlConn = new URL("http://www.google.com").openConnection();

//设置一些标志
urlConn.setDoOutput(true);  urlConn.setDoInput(true);//post的情况下需要设置DoOutput为true
urlConn.setRequestMethod("POST");
//设置是否用缓存
urlConn.setUseCache(false);
//设置content－type
urlConn.setRequestProperty("Content-type","application/x-www-form-urlencoded");

//获得输出流，便于向服务器发送信息
DataOutputStream dos = new DataOutputStream(urlConn.getOutputStream());
//向流里面写入请求参数
dos.writeBytes("name="+URLEncoder.encode("chenmouren","gb2312");
//写完关闭
dos.flush();dos.close();

//获得输入流，取数据
BufferReader reader = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
//用 !=null来判断是否结束
reader.readLine();
reader.close();

//最后记得关闭
urlConn.disconnect();
```
***
## 拓展说明
### 区别
#### TCP和UDP的区别
* TCP有连接，UDP无连接
* TCP面向连接，UDP面向数据
* TCP关心返回数据，UDP一次性，发出即结束
* TCP数据传输可靠（超时重发机制，占用资源多），UDP则相对不可靠（所占资源少）
#### HTTP和HTTPS的区别
* http是需要到CA申请证书，一般免费证书少，需要收费
* http是超文本协议，信息时明文传输，http是则具有安全性的ssl加密协议传输
* http和https是完全不同的连接方式，用的端口也不同，前者80，后面443
* http的连接很简单，是无状态的；https则是有ssl+http协议构建的可进行加密传输、身份认证的网络协议。比http安全
#### 长连接/短连接的区别
* 长连接：在一个TCP连接上可以连续发送多个数据包，在TCP连接保持期间，如果没有数据包的发送，那么需要双方发检测包维持此次连接，一般都是自己做在线维持
* 短连接：通信双方有数据交互的时候，就建立一个TCP连接，数据发送完成后，则断开TCP连接。优点是，管理起来比较简单，存在的连接都是有用的连接，不需要额外的控制手段。
>>>长连接是相对于通常的短连接而说的，也就是长时间保持客户端与服务端的连接状态
#### 长连接和短连接的操作过程
* 短连接：连接 -> 数据传输 -> 关闭连接
* 长连接：连接 -> 数据传输 -> 保持连接(心跳) -> 数据传输 -> 保持连接（心跳）.... ->关闭连接
#### 长连接和短连接的使用时机
* 长连接：用于操作频繁，点对点的通讯，而且连接数不能太多的情况
>>>例如：数据库使用长连接，如果用短连接频繁的通信会造成socket错误，而且频繁的socket 创建也是对资源的浪费

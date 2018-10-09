# 反射的理解和知识点
## 含义
所谓的反射机制就是java语言在运行拥有一项自我观察的能力，通过这一种能力，可以彻底了解自身的情况为下一步的动作做准备
## Java的反射机制实现的借助类
* Class：时类对象
* Constructor:类的构造器对象
* Field:类的属性对象
* Method:类的方法对象
## 反射的作用
在Java运行时的环境中，对于任意一个类，可以知道这个类有哪些属性和方法。对于任意一个对象。可以调用它的任意一个方法
## 反射的使用
### 得到构造器的方法
```java
Constructor getConstuctor(Class[] params); 获得使用特殊的参数类型的公共构造函数	
Constructor[] getConstructors() -- 获得类的所有公共构造函数
Constructor getDeclaredConstructor(Class[] params) -- 获得使用特定参数类型的构造函数(与接入级别无关) 
Constructor[] getDeclaredConstructors() -- 获得类的所有构造函数(与接入级别无关) 
```
### 获得字段信息的方法
```java
Field getField(String name) -- 获得命名的公共字段 
Field[] getFields() -- 获得类的所有公共字段 
Field getDeclaredField(String name) -- 获得类声明的命名的字段(与接入级别无关) 
Field[] getDeclaredFields() -- 获得类声明的所有字段(与接入级别无关)
```
### 获得类中方法信息的方法
```java
Method getMethod(String name, Class[] params) -- 使用特定的参数类型，获得命名的公共方法 
Method[] getMethods() -- 获得类的所有公共方法 
Method getDeclaredMethod(String name, Class[] params) -- 使用特写的参数类型，获得类声明的命名的方法(与接入级别无关) 
Method[] getDeclaredMethods() -- 获得类声明的所有方法(与接入级别无关) 
```
## 总结
* 在程序开发中使用反射并结合属性文件，可以达到程序代码与配置文件相分离的目的
* 如果我们想要得到对象的信息:
  一般需要“引入需要的‘包.类’的名称 ——> 通过new实例化 ——> 取得实例化对象”这样的过程。
  使用反射就可以变成“实例化对象 ——> getClass()方法 ——> 得到完整的‘包.类’名称”这样的过程
* **正常方法是通过一个类创建对象，反射方法就是通过一个对象找到一个类的信息**


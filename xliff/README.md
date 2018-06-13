# Xliff的介绍和使用

## 介绍
>>XLIFF外文全名是XML Localization Interchange File Format，中文名是XML本地化数据交换格式。

## 标签使用
```java
%n$ms：输出的是字符串，n代表是第几个参数，设置m的值可以在输出之前放置空格
%n$md：输出的是整数，n代表是第几个参数，设置m的值可以在输出之前放置空格，也可以设为0m,在输出之前放置m个0
%n$mf：输出的是浮点数，n代表是第几个参数，设置m的值可以控制小数位数，如m=2.2时，输出格式为00.00
```
## 具体使用例子
### 声明
```Java
<resources xmlns:xliff="urn:oasis:names:tc:xliff:document:1.2">
    <string name="welcome">
            欢迎 <xliff:g id="name">%1$s</xliff:g>, 排名 <xliff:g id="num">%2$d</xliff:g>
    </string>
</resources>
```
### 使用
```java
public final String getString (int resId, Object... formatArgs);
String s = getString(R.string.welcome, "abc", 123);
```
### 输出结果：欢迎 abc, 排名 123

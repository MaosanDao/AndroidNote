# View相关知识
## View的绘制流程
* measure：判断是否需要重新计算view的大小，需要则重新计算
* layout：判断是否需要重新计算view的位置，需要则重新计算
* draw：判断是否需要重新绘制该view，需要则重新计算
## 自定义属性的使用方法
### 首先在style或者attr文件中定义属性：
```java
<declare-styleable name="TestView"> 
    <attr name="attrone" format="dimension"/> 
    <attr name="attrtwo" format="string" > 
        <enum name="one" value="0"/> 
        <enum name="two" value="1"/>
     </attr> 
</declare-styleable>
```
#### 其中attr为属性的单位，而format为属性的类型
### 然后在view的自定义类中进行使用：
```Java
TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.TestView); 
float attrone = ta.getDimension(R.styleable.TestView_attrone,0); 
String attrTwo = ta.getString(R.styleable.TestView_attrtwo);

//使用完毕后，需要回收：
ta.recycle();
```
### 补充
```java
<attr name="textStyle"> 
    <flag name="normal" value="0" /> 
    <flag name="bold" value="1" /> 
    <flag name="italic" value="2" /> 
</attr>
```
其中，enum和flag的区别在于，enum只能选择其一，而flag则可以累加选择。比如：bold|italic表示既加粗也变成斜体

## 相关参考文章：
* [HenCoder自定义View讲解](https://hencoder.com/ui-1-1/)

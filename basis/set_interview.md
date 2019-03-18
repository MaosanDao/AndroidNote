# Java集合相关的面试题整理
***
## 目录
* [Java中集合分类](#java中集合分类)
* [知识点及一些面试题](#知识点)
  * [ArrayList和Vector的区别](#arraylist和vector的区别)
  * [HashMap和Hashtable的区别](#hashmap和hashtable的区别)
  * [List和Map的区别](#list和map的区别)
  * [Set如何判断元素是否重复](#set如何判断元素是否重复)
  * [Collection和Collections的区别](#collection和collections的区别)
  * [ArrayList和LinkedList的存储性能和特性](#arraylist和linkedlist的存储性能和特性)
    * [插入首尾或者中间，ArrayList比较快](#扩展)
  * [ListIterator有什么特点](#listiterator有什么特点)
  * [如果HashMap的key为类对象则该类需要满足什么条件](#如果hashmap的key为类对象则该类需要满足什么条件)
***
## Java中集合分类
### Collection
```
//重要
HashSet
TreeSet
LinkedHashSet
//重要
ArrayList
LinkedList
Vector(了解，已过时)
List
Set
```
### Map
```
LinkedHashMap
//重要
HashMap
TreeMap
ConcurrentHashMap
Hashtable(了解，，已过时)
```
## 知识点
### ArrayList和Vector的区别
```
共同点：
  
  1.这两个类都实现了List接口，它们都是有序的集合(存储有序)，底层是数组
  2.可以按位置索引号取出某个元素，允许元素重复和为null
  
同步性：
  
  1.ArrayList是非同步的
  2.Vector是同步的
  3.即便需要同步的时候，我们可以使用Collections工具类来构建出同步的ArrayList而不用Vector
```
### HashMap和Hashtable的区别
```
共同点：
  从存储结构和实现来讲基本上都是相同的，都是实现Map接口
  
同步性：
  
  1.HashMap是非同步的
  2.Hashtable是同步的
  3.需要同步的时候，我们往往不使用，而使用ConcurrentHashMap

是否允许为Null:
  
  1.HashMap允许为null
  2.Hashtable不允许为null
  
Contains方法：

  1.HashMap把Hashtable的contains方法去掉了，改成了containsValue和containsKey
  2.Hashtable有contains方法
```
### List和Map的区别
```
存储结构不同：
  
  1.List是存储单列的集合
  2.Map存储的是key-value键值对的集合
  
元素是否可以重复：

  1.List允许元素重复
  2.Map不允许Key重复
  
是否有序：

  1.List集合是有序的(存储有序)
  2.Map集合是无序的(存储无序)
```
### Set如何判断元素是否重复
```java
// 1. 如果key 相等  
if (p.hash == hash &&
    ((k = p.key) == key || (key != null && key.equals(k))))
    e = p;	// 2. 修改对应的value
if (e != null) { // existing mapping for key
      V oldValue = e.value;            if (!onlyIfAbsent || oldValue == null)
          e.value = value;
      afterNodeAccess(e);            return oldValue;
 }
   
//从上述可见，是通过找到同样的key，而去修改value。
```
### Collection和Collections的区别
```
Collection是集合的上级接口，继承它的有Set和List接口

Collections是集合的工具类，提供了一系列的静态方法对集合的搜索、查找、同步等操作
```
### ArrayList和LinkedList的存储性能和特性
```
ArrayList的底层是数组，LinkedList的底层是双向链表。

ArrayList它支持以角标位置进行索引出对应的元素(随机访问)，而LinkedList则需要遍历整个链表来获取对应的元素。
一般来说ArrayList的访问速度是要比LinkedList要快的。
  
ArrayList由于是数组，对于删除和修改而言消耗是比较大(复制和移动数组实现)。
LinkedList是双向链表删除和修改只需要修改对应的指针即可，消耗是很小的。

一般来说LinkedList的增删速度是要比ArrayList要快的。
```
#### 扩展
```
ArrayList的增删未必就是比LinkedList要慢：

  1.如果增删都是在末尾来操作【每次调用的都是remove()和add()】，此时ArrayList就不需要移动和复制数组来进行操作了
  2.如果删除操作的位置是在中间。由于LinkedList的消耗主要是在遍历上，ArrayList的消耗主要是在移动和复制上
    (底层调用的是arraycopy()方法，是native方法)。
    
中间和两端。
```
### ListIterator有什么特点
```
ListIterator继承了Iterator接口，它用于遍历List集合的元素

ListIterator可以实现双向遍历，添加元素，设置元素。
```
### 如果HashMap的key为类对象则该类需要满足什么条件
```
需要同时重写该类的hashCode()方法和它的equals()方法：
  
  1.插入元素的时候是先算出该对象的hashCode。如果hashcode相等话的。那么表明该对象是存储在同一个位置上的。
  2.如果调用equals()方法，两个key相同，则替换元素
  3.如果调用equals()方法，两个key不相同，则说明该hashCode仅仅是碰巧相同。
  
因为equals()认定了这两个对象相同，而同一个对象调用hashCode()方法时，是应该返回相同的值的！
```
### ArrayList集合加入1万条数据应该怎么提高效率
```
ArrayList的默认初始容量为10，要插入大量数据的时候需要不断扩容，而扩容是非常影响性能的。

直接在初始化的时候就设置ArrayList的容量可以提高效率。
```








































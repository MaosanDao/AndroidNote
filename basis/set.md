# Java集合相关知识点(整理并摘录至[原文](https://blog.csdn.net/axela30w/article/details/78395687))
***
## 目录
* [泛型安全的容器](#泛型安全的容器) 
    * [泛型不安全的写法](#泛型不安全的写法) 
    * [泛型安全的写法](#泛型安全的写法) 
* [Collection的常用方法](#collection) 
* [Iterator遍历](#iterator遍历) 
* [ListIterator遍历](#listIterator遍历) 
* [ForEach遍历](#foreach遍历) 
* [List相关](#list) 
    * [ArrayList和LinkedList的对比](#arraylist和linkedlist的对比) 
    * [ArrayList](#arraylist) 
    * [LinkedList](#linkedlist) 
* [Stack相关](#stack) 
* [Set相关](#set) 
    * [散列之HashSet](#散列之hashset) 
    * [结果排序之TreeSet](#结果排序之treeset) 
    * [set的常用方法](#常用方法) 
* [Map相关](#Map) 
    * [Hashmap](#hashmap)
    * [Hashtable](#hashtable)
    * [LinkedHashMap](#linkedhashmap)
    * [TreeMap](#treemap)
    * [Map的遍历](#Map的遍历)
* [先进先出Queue](#先进先出queue)
* [总结](#总结)
***
## 泛型安全的容器
### 泛型不安全的写法
```java
class Apple{
    public String color;
}

class Orange{
    public int id;
}

ArrarList apples = new ArrayList();

for(int i = 0;i < 3;i++){
    apples.add(new Apple());
    apples.add(new Orange());
}

for(int i = 0;i < apples.size();i++){
    Apple a = (Apple)apples.get(i);//Orange只有在遍历到的时候才会检测到
}

```
```
如上代码所示，因为在Java中如果没有显式的声明继承哪一个类，那么就会默认继承Object类。所以：

  1.因为Apple和Orange是继承于Object类，所以，apples数组，则都可以添加他们。
  2.当在使用ArrayList的get()方法来读取对象时，得到的只是Object引用，必须将其强制转型为Apple。
  3.当读取到ArrayList中的Orange对象时，试图将Orange转型为Apple就会报错。这样就很不安全了。
```
### 泛型安全的写法Collection
```java
ArrarList<Apple> apples = new ArrayList<Apple>();

for(int i = 0;i < 3;i++){
    apples.add(new Apple());
}

//apples.add(new Orange());

for(int i = 0;i < apples.size();i++){
    Apple a = apples.get(i);
}

for(Apple c : apples){
    System.out.print(c.color);
}
//以上的写法，则可以明确指示List是什么类型的了，不会法伤上述的情况
```
### 添加一组元素
```java
//1.使用Array.asList方法，最后方法输出为一个List对象
   Collection<Integer> collection =
      new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5));
    
//2.使用Collections.addAll()方法
    Integer[] moreInts = { 6, 7, 8, 9, 10 };
    collection.addAll(Arrays.asList(moreInts));//成员方法只能接受另外一个Collection对象作为参数
    Collections.addAll(collection, 11, 12, 13, 14, 15);//可以是一个Collection对象，或者数组，或者逗号分隔的列表
    Collections.addAll(collection, moreInts);
    List<Integer> list = Arrays.asList(16, 17, 18, 19, 20);//asList的参数可以是以逗号隔开的列表
    list.set(1, 99);
```
***
## Collection和Iteator以及ListIterator和ForEach
### Collection
```java
public interface Collection<E> extends Iterable<E> {
  //将指定的对象从集合中移除，移除成功返回true,不成功返回false
  boolean remove(Object o);
  //将指定对象添加到集合中
  boolean add(E e);
  //查看该集合中是否包含指定的对象，包含返回true,不包含返回flase
  boolean contains(Object o);
  //返回集合中存放的对象的个数。返回值为int
  int size();
  //移除该集合中的所有对象，清空该集合
  void clear();
  //返回一个包含所有对象的iterator对象，用来循环遍历
  Iterator<E> iterator();
  //返回一个包含所有对象的数组,类型是Object
  Object[] toArray();
  //返回一个包含所有对象的指定类型的数组
   <T> T[] toArray(T[] a);
}
```
### Iterator遍历
```
迭代器是一个对象，它的工作是遍历并选择序列中的对象。Iterator只能向前移动:

  1.使用iterator()方法要求容器返回一个Iterator。
  2.使用next()获得序列中的下一个元素。
  3.使用hasNext()检查序列中是否还有元素。
  4.使用remove()将迭代器新近返回的元素删除。
```
```java
//示例
Iterator it = collection.iterator(); //获取迭代器
while(it.hasNext()) {//是否有元素
 Object obj = it.next(); //获取的元素
}
```
### ListIterator遍历
```
ListIterator是一个更加强大的Iterator子类型，它只能用于各种List类的访问:
  
  1.ListIterator可以双向移动
  2.可以产生相对于迭代器在列表中指向当前位置的前一个和后一个元素的索引
  3.可以使用set()方法替换它访问过的最后一个元素
  4.可以通过调用listIterator(n)方法创建一个一开始就指向列表索引为n的元素处的ListIterator
```
```java
//代码实例

//数组元素为：[Rat, Manx, Cymric, Cymric, Rat, EgyptianMau, Hamster, EgyptianMau]
List<Pet> pets = Pets.arrayList(8);
ListIterator<Pet> it = pets.listIterator();

//这里展示指向前一个和后一个元素的索引
while(it.hasNext()){
 System.out.print(it.next() + ", " + it.nextIndex() +
   ", " + it.previousIndex() + "; ");
    //输出
    //Rat, 1, 0; Manx, 2, 1; Cymric, 3, 2; Mutt, 4, 3; Pug, 5, 4; Cymric, 6, 5; Pug, 7, 6; Manx, 8, 7;
}

//倒序遍历
while(it.hasPrevious()){
 System.out.print(it.previous().id() + " ");//7 6 5 4 3 2 1 0
}
System.out.println(pets);//[Rat, Manx, Cymric, Mutt, Pug, Cymric, Pug, Manx]

//从固定位置遍历
it = pets.listIterator(3);//从索引3开始
while(it.hasNext()) {
 it.next();
 it.set(Pets.randomPet());//替换在列表中从位置3开始向前的所有元素
}

//输出全部元素
System.out.println(pets);//[Rat, Manx, Cymric, Cymric, Rat, EgyptianMau, Hamster, EgyptianMau]
```
### ForEach遍历
```java
//用于遍历数组，也可以遍历Collection对象。但是不包括Map：
Collection<String> cs = new LinkedList<String>();
Collection.addALL(cs,"Take the long way home".split(" "));
for(String s : cs){
    System.out.print("'" + s + "' ");
}
```
### for循环
```java
for（int i=0;i<collection.size();i++）{
  //...
}
```
## List
```
特点：

    1.List里存放的对象是有序的，按元素的插入顺序设置元素的索引
    2.元素可以重复
    
缺点：
    因为往list集合里插入或删除数据时，会伴随着后面数据的移动，所以插入删除数据速度慢。 
    
分类：
    1.ArrayList
        随机访问元素速度快，但是插入和移除元素速度慢。
    2.LinkedList
        随机访问、查询元素速度慢，增删元素速度快。
```
### ArrayList和LinkedList的对比
```
1.ArrayList是实现了基于动态数组的数据结构，LinkedList基于链表的数据结构。
2.对于随机访问get和set，ArrayList优于LinkedList，因为LinkedList要移动指针。
3.对于新增和删除操作add和remove，LinkedList比较占优势，因为ArrayList要移动数据。（操作量很大的话）
    3.1 若只对单条数据插入或删除，ArrayList的速度反而优于LinkedList。
    3.2 若是批量随机的插入删除数据，LinkedList的速度大大优于ArrayList。
        因为ArrayList每插入一条数据，要移动插入点及之后的所有数据。
```
### ArrayList
```java
List<String> list = new ArrayList<String>();

list.add("啊啊啊!");
list.add("别别别!");
list.add("吃吃吃!");
list.add("对对对");
list.add("嗯嗯嗯");
System.out.println(list.size());//5 -- 长度
System.out.println(list.contains("abcde"));//false -- 是否包含
System.out.println(list.remove("对对对")); --- 移除元素
System.out.println(list.size());//4 --- 移除后长度
```
### LinkedList
```java
LinkedList<Pet> pets = new LinkedList<Pet>(Pets.arrayList(5));

print(pets);//[Rat, Manx, Cymric, Mutt, Pug]

//getFirst()与element()一样，返回列表的头，并不移除它,如果列表为空，则抛出异常
print(pets.getFirst());//Rat 
print(pets.element());//Rat

//peek()方法与这两个方法唯一的差异是列表为空时返回null。
print(pets.peek());//Rat

//removeFirst()与remove()是一样的，移除并返回列表的头。如果为空，则抛出异常
print(pets.remove());//Rat
print(pets.removeFirst());//Manx

//poll()方法与这两个方法唯一的差异是在列表为空时返回null
print(pets.poll());//Cymric
print(pets);//[Mutt, Pug]

//addFirst()与add()与addLast()相同，将某个元素插入到列表的尾部。 
pets.addFirst(new Rat());
print(pets);//[Rat, Mutt, Pug]
pets.offer(Pets.randomPet());//将一个元素插入到队尾
print(pets);//[Rat, Mutt, Pug, Cymric]
pets.add(Pets.randomPet());
print(pets);//[Rat, Mutt, Pug, Cymric, Pug]
pets.addLast(new Hamster());
print(pets);//[Rat, Mutt, Pug, Cymric, Pug, Hamster]

//removeLast()移除并返回列表的最后一个元素
print(pets.removeLast());//Hamster
```
### Stack
```
栈”通常是指“后进先出LIFO”的容器。最后“压入”栈的元素，最先“弹出”栈。（Last In First Out）
LinkedList具有能够直接实现栈的所有功能的方法，可以直接将LinkedList作为栈使用。 
```
```java
//自己写一个栈
public class Stack<T> {
  private LinkedList<T> storage = new LinkedList<T>();
  
  public void push(T v) { storage.addFirst(v); }//把元素插入尾部
  public T peek() { return storage.getFirst(); }//返回第一个元素，不移除它
  public T pop() { return storage.removeFirst(); }//移除并返回列表的第一个元素
  public boolean empty() { return storage.isEmpty(); }
  public String toString() { return storage.toString(); }
}

//测试示例代码
public class StackTest {
    public static void main(String [] args){
        Stack<String> stack = new Stack<String>();
        for(String s : "我 在 这 里".split(" ")){
            stack.push(s);
            System.out.println(stack.toString());
        }
        while (!stack.empty()){
                System.out.print(stack.pop() + "---");//后进先出
        }
        System.out.println();
        System.out.println(stack.toString());
    }
}

//输出结果
[我]
[在, 我]
[这, 在, 我]
[里, 这, 在, 我]
里---这---在---我---
[]
```
## Set
```
Set不保存重复的元素，Set最常被使用的是测试归属性，可以很容易的询问某个对象是否在某个Set中。
查找就成了Set最重要的操作，通常会选择一个HashSet的实现，它专门对快速查找进行的优化。
```
### 散列之HashSet
```
public class SetOfInteger {
    public static void main(String[] args){
        Random rand = new Random(47);
        Set<Integer> intset = new HashSet<Integer>();
        for (int i = 0;i < 10000;i++){
            intset.add(rand.nextInt(30));
        }
        System.out.println(intset);
    }
}

//输出结果:
//[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 16, 19, 18, 21, 20, 23, 22, 25, 24, 27, 26, 29, 28]

//由输出可以看出：
    //1.每一个数字只有一个实例出现在结果中（不保存重复的元素），输出的顺序也是没有规律的（散列）
```
### 结果排序之TreeSet
```java
public class TreeSetOfInteger {
    public static void main(String[] arge){
        Random rand = new Random(47);
        SortedSet<Integer> intset = new TreeSet<Integer>();//SortedSet升序
        for (int i = 0;i < 10000;i++){
            intset.add(rand.nextInt(30));
        }
        System.out.println(intset);
    }
}
//输出结果
[0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29]
```
### 常用方法
```java
//最常见的操作就是用contains()测试Set的归属性
public class SetOperations {
  public static void main(String[] args) {
    Set<String> set1 = new HashSet<String>();
    Collections.addAll(set1,"A B C D E F G H I J K L".split(" "));//将列表添加到set1中
    
    set1.add("M");
    print(set1.contains("H"));//true
    print(set1.contains("N"));//false
    
    Set<String> set2 = new HashSet<String>();
    Collections.addAll(set2, "H I J K L".split(" "));
    print(set1.containsAll(set2));//true
    
    set1.remove("H");
    print(set1);//[D, K, C, B, L, G, I, M, A, F, J, E]
    print(set1.containsAll(set2));//false
    
    set1.removeAll(set2);
    print(set1);//[D, C, B, G, M, A, F, E]
    Collections.addAll(set1, "X Y Z".split(" "));
    print(set1);//[Z, D, C, B, G, M, A, F, Y, X, E]
  }
} 
```
***
## Map
```
Map集合中存储的是键值对，键不能重复，值可以重复，根据键得到值。
对map集合遍历时先得到键的set集合，对set集合进行遍历，得到相应的值。

常用方法：

    put(K key, V value) 向集合中添加指定的键值对 
    putAll (Map< ? extends K,? extends V>t)把一个Map中的所有键值对添加到该集合 
    containsKey(Object key) 如果包含该键，则返回true 
    containsValue(Object value) 如果包含该值，则返回true 
    get(Object key) 根据键,返回相应的值对象 
    keySet() 将该集合中的所有键以Set集合形式返回 
    values() 将该集合中所有的值以Collection形式返回 
    remove(Object key) 如果存在指定的键，则移除该键值对，返回键所对应的值，如果不存在则返回null 
    clear() 移除Map中的所有键值对，或者说就是清空集合 
    isEmpty() 查看Map中是否存在键值对 
    size()查看集合中包含键值对的个数，返回int类型
```
### HashMap
```
最常用的Map，它根据键的HashCode值存储数据，根据键可以直接获取它的值，具有很快的访问速度。

    1.遍历时，取得数据的顺序是完全随机的。
    2.因为键对象不可以重复，所以HashMap最多只允许一条记录的键为Null，允许多条记录的值为Null。

在Map 中插入、删除和定位元素，HashMap是最好的选择。
```
### Hashtable
```
注意Hashtable中的t是小写的，它是HashMap的线程安全版本，现在已经很少使用。
它支持线程的同步，即任一时刻只有一个线程能写Hashtable，因此也导致了Hashtale在写入时会比较慢，
它继承自Dictionary类，不同的是它不允许记录的键或者值为null，同时效率较低。 
```
### LinkedHashMap
```
保存了记录的插入顺序，在用Iteraor遍历LinkedHashMap时，先得到的记录肯定是先插入的，
在遍历的时候会比HashMap慢，有HashMap的全部特性。
```
### TreeMap
```
能够把它保存的记录根据键排序，默认是按键值的升序排序（自然顺序），也可以指定排序的比较器，
当用Iterator遍历TreeMap时，得到的记录是排过序的。不允许key值为空，非同步的。
```
### Map的遍历
```java
//第一种：将Map中所有的键存入到set集合中。用迭代方式取出所有的键，再根据get方法。获取每一个键对应的值
Iterator it = map.keySet().iterator();//使用keySet()取出Key
while(it.hasNext()){
    Object key = it.next();
    System.out.println(map.get(key));
}

//第二种：键和值一起遍历
Iterator it = map.entrySet().iterator();//使用entrySet()则可以取出键值
while(it.hasNext()){
    Entry e =(Entry) it.next();
    System.out.println("键"+e.getKey () + "的值为" + e.getValue());
}

//以上两种，推荐第二种
//因为第一种，相当于遍历了2次，而第二种则只进行了一次
```
***
## 先进先出Queue
```
队列是一个典型的“先进先出FIFO”的容器，区分于Stack栈的“后进先出LIFO”。

可以用到LinkedList，LinkedList提供了方法以支持队列的行为，并且实现了Queue接口。
LinkedList可以用做Queue的一种实现。通过LinkedList向上转型为Queue。
```
```java
public static void printQ(Queue queue) {
    //peek不移除元素，返回队头
    while(queue.peek() != null){
      System.out.print(queue.remove() + " ");
    }
    System.out.println();
}
  
public static void main(String[] args) {
    //先进先出
    Queue<Character> qc = new LinkedList<Character>();
    for(char c : "Brontosaurus".toCharArray()){
      qc.offer(c);//插入到队尾，但是先出
    }
    printQ(qc);//B r o n t o s a u r u s
}
```
***
## 总结
```
1.Collection保存单一的元素，Map保存相关联的键值对，通过泛型，可以指定容器中存放的类型，
  这样获取元素时才不必进行类型转换，否则，获取的元素则为Object对象，需要进行强制类型转换。
  
2.List像数组一样建立数字索引与对象的关联，List是有序的，自动扩充尺寸。

3.如果需要进行大量的随机访问，用ArrayList；如果经常插入或删除元素，用LinkedList。

4.LinkedList提供了方法以支持队列的行为，并且实现了Queue接口。各种Queue和栈的行为，由LinkedList提供支持。(先进先出)

5.Map是将对象与对象关联起来的键值对（key-value）设计。
    5.1 HashMap用来快速访问
    5.2 TreeMap按照比较结果升序保存键，保持键始终处于排序状态
    5.3 LinkedHashMap保持元素插入的顺序，但是通过散列提供了快速访问能力
    
6.Set不会保存重复的元素
    6.1 HashSet提供最快的查询速度
    6.2 TreeSet保持元素处于排序状态
    6.3 LinkedHashSet以插入顺序保存元素

```
### 常用集合是否有序，元素是否重复
![Image](https://img-blog.csdn.net/20171031153903945?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQvQXhlbGEzMFc=/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/SouthEast)
### 最后总结
```
1.一共只有四种容器：Map、List、Set、Queue，它们各有2-3个实现版本。（LMSQ）

2.用的容器类四个：ArrayList、LinkedList、HashMap、HashSet。

3.任意的Collection都可以生成Iterator，List可以生成ListIterator（也能生成Iterator，因为List继承自Collection）。
```









































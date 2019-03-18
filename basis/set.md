# Java集合相关知识点
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
### 泛型安全的写法：
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
   ", " + it.previousIndex() + "; ");//Rat, 1, 0; Manx, 2, 1; Cymric, 3, 2; Mutt, 4, 3; Pug, 5, 4; Cymric, 6, 5; Pug, 7, 6; Manx, 8, 7;
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











































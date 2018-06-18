# 泛型相关知识

## 泛型定义
泛型，即参数化类型
泛型的本质是为了参数化类型（在不创建新的类型的情况下，通过泛型指定的不同类型来控制形参具体限制的类型）。也就是说在泛型使用过程中，操作的数据类型被指定为一个参数，这种参数类型可以用在类、接口和方法中，分别被称为泛型类、泛型接口、泛型方法。
## 特性
泛型在逻辑上可以看成不同的类型，其实实际都是一种基本类型

## 泛型的使用
### 泛型类
```java
public class Generic<T>{ 
//key这个成员变量的类型为T,T的类型由外部指定  
 private T key;

public Generic(T key) { //泛型构造方法形参key的类型也为T，T的类型由外部指定
	 this.key = key;
 }

public T getKey(){ //泛型方法getKey的返回值类型为T，T的类型由外部指定
	 return key;
}
}

//泛型的类型参数只能是类类型（包括自定义类），不能是简单类型
//传入的实参类型需与泛型的类型参数类型相同，即为Integer.
Generic<Integer> genericInteger = new Generic<Integer>(123456);

//也可以这样使用
Generic generic3 = new Generic(false);
```
### 泛型接口
```java
//定义一个泛型接口
public interface Generator<T> {
			public T next();
}

/**
* 未传入泛型实参时，与泛型类的定义相同，在声明类的时候，需将泛型的声明也一起加到类中
* 即：class FruitGenerator<T> implements Generator<T>
* 如果不声明泛型，如：class FruitGenerator implements Generator<T>，编译器会报错："Unknown class"
*/
class FruitGenerator<T> implements Generator<T>{
			@Override
			public T next() {
			    return null;
		 }
}

/**
* 传入泛型实参时：
* 定义一个生产器实现这个接口,虽然我们只创建了一个泛型接口Generator<T>
* 但是我们可以为T传入无数个实参，形成无数种类型的Generator接口。
* 在实现类实现泛型接口时，如已将泛型类型传入实参类型，则所有使用泛型的地方都要替换成			传入的实参类型
* 即：Generator<T>，public T next();中的的T都要替换成传入的String类型。
*/
```
### 泛型方法
```java
public <T> T genericMethod(Class<T> tClass)throws InstantiationException ,
  			IllegalAccessException{
        				T instance = tClass.newInstance();
        				return instance;
}
```
* public 与 返回值中间<T>非常重要，可以理解为声明此方法为泛型方法
* 只有声明了<T>的方法才是泛型方法，泛型类中的使用了泛型的成员方法并不是泛型方法
* <T>表明该方法将使用泛型类型T，此时才可以在方法中使用泛型类型T
* 与泛型类的定义一样，此处T可以随便写为任意标识，常见的如T、E、K、V等形式的参数常用于表示泛型
  
## 具体示例
```Java
public class GenericTest {
   //这个类是个泛型类，在上面已经介绍过
   public class Generic<T>{     
        private T key;

        public Generic(T key) {
            this.key = key;
        }

        //我想说的其实是这个，虽然在方法中使用了泛型，但是这并不是一个泛型方法。
        //这只是类中一个普通的成员方法，只不过他的返回值是在声明泛型类已经声明过的泛型。
        //所以在这个方法中才可以继续使用 T 这个泛型。
        public T getKey(){
            return key;
        }

        /**
         * 这个方法显然是有问题的，在编译器会给我们提示这样的错误信息"cannot reslove symbol E"
         * 因为在类的声明中并未声明泛型E，所以在使用E做形参和返回值类型时，编译器会无法识别。
        public E setKey(E key){
             this.key = keu
        }
        */
    }

    /** 
     * 这才是一个真正的泛型方法。
     * 首先在public与返回值之间的<T>必不可少，这表明这是一个泛型方法，并且声明了一个泛型T
     * 这个T可以出现在这个泛型方法的任意位置.
     * 泛型的数量也可以为任意多个 
     *    如：public <T,K> K showKeyName(Generic<T> container){
     *        ...
     *        }
     */
    public <T> T showKeyName(Generic<T> container){
        System.out.println("container key :" + container.getKey());
        //当然这个例子举的不太合适，只是为了说明泛型方法的特性。
        T test = container.getKey();
        return test;
    }

    //这也不是一个泛型方法，这就是一个普通的方法，只是使用了Generic<Number>这个泛型类做形参而已。
    public void showKeyValue1(Generic<Number> obj){
        Log.d("泛型测试","key value is " + obj.getKey());
    }

    //这也不是一个泛型方法，这也是一个普通的方法，只不过使用了泛型通配符?
    //同时这也印证了泛型通配符章节所描述的，?是一种类型实参，可以看做为Number等所有类的父类
    public void showKeyValue2(Generic<?> obj){
        Log.d("泛型测试","key value is " + obj.getKey());
    }

     /**
     * 这个方法是有问题的，编译器会为我们提示错误信息："UnKnown class 'E' "
     * 虽然我们声明了<T>,也表明了这是一个可以处理泛型的类型的泛型方法。
     * 但是只声明了泛型类型T，并未声明泛型类型E，因此编译器并不知道该如何处理E这个类型。
    public <T> T showKeyName(Generic<E> container){
        ...
    }  
    */

    /**
     * 这个方法也是有问题的，编译器会为我们提示错误信息："UnKnown class 'T' "
     * 对于编译器来说T这个类型并未项目中声明过，因此编译也不知道该如何编译这个类。
     * 所以这也不是一个正确的泛型方法声明。
    public void showkey(T genericObj){

    }
    */

    public static void main(String[] args) {


    }
}
```
## 具体示例2
```Java
public class GenericFruit {
    class Fruit{
        @Override
        public String toString() {
            return "fruit";
        }
    }

    class Apple extends Fruit{
        @Override
        public String toString() {
            return "apple";
        }
    }

    class Person{
        @Override
        public String toString() {
            return "Person";
        }
    }

    class GenerateTest<T>{
        public void show_1(T t){
            System.out.println(t.toString());
        }

        //在泛型类中声明了一个泛型方法，使用泛型E，这种泛型E可以为任意类型。可以类型与T相同，也可以不同。
        //由于泛型方法在声明的时候会声明泛型<E>，因此即使在泛型类中并未声明泛型，编译器也能够正确识别泛型方法中识别的泛型。
        public <E> void show_3(E t){
            System.out.println(t.toString());
        }

        //在泛型类中声明了一个泛型方法，使用泛型T，注意这个T是一种全新的类型，可以与泛型类中声明的T不是同一种类型。
        public <T> void show_2(T t){
            System.out.println(t.toString());
        }
    }

    public static void main(String[] args) {
        Apple apple = new Apple();
        Person person = new Person();

        GenerateTest<Fruit> generateTest = new GenerateTest<Fruit>();
        //apple是Fruit的子类，所以这里可以
        generateTest.show_1(apple);
        //编译器会报错，因为泛型类型实参指定的是Fruit，而传入的实参类是Person
        //generateTest.show_1(person);

        //使用这两个方法都可以成功
        generateTest.show_2(apple);
        generateTest.show_2(person);

        //使用这两个方法也都可以成功
        generateTest.show_3(apple);
        generateTest.show_3(person);
    }

```
## 可变参数和泛型
```Java
public <T> void printMsg( T... args){
    for(T t : args){
        Log.d("泛型测试","t is " + t);
    }
}
```
## 静态方法和泛型
如果一个静态方法需要使用泛型作为参数，则需要将这个静态方法设置为泛型方法，即，添加<T>
  
```Java
public class StaticGenerator<T> {
    ....
    ....
    /**
     * 如果在类中定义使用泛型的静态方法，需要添加额外的泛型声明（将这个方法定义成泛型方法）
     * 即使静态方法要使用泛型类中已经声明过的泛型也不可以。
     * 如：public static void show(T t){..},此时编译器会提示错误信息：
          "StaticGenerator cannot be refrenced from static context"
     */
    public static <T> void show(T t){

    }
}
```

## 泛型的上下边界
### 含义
在使用泛型的时候，我们还可以为传入的泛型类型实参进行上下边界的限制，如：类型实参只准传入某种类型的父类或某种类型的子类
### 上边界 — 传入的参数类型实参必须为指定类型的子类
#### 泛型类的上边界
```java
public void showKeyValue1(Generic<? extends Number> obj){
   		 Log.d("泛型测试","key value is " + obj.getKey());
	}
```
#### 泛型方法的上边界
```java
//在泛型方法中添加上下边界限制的时候，必须在权限声明与返回值之间的<T>上添加上下边界，即在泛型	声明的时候添加
//public <T> T showKeyName(Generic<T extends Number> container)，编译器会报错："Unexpected 	bound"
public <T extends Number> T showKeyName(Generic<T> container){
    		System.out.println("container key :" + container.getKey());
   		 T test = container.getKey();
   		 return test;
}
```
### 下边界 — 传入的参数类型必须为指定类型的父类
#### 泛型类的下边界
```java
public void showKeyValue1(Generic<? super Number> obj){
   		 Log.d("泛型测试","key value is " + obj.getKey());
}
```
#### 泛型方法的下边界
```java
//在泛型方法中添加上下边界限制的时候，必须在权限声明与返回值之间的<T>上添加上下边界，即在泛型	声明的时候添加
//public <T> T showKeyName(Generic<T super Number> container)，编译器会报错："Unexpected 		bound"
public <T super Number> T showKeyName(Generic<T> container){
    		System.out.println("container key :" + container.getKey());
   		 T test = container.getKey();
   		 return test;
}
```
## 注意：T 和 ？之间的区别
### T
当我们定义泛型的时候用
```java
SomeName<T>
```
泛型也叫参数化类型，意味着我们在使用泛型的时候要给它参数
  
### ?
当对已经存在的泛型，我们不想给她一个具体的类型做为类型参数，我们可以给她一个不确定的类型作为参数，（前提是这个泛型必须已经定义）
```java
SomeName<?>
```
### 加以限制
```java
SomeName<? super B>
```
### 一个用在定义的时候（不能用?必须给个名字，比如T等，否则定义的代码里怎么用呢？）
### 一个是在使用的时候。


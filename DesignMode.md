# Android中常用的设计模式简易使用指南
## 主要内容
* [建造者模式](#建造者模式)
* [单例模式](#单例模式)
***
## 建造者模式
### 引出问题
>假如一个对象由许多不同的属性构造，我们想要构造一个我们自己指定特定属性的对象，最简单的方法就是为每种情况提供一个构造函数，我们根据不要的构造函数来得到我们需要的包含了指定属性的对象。但是属性一多，就需要更多的构造函数了，这样显然是不明智的。
### 定义
>建造者模式：它就是单独的来对一个对象进行构造，将一个复杂的构建与其表示相分离，使得同样的构建过程可以创建不同的表示。也就是说它来完成对象的构造过程，并且这个过程可以构造出上面我们所说的所有我们希望得到的对象。

>建造模式是将复杂的内部创建封装在内部，对于外部调用的人来说，只需要传入建造者和建造工具，对于内部是如何建造成成品的，调用者无需关心。
### 具体示例
>定义一个Person类，他包含了所有属性的get,set方法
```java
public class Person {
   private String name;
   private boolean sex;
   private int age;
   private float height;
   private float weight;
   
   public Person(String name, boolean sex, int age, float height, float weight) {
       this.name = name;
       this.sex = sex;
       this.age = age;
       this.height = height;
       this.weight = weight;
   }
}
```
>创建一个Builder类
```java
public class Builder {
   private String name;
   private boolean sex;
   private int age;
   private float height;
   private float weight;

   public Builder setName(String name) {
       this.name = name;
       return this;
   }

   public Builder setSex(boolean sex) {
       this.sex = sex;
       return this;
   }

   public Builder setAge(int age) {
       this.age = age;
       return this;
   }

   public Builder setHeight(float height) {
       this.height = height;
       return this;
   }

   public Builder setWeight(float weight) {
       this.weight = weight;
       return this;
   }

   public Person create() {
       return new Person(name, sex, age, height, weight);
   }
}
```
>如何使用?
```java
Builder builder = new Builder();
builder.setName("Mirhunana");
builder.setAge(23);
Perons person = builder.create();
```
### 抽离
#### 抽象建造者（Builder）角色
>给出一个抽象接口，以规范产品对象的各个组成成分的建造。一般而言，此接口独立于应用程序的商业逻辑。模式中直接创建产品对象的是具体建造者 (ConcreteBuilder)角色。具体建造者类必须实现这个接口所要求的两种方法：一种是建造方法(buildPart1和 buildPart2)，另一种是返还结构方法(retrieveResult)。一般来说，产品所包含的零件数目与建造方法的数目相符。换言之，有多少零件，就有多少相应的建造方法
```java
public interface Builder {

    public Builder setName(String name);

    public Builder setSex(boolean sex);

    public Builder setAge(int age);

    public Builder setHeight(float height);

    public Builder setWeight(float weight);

    public Person create();
}
```
#### 具体建造者（ConcreteBuilder）角色
>担任这个角色的是与应用程序紧密相关的一些类，它们在应用程序调用下创建产品的实例。这个角色要完成的任务包括：
* 实现抽象建造者Builder所声明的接口，给出一步一步地完成创建产品实例的操作
* 在建造过程完成后，提供产品的实例。
```java
public class ConcreteBuilder implements Builder {
    private String name;
    private boolean sex;
    private int age;
    private float height;
    private float weight;

    public Builder setName(String name) {
        this.name = name;
        return this;
    }

    public Builder setSex(boolean sex) {
        this.sex = sex;
        return this;
    }

    public Builder setAge(int age) {
        this.age = age;
        return this;
    }

    public Builder setHeight(float height) {
        this.height = height;
        return this;
    }

    public Builder setWeight(float weight) {
        this.weight = weight;
        return this;
    }

    public Person create() {
        return new Person(name, sex, age, height, weight);
    }
}
```
#### 导演者（Director）角色
>担任这个角色的类调用具体建造者角色以创建产品对象。应当指出的是，导演者角色并没有产品类的具体知识，真正拥有产品类的具体知识的是具体建造者角色。
```java
public class Director {
    private Builder builder;

    public Director(Builder builder){
        this.builder = builder;
    }

    public void construct(String name, boolean sex, int age, float height, float weight) {
        builder.setName(name);
        builder.setSex(sex);
        builder.setAge(age);
        builder.setHeight(height);
        builder.setWeight(weight);
    }
}
```
#### 产品（Product）角色
>产品便是建造中的复杂对象。一般来说，一个系统中会有多于一个的产品类，而且这些产品类并不一定有共同的接口，而完全可以是不相关联的。
```java
public class Person {
   private String name;
   private boolean sex;
   private int age;
   private float height;
   private float weight;
   
   public Person(String name, boolean sex, int age, float height, float weight) {
       this.name = name;
       this.sex = sex;
       this.age = age;
       this.height = height;
       this.weight = weight;
   }
}
```
#### 使用
```java
public class Test {
   public static void main(String[] args) {
       Builder builder = new ConcreteBuilder();
       Director pcDirector = new Director(builder);
       pcDirector.construct("Mirhunana", true, 23, 180, 100);
       Person person = builder.create();
   }
}
```
***
## 单例模式
### 单例模式的使用条件
>使用单例模式有一个必要条件：在一个系统要求一个类只有一个实例时才应该使用单例模式。反过来，如果一个类可以有几个实例存在，那么就没有必要使用这个单例类。

>作为对象的创建模式，单例模式确保某一个类只有一个实例。而且自行实例化并向整个系统系统这个实例。这个类称为单例类。
### 单例模式的三个特点
* 某个类只有一个实例
* 必须自行创建这个实例
* 必须自行向整个系统提供这个实例
### 不同模式的创建方式
#### 懒汉式单例（并发执行时线程不安全）
```java
//懒汉式单例类.在第一次调用的时候实例化自己   
public class Singleton {  
    private Singleton() {}  
    private static Singleton single=null;  
    //静态工厂方法   
    public static Singleton getInstance() {  
         if (single == null) {    
             single = new Singleton();  
         }    
        return single;  
    }  
} 
```
##### 改进
>在getInstance方法上加同步
```java
public class Singleton {  
    private static Singleton instance = null;  
    private Singleton() {}  
    public static synchronized Singleton getInstance() {  
        if (instance == null) {  
            instance = new Singleton();  
        }  
        return instance;  
    }  
}
```
>静态内部类:利用了ClassHoader的机制来保证初始化instance时只有一个线程，所以也是线程安全的，同时没有性能损耗。在第一次加载Holder时初始化一次instance对象, 保证唯一性, 也延迟了单例的实例化。
```java
public class Singleton {  
    private Singleton() {}  
    public static class Holder {  
        // 这里的私有没有什么意义  
        /* private */static Singleton instance = new Singleton();  
    }  
    public static Singleton getInstance() {  
        // 外围类能直接访问内部类（不管是否是静态的）的私有变量  
        return Holder.instance;  
    }  
} 
```
#### 饿汉式单例（天生线程安全）
>在类被加载的时候，静态变量single会被初始化，java语言中单例类的一个重要特点是类的构造函数是私有的，从而避免外界利用这个构造函数直接创建出任意多个实例。值得指出的是，由于构造函数是私有的，因此类不能被继承。 
饿汉式在类创建的同时就已经创建好一个静态的对象供系统使用，以后不再改变，所以天生是线程安全的。
```java
public class Singleton {  
    private Singleton() {}  
    private static final Singleton single = new Singleton();  
    //静态工厂方法   
    public static Singleton getInstance() {  
        return single;  
    }  
}
```
##### 枚举单例（线程安全）
```java
enum SingletonEnum {
    INSTANCE;
    public void doSomething() {
        System.out.println("do sth.");
    }
}
```

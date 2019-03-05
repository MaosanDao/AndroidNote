# Android中常用的设计模式简介以及实例代码
## 主要内容
* [建造者模式](#建造者模式)
* [单例模式](#单例模式)
* [工厂模式](#工厂模式)
* [策略模式](#策略模式)
* [适配器设计模式](#适配器设计模式)
* [观察者模式](#观察者模式)
* [原型模式](#原型模式)
****
## 建造者模式
### 引出问题
```
假如一个对象由许多不同的属性构造，我们想要构造一个我们自己指定特定属性的对象，
最简单的方法就是为每种情况提供一个构造函数，我们根据不要的构造函数来得到我们需要的包含了指定属性的对象。
但是属性一多，就需要更多的构造函数了，这样显然是不明智的。
```
### 定义
```
建造者模式：它就是单独的来对一个对象进行构造，将一个复杂的构建与其表示相分离，
使得同样的构建过程可以创建不同的表示。也就是说它来完成对象的构造过程，
并且这个过程可以构造出上面我们所说的所有我们希望得到的对象。

建造模式是将复杂的内部创建封装在内部，对于外部调用的人来说，只需要传入建造者和建造工具，
对于内部是如何建造成成品的，调用者无需关心。
```
### 具体示例
#### 定义一个Person类，他包含了所有属性的get,set方法
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
#### 创建一个Builder类
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
#### 如何使用?
```java
Builder builder = new Builder();
builder.setName("Mirhunana");
builder.setAge(23);
Perons person = builder.create();
```
****
## 单例模式
### 单例模式的使用条件
```
使用单例模式有一个必要条件：在一个系统要求一个类只有一个实例时才应该使用单例模式。
反过来，如果一个类可以有几个实例存在，那么就没有必要使用这个单例类。

作为对象的创建模式，单例模式确保某一个类只有一个实例。而且自行实例化并向整个系统系统这个实例。
这个类称为单例类。
```
### 单例模式的三个特点
```
* 某个类只有一个实例
* 必须自行创建这个实例
* 必须自行向整个系统提供这个实例
```
### 不同模式的创建方式
#### 懒汉式单例（并发执行时线程不安全） --- 调用的时候，才进行初始化
```java
//懒汉式单例类.在第一次调用的时候实例化自己   
public class Singleton {  
	//私有外部不能直接调用
	private Singleton() {}  
	//内部私有静态实例
	private static Singleton single=null;  
	//静态工厂方法 --- 提供给外部调用获取本类实例的
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
#### 静态内部类
```
利用了ClassHoader的机制来保证初始化instance时只有一个线程，所以也是线程安全的，
同时没有性能损耗。在第一次加载Holder时初始化一次instance对象, 保证唯一性, 也延迟了单例的实例化。
```
```java
public class Singleton {  
	private Singleton() {}  
	private static class Holder {  
			// 这里的私有没有什么意义  
			/* private */static Singleton instance = new Singleton();  
	}  
	public static Singleton getInstance() {  
			// 外围类能直接访问内部类（不管是否是静态的）的私有变量  
			return Holder.instance;  
	}  
} 
```
#### 饿汉式单例（天生线程安全） --- 直接在类初始化的时候，将本类实例初始化
```
在类被加载的时候，静态变量single会被初始化，java语言中单例类的一个重要特点是类的构造函数是私有的，
从而避免外界利用这个构造函数直接创建出任意多个实例。值得指出的是，由于构造函数是私有的，因此类不能被继承。 
饿汉式在类创建的同时就已经创建好一个静态的对象供系统使用，以后不再改变，所以天生是线程安全的。
```
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
***
## 观察者模式
```
定义对象间的一种一对多的依赖关系，当一个对象的状态发生改变时，所有依赖于它的对象都得到通知并被自动更新。
这种模式最常用在我们熟悉的事件驱动模型里面，像VC、Android界面里面的事件响应，就是基于观察者模式来实现。

观察者模式主要是针对一对多的数据更新。简单来说就是，系统里面某个元素更新了数据，
然后有好几个元素是使用了这个元素的数据。此时更新了数据的对象，就要通知其他使用了它数据的对象，让他们都进行更新。

简单来讲，当一个对象（被观察者）变化时，其它依赖该对象的对象（观察者）都会收到通知，并且随着变化！对象之间是一种一对多的关系。
```
### 简单示例代码
```java
//通用接口
public interface Observer {
	public void update();
}
//---------------------------------------------------------
//观察者1
public class Observer1 implements Observer {
	@Override
	public void update() {
			System.out.println("observer1 has received!");
	}
}
//---------------------------------------------------------
//观察者2
public class Observer2 implements Observer {
	@Override
	public void update() {
			System.out.println("observer2 has received!");
	}
}
//---------------------------------------------------------
//
public interface Subject {

	/*增加观察者*/
	public void add(Observer observer);
	/*删除观察者*/
	public void del(Observer observer);

	/*通知所有的观察者*/
	public void notifyObservers();
	/*自身的操作*/
	public void operation();
}
//---------------------------------------------------------
public abstract class AbstractSubject implements Subject {
	//观察者集合
	private Vector<Observer> vector = new Vector<Observer>();
	@Override
	public void add(Observer observer) {
			vector.add(observer);
	}
	@Override
	public void del(Observer observer) {
			vector.remove(observer);
	}
	@Override
	public void notifyObservers() {
			Enumeration<Observer> enumo = vector.elements();
			while (enumo.hasMoreElements()) {//循环调用每个观察者的update方法
					enumo.nextElement().update();
			}
	}
}
//---------------------------------------------------------
public class MySubject extends AbstractSubject {
	@Override
	public void operation() {
			System.out.println("update self!");
			notifyObservers();//通知所有观察者
	}
}

//---------------------------------------------------------
//测试
public class ObserverTest {
	public static void main(String[] args) {
			Subject sub = new MySubject();
			sub.add(new Observer1());//增加2个观察者
			sub.add(new Observer2());
			sub.operation();//这里调用，那么就会分别更新各自内部的方法
	}
}
```
***
## 工厂模式
```
一共分为2种模式：
1.工厂方法模式
	1.1 普通工厂模式
	1.2 多个工厂方法模式
	1.3 静态工厂方法模式
2.抽象工厂模式
```
### 基础代码
```java
public interface Sender {
	public void Send();
}
public class MailSender implements Sender {
	@Override
	public void Send() {
	System.out.println("this is mail sender!");
	}
}
public class SmsSender implements Sender {
	@Override
	public void Send() {
	System.out.println("this is sms sender!");
	}
}
```
### 普通工厂模式
```java
//工厂
public class SendFactory {
//在工厂模式中，调用者不管内部怎么实现的不同派生类的具体代码，只需要将类型传递
//最后，工厂会返回相应的对象回去
public Sender produce(String type) {
		if ("mail".equals(type)) {
				return new MailSender();
		} else if ("sms".equals(type)) {
				return new SmsSender();
		} else {
				System.out.println("请输入正确的类型!");
				return null;
		}
	}
}
```
### 多个工厂方法模式（如果用户在普通工厂模式传递了错误的类型怎么办？）
```
该模式是对普通工厂方法模式的改进，在普通工厂方法模式中，如果传递的字符 
串出错，则不能正确创建对象，而多个工厂方法模式是提供多个工厂方法，分别 
创建对象。
```
```java
//这里的工厂直接将具体的生产对象方法暴露出去
public class SendFactory {
	public Sender produceMail(){
			return new MailSender();
	}
	public Sender produceSms(){
			return new SmsSender();
	}
}
public class FactoryTest {
	public static void main(String[] args) {
			SendFactory factory = new SendFactory();
			Sender sender = factory.produceMail();
			sender.send();
	}
}
```
### 静态工厂方法模式（将多个工厂方法改为静态方法即可）
```java
public class SendFactory {
	public static Sender produceMail(){
		return new MailSender();
	}
	public static Sender produceSms(){
		return new SmsSender();
	}
}
public class FactoryTest {
	public static void main(String[] args) {
		Sender sender = SendFactory.produceMail();
		sender.send();
	}
}
```
### 抽象工厂模式
```java
//抽象工厂接口
public interface Provider {
    public Sender produce();
}

//-------------------------------------------------------------------------------------
//产品接口
public interface Sender {
	public void send();
}
//-------------------------------------------------------------------------------------
public class MailSender implements Sender {
	@Override
	public void send() {
			System.out.println("this is mail sender!");
	}
}
//-------------------------------------------------------------------------------------
public class SmsSender implements Sender {
	@Override
	public void send() {
			System.out.println("this is sms sender!");
	}
}
//-------------------------------------------------------------------------------------
//直接新增工厂方法
public class SendSmsFactory implements Provider {
	@Override
	public Sender produce() {
			return new SmsSender();
	}
}

//-------------------------------------------------------------------------------------
//直接新增工厂方法
public class SendMailFactory implements Provider {
	@Override
	public Sender produce() {
			return new MailSender();
	}
}
//-------------------------------------------------------------------------------------
public class Test {
	public static void main(String[] args) {
			Provider provider = new SendMailFactory();
			Sender sender = provider.produce();
			sender.send();
	}
}
```
***
## 策略模式
```
策略模式定义了一系列算法，并将每个算法封装起来，使他们可以相互替换，且算法的变化不会影响到使用算法的客户。
需要设计一个接口，为一系列实现类提供统一的方法，多个实现类实现该接口。

设计一个抽象类（可有可无，属于辅助类），提供辅助函数。策略模式的决定权在用户，
系统本身提供不同算法的实现，新增或者删除算法，对各种算法做封装。
因此，策略模式多用在算法决策系统中，外部用户只需要决定用哪个算法即可。
```
```java
//统一的接口
public interface ICalculator {
	public int calculate(String exp);
}

//---------------------------------------------------------
public class Minus extends AbstractCalculator implements ICalculator {
	@Override
	public int calculate(String exp) {
			int arrayInt[] = split(exp, "-");
			return arrayInt[0] - arrayInt[1];
	}
}

//---------------------------------------------------------
public class Plus extends AbstractCalculator implements ICalculator {
	@Override
	public int calculate(String exp) {
			int arrayInt[] = split(exp, "\\+");
			return arrayInt[0] + arrayInt[1];
	}
}
//--------------------------------------------------------
//提供辅助的方法（可有可无）
public class AbstractCalculator {
	public int[] split(String exp, String opt) {
			String array[] = exp.split(opt);
			int arrayInt[] = new int[2];
			arrayInt[0] = Integer.parseInt(array[0]);
			arrayInt[1] = Integer.parseInt(array[1]);
			return arrayInt;
	}
}
//---------------------------------------------------------
public class StrategyTest {
	public static void main(String[] args) {
			String exp = "2+8";
			ICalculator cal = new Plus();//这里根据用户自己的选择进行不同的算法调用
			int result = cal.calculate(exp);
			System.out.println(result);
	}
}
```
***
## 适配器设计模式
```
大致分为2种：
1. 对象适配器模式
2. 类适配器模式
```
### 对象适配器模式
```
1.创建一个适配器的接口
```
```java
interface Adapter {//适配器类
	int convert_5v();//装换成5V
}
```
```
2. 创建被适配角色,一般是已存在的类
```
```java
public class Electric {// 电源
	public int output_220v() {//输出220V
			return 220;
	}
}
```
```
3.创建一个适配器
```
```java
public class PhoneAdapter implements Adapter {//手机适配器类
	private Electric mElectric;//适配器持有源目标对象

	public PhoneAdapter(Electric electric) {//通过构造方法传入对象
			mElectric = electric;
	}

	@Override
	public int convert_5v() {
			System.out.println("适配器开始工作：");
			System.out.println("输入电压：" + mElectric.output_220v());
			System.out.println("输出电压：" + 5);
			return 5;
	}
}
```
```
测试
```
```java
public void test() {
	Electric electric = new Electric();
	System.out.println("默认电压：" + electric.output_220v());

	Adapter phoneAdapter = new PhoneAdapter(electric);//传递一个对象给适配器
	System.out.println("适配转换后的电压：" + phoneAdapter.convert_5v());
}
```
#### 说明
```
适配的源目标对象需要传递给适配器
```
### 类适配器模式
```
类适配器只要是通过继承源目标类来实现
```
```
1.创建一个适配器的接口
```
```java
interface Adapter {//适配器类
	int convert_5v();//装换成5V
}
```
```
2. 创建被适配角色,一般是已存在的类
```
```java
public class Electric {// 电源
	public int output_220v() {//输出220V
			return 220;
	}
}
```
```
3.这里变为了继承的方式进行适配
```
```java
public class PhoneAdapter extends Electric implements Adapter {//通过继承源目标类的方式，不持有源目标对象
	@Override
	public int convert_5v() {
		System.out.println("适配器开始工作：");
		System.out.println("输入电压：" + output_220v());
		System.out.println("输出电压：" + 5);
		return 5;
	}
}
```
```
测试
```
```java
public void test() {
	Adapter phoneAdapter = new PhoneAdapter();
	System.out.println("适配转换后的电压：" + phoneAdapter.convert_5v());
}
```
#### 说明
```
类适配器模式只要通过继承源目标类来实现，无需持有源目标对象。
```
***
## 原型模式
```
用原型实例指定创建对象的种类，并通过拷贝这些原型创建新的对象。

1.一个已存在的对象（即原型），通过复制原型的方式来创建一个内部属性跟原型都一样的新的对象，这就是原型模式。
2.原型模式的核心是clone方法，通过clone方法来实现对象的拷贝。

实现Cloneable接口:
```
```java
//具体原型类,卡片类
public class Card implements Cloneable {//实现Cloneable接口，Cloneable只是标识接口
	private int num;//卡号
	private Spec spec = new Spec();//卡规格

	public Card() {
		System.out.println("Card 执行构造函数");
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setSpec(int length, int width) {
		spec.setLength(length);
		spec.setWidth(width);
	}

	@Override
	public String toString() {
		return "Card{" +
						"num=" + num +
						", spec=" + spec +
						'}';
	}

	//重写clone()方法，clone()方法不是Cloneable接口里面的，而是Object里面的
	@Override
	protected Card clone() throws CloneNotSupportedException {
		System.out.println("clone时不执行构造函数");
		return (Card) super.clone();
	}
}

//规格类，有长和宽这两个属性
public class Spec {
	private int width;
	private int length;

	public void setLength(int length) {
			this.length = length;
	}

	public void setWidth(int width) {
			this.width = width;
	}

	@Override
	public String toString() {
			return "Spec{" +
							"width=" + width +
							", length=" + length +
							'}';
	}
}
```
```
创建客户端类：
既要使用原型模式的地方。
```
```java
public class Client {
	public void test() throws CloneNotSupportedException {

		Card card1 = new Card();
		card1.setNum(9527);
		card1.setSpec(10, 20);
		System.out.println(card1.toString());
		System.out.println("----------------------");

		Card card2 = card1.clone();
		System.out.println(card2.toString());
		System.out.println("----------------------");
	}
}
```
```java
//输出结果为：
Card 执行构造函数
Card{num=9527, spec=Spec{width=20, length=10}}
----------------------
clone时不执行构造函数
Card{num=9527, spec=Spec{width=20, length=10}}
----------------------
```
#### 说明
```
1.clone对象时不会执行构造函数。
2.clone方法不是Cloneable接口中的，而是Object中的方法。Cloneable是个标识接口，表面了这个对象是可以拷贝的，如果没有实现Cloneable接口却调用clone方法则会报错。
```
#### 额外
这个链接是说明拷贝时候是否是[深拷贝和浅拷贝的区别](https://www.jianshu.com/p/6d1333917ae5)

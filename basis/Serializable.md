# Android序列化相关知识点
## 序列化的初衷
```
在日常的应用开发中，我们可能需要让某些对象离开内存空间，存储到物理磁盘，以便长期保存，同时也能减少对内存的压力，
而在需要时再将其从磁盘读取到内存，比如将某个特定的对象保存到文件中，隔一段时间后再把它读取到内存中使用，
那么该对象就需要实现序列化操作。
```
## 序列和反序列化
### 序列化含义
```
由于存在于内存中的对象都是暂存的，无法长期驻存，所以为了吧对象的状态保持下来，这时需要把对象写入到磁盘或者其他介质中。
这个过程就叫序列化。
```
### 反序列化的含义
```
反序列化的工作和序列化刚刚相反，就是将磁盘或者其他介质中的对象，反序列化读取到内存中，以便后续的操作。
```
### 概括
```
序列化是指将对象实例的状态存储到存储媒体（磁盘或者其他介质）的过程。在此过程中，
先将对象的公共字段和私有字段以及类的名称（包括类所在的程序集）转换为字节流，然后再把字节流写入数据流。

在随后对对象进行反序列化时，将创建出与原对象完全相同的副本。
```
### 实现序列化的条件
```
在Java中，一个对象想要进行序列化，那么相对应的该类就必须要实现Serializable接口。

在Android中，则是实现Parcelable接口。
```
### 使用情况
```
1.内存中的对象写入到磁盘中
2.用套接字在网络上传送对象

不限于以上两种
```
***
### Serializable的使用
```
Serializable是java提供的一个序列化接口，它是一个空接口，专门为对象提供标准的序列化和反序列化操作，
使用Serializable实现类的序列化比较简单，只要在类声明中实现Serializable接口即可，同时强烈建议声明序列化标识。

也就是说，直接实现Serializable接口，最好再加上序列化标识。

实例代码：
```
```java
public class User implements Serializable {
    //这里是序列化标识serialVersionUID
    private static final long serialVersionUID = -2083503801443301445L;
    
    private int id;
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```
#### 关于序列化标识SerialVersionUID
```
SerialVersionUID:
  这个是一个对象进行序列化和反序列化的凭证，序列化与反序列化的serialVersionUID必须相同才能够使序列化操作成功。
  
具体过程是：
  序列化操作的时候系统会把当前类的serialVersionUID写入到序列化文件中，当反序列化时系统会去检测文件中的serialVersionUID，
  判断它是否与当前类的serialVersionUID一致，如果一致就说明序列化类的版本与当前类版本是一样的，可以反序列化成功，否则失败。
  
为什么要自己指定？
  虽然在不指定的情况下，对象序列化也会自动创建一个UID，但是假如序列化后的文件，多了一个空格，那么自动生成的UID就会截然不同。
  所以，最好自己指定一下。这样就算有细微的变化，也不会导致UID不一致。
```
#### 如何将对象进行序列化和反序列化
```java
public class Demo {

    public static void main(String[] args) throws Exception {
        // 构造对象
        // 这个对象已经实现了Serializable接口
        User user = new User();
        user.setId(1000);
        user.setName("韩梅梅");

        // 把对象序列化到文件
        // 写入到文件
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/serializable/user.txt"));
        oos.writeObject(user);
        oos.close();

        // 反序列化到内存
        // 从文件中读取信息，并实例化为一个对象
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/serializable/user.txt"));
        User userBack = (User) ois.readObject();
        System.out.println("read serializable user:id=" + userBack.getId() + ", name=" + userBack.getName());
        ois.close();
    }
}
```
```
以上代码注意两点：

  1.如果在反序列化的时候，之前序列化的对象的成员变量和方法已经发生了变化，那么即使UID相同，也不会反序列化成功的。
  2.静态变量不属于对象，所以无法进行反序列化。
```
#### 改变系统默认的序列化过程
```java
public class User implements Serializable {

    private static final long serialVersionUID = -4083503801443301445L;

    private int id;

    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 序列化时,
     * 首先系统会先调用writeReplace方法,在这个阶段,
     * 可以进行自己操作,将需要进行序列化的对象换成我们指定的对象.
     * 一般很少重写该方法
     */
    private Object writeReplace() throws ObjectStreamException {
        System.out.println("writeReplace invoked");
        return this;
    }
    /**
     *接着系统将调用writeObject方法,
     * 来将对象中的属性一个个进行序列化,
     * 我们可以在这个方法中控制住哪些属性需要序列化.
     */
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        System.out.println("writeObject invoked");
        //这里只序列化name属性
        out.writeObject(this.name == null ? "默认值" : this.name);
    }

    /**
     * 反序列化时,系统会调用readObject方法,将我们刚刚在writeObject方法序列化好的属性,
     * 反序列化回来.然后通过readResolve方法,我们也可以指定系统返回给我们特定的对象
     * 可以不是writeReplace序列化时的对象,可以指定其他对象.
     */
    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        System.out.println("readObject invoked");
        this.name = (String) in.readObject();
        System.out.println("got name:" + name);
    }

    /**
     * 通过readResolve方法,我们也可以指定系统返回给我们特定的对象
     * 可以不是writeReplace序列化时的对象,可以指定其他对象.
     * 一般很少重写该方法
     */
    private Object readResolve() throws ObjectStreamException {
        System.out.println("readResolve invoked");
        return this;
    }
}
```
***
### Parcelable的使用
```
Parcelable是Android上特有的序列化操作。

之前介绍的Parcelable序列化在内存的消耗上过大，所以建议使用Parcelable来进行序列化操作。
但是，如果通过Intent在Activity中传递数据，那么比较麻烦了。
```
```java
public class User implements Parcelable {

    public int id;
    public String name;
    public User friend;

    /**
     * 当前对象的内容描述,一般返回0即可
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * 将当前对象写入序列化结构中
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //写的顺序和读的顺序一样
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeParcelable(this.friend, 0);
    }

    public NewClient() {}

    /**
     * 从序列化后的对象中创建原始对象
     */
    protected NewClient(Parcel in) {
        //写的顺序和读的顺序一样
        this.id = in.readInt();
        this.name = in.readString();
        //friend是另一个序列化对象，此方法序列需要传递当前线程的上下文类加载器，否则会报无法找到类的错误
        this.friend=in.readParcelable(Thread.currentThread().getContextClassLoader());
    }

    /**
     * public static final一个都不能少，内部对象CREATOR的名称也不能改变，必须全部大写。
     * 重写接口中的两个方法：
     * createFromParcel(Parcel in) 实现从Parcel容器中读取传递数据值,封装成Parcelable对象返回逻辑层，
     * newArray(int size) 创建一个类型为T，长度为size的数组，供外部类反序列化本类数组使用。
     */
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        /**
         * 从序列化后的对象中创建原始对象
         */
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        /**
         * 创建指定长度的原始对象数组
         */
        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
```
```
概括：
  一句话概括来说就是通过writeToParcel将我们的对象映射成Parcel对象，再通过createFromParcel将Parcel对象映射成我们的对象。
  也可以将Parcel看成是一个类似Serliazable的读写流，通过writeToParcel把对象写到流里面，在通过createFromParcel从流里读取对象，

注意：
  我们自己实现的时候，写的顺序要和读的顺序一样。
```
#### Parcelable使用时机
```
在Android中使用Intent传递自定义引用序列化对象：
  
   1.putExtra(String name, Parcelable value)
    设置自定义类型并实现Parcelable的对象
   2.putExtra(String name, Parcelable[] value)
    设置自定义类型并实现Parcelable的对象数组
   3.putParcelableArrayListExtra(String name, ArrayList value)
    设置List数组，其元素必须是实现了Parcelable接口的数据
```
***
### Parcelable与Serializable区别
#### 实现过程
```
Serializable：
  只需要实现Serializable接口，并且给对象打上一个UID的标记即可，剩下的，系统会自动进行序列化和反序列化。
  
Parcelable：
  不仅仅只是实现了Parcelable接口，还需要我们自行在该类中添加一个静态的成员变量CREATOR，
  这个变量要实现Parcelable，Creator接口，并实现读写的抽象方法。
```
#### 设计初衷
```
Serializable：--- 内存消耗大，磁盘中读写
  为了序列化对象到本地文件、数据库、网络流、RMI以便数据传输，当然这种传输可以是程序内的也可以是两个程序间的。
  
Parcelable：--- 内存消耗较小，android特有，内存中读写
  由于Serializable效率过低，消耗大，而android中数据传递主要是在内存环境中（内存属于android中的稀有资源），
  因此Parcelable的出现为了满足数据在内存中低开销而且高效地传递问题。
```
#### 效率
```
Serializable：
  在磁盘中读写，效率较慢，且序列化的过程中使用了反射，产生了很多临时对象。
  优点，代码少。
  
  将对象序列化到存储设备或者通过网络传输的时候，优先选择Serializable。
  
Parcelable：
  在内存中读写，效率较高，且适用于android中的ADIL传输数据等。
  优先选择Parcelable在Android应用程序间传递数据。
```
***
### AndroidStudio中快速生成Parcelable代码
```
使用android studio中的插件：
  android Parcelable code generator 
```
### AndroidStudio中快速生成Serializable的UID
```
在Android Studio选项中，查找Inspections选项，并勾选Serializable class without serialVersionUID 即可。
```
**
## 摘录并整理至
* [Android序列化总结](https://www.jianshu.com/p/208ac4a71c6f)



































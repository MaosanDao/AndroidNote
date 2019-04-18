# Java中Transient关键字的作用分析

## 作用和使用方法
### 它的作用
```
只要一个对象实现了Serilizable接口，那么这个类的所有属性和方法都会被自动序列化。

那么，我这个类中的一些属性需要序列化，另外一个属性不需要序列化呢？
那就要使用到transient关键字了，它的作用是：

加上这个关键字后，它的生命周期筋窜于调用者的内存中而不会写到磁盘里持久化。
```
### 示例代码
```java
public class TransientTest {
 
    public static void main(String[] args) {
 
        User user = new User();
        user.setUsername("Alexia");
        user.setPasswd("123456");
 
        System.out.println("read before Serializable: ");
        System.out.println("username: " + user.getUsername());
        System.err.println("password: " + user.getPasswd());
 
        try {
            ObjectOutputStream os = new ObjectOutputStream(
                    new FileOutputStream("C:/user.txt"));
            os.writeObject(user); // 将User对象写进文件
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(
                    "C:/user.txt"));
            user = (User) is.readObject(); // 从流中读取User的数据
            is.close();
 
            System.out.println("\nread after Serializable: ");
            System.out.println("username: " + user.getUsername());
            System.err.println("password: " + user.getPasswd());
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
 
class User implements Serializable {
    private static final long serialVersionUID = 8294180014912103005L;  
 
    private String username;
    //这里使用transient修饰，那么久不会被序列化
    private transient String passwd;
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPasswd() {
        return passwd;
    }
 
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
 
}


//执行结果为：
read before Serializable: 
username: Alexia
password: 123456
 
read after Serializable: 
username: Alexia
password: null

//以上结果表明，这个password字段根本没有从文件中获取到数据
```
## transient使用小结
```
小结：

1.一旦变量被transient修饰，变量将不再是对象持久化的一部分，该变量内容在序列化后无法获得访问。

2.transient关键字只能修饰变量，而不能修饰方法和类。
  注意，本地变量是不能被transient关键字修饰的。变量如果是用户自定义类变量，
       则该类需要实现Serializable接口。
       
3.被transient关键字修饰的变量不再能被序列化，一个静态变量不管是否被transient修饰，均不能被序列化。


关于第三点的代码说明：
```
```java
public class TransientTest {
 
    public static void main(String[] args) {
 
        User user = new User();
        user.setUsername("Alexia");
        user.setPasswd("123456");
 
        System.out.println("read before Serializable: ");
        System.out.println("username: " + user.getUsername());
        System.err.println("password: " + user.getPasswd());
 
        try {
            ObjectOutputStream os = new ObjectOutputStream(
                    new FileOutputStream("C:/user.txt"));
            os.writeObject(user); // 将User对象写进文件
            os.flush();
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            // 在反序列化之前改变username的值
            User.username = "jmwang";
 
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(
                    "C:/user.txt"));
            user = (User) is.readObject(); // 从流中读取User的数据
            is.close();
 
            System.out.println("\nread after Serializable: ");
            System.out.println("username: " + user.getUsername());
            System.err.println("password: " + user.getPasswd());
 
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
 
class User implements Serializable {
    private static final long serialVersionUID = 8294180014912103005L;  
 
    public static String username;
    private transient String passwd;
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getPasswd() {
        return passwd;
    }
 
    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
 
}

//运行结果为：
read before Serializable: 
username: Alexia
password: 123456
 
read after Serializable: 
username: jmwang
password: null


//以上结果说明：
//其实最后获取到的“jmwang”本身就是在JVM内存中获取到的，而并非是通过序列化而得的。
//故可以证明第三点的正确性

```
## 转载并整理至
* [Java transient关键字使用小记](http://www.importnew.com/21517.html)














































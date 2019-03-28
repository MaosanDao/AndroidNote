# MVC、MVP、MVVM介绍以及相关知识点
****
## 目录
* [MVC](#mvc)
* [MVP](#mvp)
* [MVVM](#mvvm)
****
## MVC
#### 图示
![MVC](https://images2015.cnblogs.com/blog/826785/201609/826785-20160922195119231-4850362.png)
#### 通用解释
```
  MVC，(Model View Controller)，是软件架构中最常见的一种框架，简单来说就是通过controller的控制去操作model层的数据，
并且返回给view层展示。
```
#### 在Android中的解释
```
针对于原生的App则可以这样去理解MVC：

Model：各种java bean，以及一些数据库的类型，存储之类的。
View：xml中的一些布局代码，则对应view层。
Controller：activity中的一些逻辑代码。
```
#### 存在的一些问题
```
1.在原生App中，xml作为view层，控制能力实在太弱了。试想，如果想要将一个按钮进行隐藏，那么就需要在activity中使用
  逻辑代码来进行控制。那么，就会造成activity既是controller又是view的局面，进而造成c层拥挤和堵塞。
  
2.view层和model层是相互可知的，这就说明了它们之间有着一种耦合。这是程序非常致命的弱点。
```
#### 大体请求流程
```
1. View接受用户的请求
2. View传递请求给Controller
3. Controller操作Model进行数据更新
4. Model通知View变化
5. View根据更新的数据做出显示
```
#### 具体代码示例（[摘录](https://blog.csdn.net/singwhatiwanna/article/details/80904132)）
BaseModel，所有Model的基类。这里的onDestroy()方法用于跟activity或者fragment生命周期同步，在destroy做一些销毁操作。
```java
public interface BaseModel {
    void onDestroy();
}
```
Callback是根据View或者Controller调用Model时回调的参数个数选择使用。
```java
public interface Callback1<T> {
    void onCallBack(T t);
}
public interface Callback2<T,P> {
    void onCallBack(T t,P p);
}
```
Model类：
```java
public class SampleModel implements BaseModel{

  public void  getUserInfo(String uid,Callback1<UserInfo> callback)
  {
      UserInfo userInfo= new HttpUtil<UserInfo>().get(uid);
      //通过回调传出去
      callback.onCallBack(userInfo);
  }

  @Override
  public void onDestroy() {
    //接口中的onDestroy，做一些销毁工作
  }
  
  //Bean
  public class UserInfo
  {
      private int age;
      private String name;

      public int getAge() {
          return age;
      }

      public void setAge(int age) {
          this.age = age;
      }

      public String getName() {
          return name;
      }

      public void setName(String name) {
          this.name = name;
      }
  }
}
```
View以及Controller（因为View的更新和控制都在Activity中做的）
```java
public class SampleActivity extends AppCompatActivity {

  private SampleModel sampleModel;
  Button button;
  EditText textView;
  TextView tvAge,tvName;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sample);
      //调用Model
      sampleModel=new SampleModel();
      
      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              getUserInfo(textView.getText().toString());
          }
      });

  }

  @Override
  protected void onDestroy() {
      super.onDestroy();
      sampleModel.onDestroy();
  }

  /**
   * 获取用户信息
   * @param uid
   */
  private void getUserInfo(String uid)
  {
      sampleModel.getUserInfo(uid, new Callback1<SampleModel.UserInfo>() {
          @Override
          public void onCallBack(SampleModel.UserInfo userInfo) {
              setDataToView(userInfo);
          }
      });
  }

  /**
   * 设置用户信息到view
   */
  private void setDataToView(SampleModel.UserInfo userInfo)
  {
      tvAge.setText(userInfo.getAge());
      tvName.setText(userInfo.getName());
  }

}
```
```
上述代码的大体MVC流程：

1. Button被点击：View -> Controller
2. 获取用户信息事件的触发：Controller -> Model
3. 绑定用户信息到View：Controller -> View
```
#### 总结
```
* 具有一定的分层，model彻底解耦，controller和view并没有解耦 
* controller和view在a

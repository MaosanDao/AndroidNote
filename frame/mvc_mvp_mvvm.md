# MVC、MVP、MVVM介绍以及相关知识点
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
* controller和view在android中无法做到彻底分离
* 业务逻辑被放置在model层，能够更好的复用和修改增加业务
```
****
## 进阶MVP
#### 图示
![MVP](https://images2015.cnblogs.com/blog/826785/201609/826785-20160922195139793-736076577.jpg)
#### 通用解释
```
  用于操作view层发出的事件传递到presenter层中，presenter层去操作model层，并且将数据返回给view层，
整个过程中view层和model层完全没有联系。
```
#### 那View层和Presenter层又是耦合了？
```
  其实不是的，对于view层和presenter层的通信，我们是可以通过接口实现的;具体的意思就是说我们的activity，
fragment可以去实现实现定义好的接口，而在对应的presenter中通过接口调用方法。

  不仅如此，我们还可以编写测试用的View，模拟用户的各种操作，从而实现对Presenter的测试。
这就解决了MVC模式中测试，维护难的问题。
```
#### 大体请求流程
```
1. View 接受用户请求
2. View 传递请求给Presenter
3. Presenter做逻辑处理，修改Model
4. Model 通知Presenter数据变化
5. Presenter 更新View（接口）
```
#### MVP的特点
```
* Presenter完全将Model和View解耦，主要逻辑处于Presenter中
* Presenter和具体View没有直接关联，通过定义好的接口进行交互
* View变更时，可以保持Presenter不变(符合面向对象编程的特点)
* View只应该有简单的Set/Get方法、用户输入、界面展示的内容，此外没有更多内容
* 低耦合：Model和View的解耦，决定了该特性
```
#### MVP的优点
```
* 可重用性：Model层可以用于多个View。比如请求影视数据，可能有多个页面都需要这个功能，但是Model层代码只要有一份就可以了
* 方便测试：可以单独对Model层和View层进行测试
* 低耦合：Model、View层的变换不会影响到对方
```
#### 具体代码示例
##### 契约类
契约类用于定义同一个界面的view的接口和presenter的具体实现。好处是通过规范的方法命名和注释可以清晰的看到整个页面的逻辑。
```java
public interface LoginMVPContract{
  //View接口
  public interface ILoginView<T>{
      public void showLoginSuccess(T data);
      public void showLoginFailed(String errorMsg);
  }
  //Task接口
  public interface ILoginTask{
      public void startLogin(String phoneNumber, ILoginCallBack callback);
  }
  //Presenter
  public interface ILoginPresenter{
      public void startLogin(String phoneNumber);
  }
  //Presenter和Task间交互的接口
  public interface ILoginCallBack<T>{
      public void onLoginSuccess(T data);
      public void onLoginFailed(String errorMsg);
  }
}
```
##### Model
下面是伪代码，相当于一个实体类，和一个网络请求类。
```java
//Bean
public class LoginResultBean {}

//请求网络
public class LoginTask implements LoginMVPContract.ILoginTask{
    @Override
    public void startLogin(String phoneNumber, LoginMVPContract.ILoginCallBack callback) {
        //模拟请求网络
        if(true){
            callback.onLoginSuccess(new LoginResultBean());
        }else{
            callback.onLoginFailed("登录失败");
        }
    }
}
```
##### Presenter
```java
public class LoginPresenter implements LoginMVPContract.ILoginPresenter, LoginMVPContract.ILoginCallBack{

  //回调到View的接口
  LoginMVPContract.ILoginView mLoginView;
  //请求任务
  LoginMVPContract.ILoginTask mTask;

  public LoginPresenter(LoginMVPContract.ILoginView loginView, LoginMVPContract.ILoginTask task){
      mLoginView = loginView;
      mTask = task;
  }

  /**
   * 接口回调
   */
  @Override
  public void onLoginSuccess(Object data) {
      mLoginView.showLoginSuccess(data);
  }

  @Override
  public void onLoginFailed(String errorMsg) {
      //回调到View
      mLoginView.showLoginFailed(errorMsg);
  }

  @Override
  public void startLogin(String phoneNumber) {
      mTask.startLogin(phoneNumber, this);
  }
}
```
##### View
主要实例化Presenter类，并将View的接口实现，方便更新View的变化。
```java
public class LoginFragment extends SupportFragment implements LoginMVPContract.ILoginView<LoginResultBean>{
  //Presenter实例化
  LoginMVPContract.ILoginPresenter mLoginPresenter;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      //初始化，将View和Model的请求任务传递进去
      mLoginPresenter = new LoginPresenter(this, new LoginTask());
      mLoginPresenter.startLogin("17777777777");
  }

  @Override
  public void showLoginSuccess(LoginResultBean data) {
      //登陆成功 --- View的回调
  }

  @Override
  public void showLoginFailed(String errorMsg) {
      //登录失败 --- View的回调
  }
}
```
#### 优化MVP的文件量
```
* 采用泛型定义契约类，将model、view、presenter定义在一个契约类中
* 结构清晰，一个契约类对应一个业务模块
```
****
## 完全进阶MVVM
#### 图示
![MVVM](https://images2015.cnblogs.com/blog/826785/201609/826785-20160922195156715-801029603.jpg)
#### 解释
```
  MVVM，(Model View ViewModel)如果说MVP是对MVC的进一步改进，那么MVVM则是思想的完全变革。
它是将“数据模型数据双向绑定”的思想作为核心，因此在View和Model之间没有联系。
  通过ViewModel进行交互，而且Model和ViewModel之间的交互是双向的，因此视图的数据的变化会同时修改数据源，
而数据源数据的变化也会立即反应到View上。
```



























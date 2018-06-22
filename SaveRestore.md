# Activity和Fragment的保存和恢复数据
## Activity在保存和恢复的时候发生了什么？
>当OnSaveInstanceState()被调用的时候，Activity会在View层次(View Hierachy)收集每一个View的状态，
>注意，只会搜集实现了View状态保存/恢复的内部方法的View的数据。一旦onRestoreInstanceState被调用,
>Activity将会将这些搜集到的数据一对一的返还给View层次里在搜集的时候提供了同样的android:id属性的View。
### 注意
>这也是为什么View 在没有被设置android:id属性的时候不能保存和恢复自己的状态的原因
### Activity的成员变量是不会自动保存的，所以需要自己来做处理。主要通过OnSaveInstanceState和OnRestoreInstanceState方法来保存和恢复
```java
public class MainActivity extends AppCompatActivity {

    // These variable are destroyed along with Activity
    private int someVarA;
    private String someVarB;

    ...

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("someVarA", someVarA);
        outState.putString("someVarB", someVarB);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        someVarA = savedInstanceState.getInt("someVarA");
        someVarB = savedInstanceState.getString("someVarB");
    }

}
```
## Fragment在保存和恢复的时候，基本和Activity是保持一致的。唯一不同的是Fragment没有onRestoreInstanceState方法，取而代之的是onActivityCreated方法来恢复数据
```java
public class MainFragment extends Fragment {

    // These variable are destroyed along with Activity
    private int someVarA;
    private String someVarB;

    ...

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("someVarA", someVarA);
        outState.putString("someVarB", someVarB);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        someVarA = savedInstanceState.getInt("someVarA");
        someVarB = savedInstanceState.getString("someVarB");
    }

}
```
>Fragment从后退后，从栈里退出，会造成其中的View都会被销毁（但是Fragment不会被销毁）,在这种情况下，Fragment中的View 状态的保存/恢复会被内部调用。
>结果就是，每一个实现了内部View 状态保存/恢复的View ，将会被自动的保存并且恢复状态。
### 总结
* Fragment中的View的保存和恢复不需要对其进行处理(只要实现了内部View 状态保存/恢复的View )
* Fragment的退栈，也不会让Fragment销毁，所以其中的成员变量也不需要做处理
```java
public class MainFragment extends Fragment {

    // These variable still persist in this case
    private int someVarA;
    private String someVarB;

    ...

}
```

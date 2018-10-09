# Activity和Fragment的保存和恢复数据
### Activity在保存和恢复的时候发生了什么？
>当OnSaveInstanceState()被调用的时候，Activity会在View层次(View Hierachy)收集每一个View的状态，
>注意，只会搜集实现了View状态保存/恢复的内部方法的View的数据。一旦onRestoreInstanceState被调用,
>Activity将会将这些搜集到的数据一对一的返还给View层次里在搜集的时候提供了同样的android:id属性的View。
### 注意
>这也是为什么View 在没有被设置android:id属性的时候不能保存和恢复自己的状态的原因
#### Activity的成员变量是不会自动保存的，所以需要自己来做处理。主要通过OnSaveInstanceState和OnRestoreInstanceState方法来保存和恢复
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
#### Fragment在保存和恢复的时候，基本和Activity是保持一致的。唯一不同的是Fragment没有onRestoreInstanceState方法，取而代之的是onActivityCreated方法来恢复数据
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
### 在Fragment中保存和恢复状态的最佳实践
#### 单独处理View的保存和恢复
>你应用中使用的每一个单独的View都必须在内部实现状态的保存和恢复
```java
public class CustomView extends View {

    ...

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        // Save current View's state here
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        // Restore View's state here
    }

    ...

}
```
>第三方的一些View，需要自己派生并添加保存和恢复数据的方法（onSaveInstanceState和onRestoreInstanceState）
```java
//
// Assumes that SomeSmartButton is a 3rd Party view that
// View State Saving/Restoring are not implemented internally
//
public class SomeBetterSmartButton extends SomeSmartButton {

    ...

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        // Save current View's state here
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        // Restore View's state here
    }

    ...

}
```
>除此之外，需要为想要保存数据的View设置ID属性,才能让其自行保存和恢复数据
#### 完全分开处理Fragment状态和view状态
>为了使你的代码变得干净和可扩展，你最好把Fragment状态和View状态分开处理。如果这里有任何属性是属于View的，在View内部进行保存和恢复.如果这里有任何属性是属于Fragment的，在Fragment内部进行保存和恢复。这里有一个例子。
```java
public class MainFragment extends Fragment {

    ...

    private String dataGotFromServer;//这里是属于Fragment的，所以在onSaveInstanceState中进行保存和恢复

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("dataGotFromServer", dataGotFromServer);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dataGotFromServer = savedInstanceState.getString("dataGotFromServer");
    }

    ...

}
```
>不要在Fragment中的onSaveInstanceState中保存View状态,也不要在View中保存Fragment的的状态。
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
## 参考并修改至
[[译] 保存/恢复 Activity 和 Fragment 状态的最佳实践](https://segmentfault.com/a/1190000006691830)

# Android OOM出现的常见原因及解决办法
>虽然JAVA有垃圾回收机制，但也存在内存泄露。如果我们一个程序中，已经不再使用某个对象，但是因为仍然有引用指向它，垃圾回收器就无法回收它，当然 该对象占用的内存就无法被使用，这就造成了内存泄露。
## 常见的造成OOM的主要原因
* 数据库的cursor没有进行关闭
* 构造Adpater没有使用缓存ContentView
* 调用了RegisterReceiver()后，没有进行UnRegisterReceiver()
* 没有关闭InputStream和OutputStream
* Bitmap使用后没有recycle()
* Context泄漏
* static关键字
### 1.Static关键字
> static是Java中的一个关键字，当用它来修饰成员变量时，那么该变量就属于该类，而不是该类的实例。不少程序员喜欢用static这个关键字修饰变量，因为他使得变量的生命周期大大延长啦，并且访问的时候，也极其的方便，用类名就能直接访问，各个资源间 传值也极其的方便，所以，它经常被我们使用。但如果用它来引用一些资源耗费过多的实例（Context的情况最多），这时就要谨慎对待了。
#### Static Context泄漏
```java
public class ClassName {  
   private static Context mContext;  
   //省略  
}  
```
以上的代码是很危险的，如果将Activity赋值到么mContext的话。那么即使该Activity已经onDestroy，但是由于仍有对象保存它的引用，因此该Activity依然不会被释放，并且，如果该activity里面再持有一些资源，那就糟糕了。
#### 如何解决
* 尽量避免static成员变量引用资源过多的实例
* Context尽量用Application的Context，它的周期比较长
### 2.Context泄漏
>内部类持有外部对象造成的内存泄露，常见是内部线程造成的。
```java
public class BasicActivity extends Activity {  
  @Override  
  public void onCreate(Bundle savedInstanceState) {  
      super.onCreate(savedInstanceState);  
      setContentView(R.layout.main);  
      new MyThread().start();  
  }   

  private class OneThread extends Thread{  
      @Override  
      public void run() {  
          super.run();  
          //do somthing  
      }  
  }  
} 
```
假如此时屏幕发生转变，则相应的Activity会进行销毁，但是此时并不会。因为我们的线程是Activity的内部类，所以OneThread中保存了Activity的一个引用，当OneThread的run函数没有结束 时，OneThread是不会被销毁的，因此它所引用的老的Activity也不会被销毁，因此就出现了内存泄露的问题。
#### 解决方法
* 将线程的内部类，改为静态的内部类，且注意第二条
* 在线程内部采用弱引用的方式去保存Context
### 3.Bitmap泄漏
>可以说出现OutOfMemory问题的绝大多数人，都是因为Bitmap的问题。因为Bitmap占用的内存实在是太多了，它是一个“超级大胖子”，特别是分辨率大的图片，如果要显示多张那问题就更显著了。
#### 如何解决
##### 及时的销毁
```java
if(!bitmap.isRecycled()){
    bitmap.recycle();
    bitmap = null;
}  
```
##### 设置一定的采样率
>有时候，我们要显示的区域很小，没有必要将整个图片都加载出来，而只需要记载一个缩小过的图片，这时候可以设置一定的采样率，那么就可以大大减小占用的内存。
```java
private ImageView preview;  
BitmapFactory.Options options = new BitmapFactory.Options();  

options.inSampleSize = 2;//图片宽高都为原来的二分之一，即图片为原来的四分之一  
Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri), null, options);  
preview.setImageBitmap(bitmap);  
```
### 4.未关闭InputStream和OutputStream
* 及时进行关闭相应的流
### 5.调用了registerReceiver()后，未调用unregisterReceiver()
```java
IntentFilter postFilter = new IntentFilter(); 
postFilter.addAction(getPackageName() + ".background.job"); 
this.registerReceiver(receiver, postFilter);  

this.unRegisterReceiver(receiver); //需要成对的出现
```
### 6.构造Apapter没有使用缓存ContentView
>当一个listview的子项有成千上万个时，如果我们没有采用一定的策略来重用这些资源，那应用的那点对内存，是远远不够使用的。在继承BaseAdapter时会让我们重写getView(int position, View   convertView, ViewGroup parent)方法，第二个参数convertView就是我们要用到的重用的对象。
### 7.数据库的Cursor没有进行关闭
>Cursor是Android查询数据后得到的一个管理数据集合的类，正常情况下，如果查询得到的数据量较小时不会有内存问题，而且虚拟机能够保证Cusor最终会被释放掉。
```java
Cursor cursor = null;  
try {  
  cursor = mContext.getContentResolver().query(uri,null, null,null,null);  
  if(cursor != null) {  
      cursor.moveToFirst();  
      //do something  
  }  
} catch (Exception e) {  
  e.printStackTrace();    
} finally {  
  if (cursor != null) {  
     cursor.close();  //需要把这个Cursor关闭掉
  }  
}
```
## 总结
>要减小内存的使用，其实还有很多方法和要求。比如不要使用整张整张的图，尽量使用9path图片。Adapter要使用convertView等等，好多细节都可以节省内存。
## 摘自
[Android OOM出现常见原因及解决办法](https://blog.csdn.net/hudfang/article/details/51781997)




























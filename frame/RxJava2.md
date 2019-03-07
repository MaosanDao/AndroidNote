# RxJava2基本知识与源码解析(整理并转载至[原文](http://gank.io/post/560e15be2dca930e00da1083))
***
## RxJava是什么？
```
异步
```
## 为什么不用线程、Handler？
```
简洁。

它的简洁的与众不同之处在于，随着程序逻辑变得越来越复杂，它依然能够保持简洁。
```
## 举个栗子
```
现在有一个需求：
  一个自定的图片加载器，可以加载多个图片。现在需要从本地中加载多个图片到该View中。
```
```java
//常规写法
new Thread() {
    @Override
    public void run() {
        super.run();
        for (File folder : folders) {
            File[] files = folder.listFiles();
            for (File file : files) {
                if (file.getName().endsWith(".png")) {
                    final Bitmap bitmap = getBitmapFromFile(file);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageCollectorView.addImage(bitmap);
                        }
                    });
                }
            }
        }
    }
}.start();

//RxJava写法：
Observable.from(folders)
    //传入File，返回Observable<File>
    .flatMap(new Func1<File, Observable<File>>() {
        @Override
        public Observable<File> call(File file) {
            return Observable.from(file.listFiles());
        }
    })
    .filter(new Func1<File, Boolean>() {
        @Override
        public Boolean call(File file) {
            return file.getName().endsWith(".png");
        }
    })
    .map(new Func1<File, Bitmap>() {
        @Override
        public Bitmap call(File file) {
            return getBitmapFromFile(file);
        }
    })
    //线程切换
    .subscribeOn(Schedulers.io())//注册在IO线程
    .observeOn(AndroidSchedulers.mainThread())//监听或者观察到的时候，在主线程更新UI
    .subscribe(new Action1<Bitmap>() {
        @Override
        public void call(Bitmap bitmap) {
            imageCollectorView.addImage(bitmap);
        }
    });
```
## API介绍和原理简介
### RxJava的观察者模式
```
四个基本概念：
  1.Observable (可观察者，即被观察者)
  2.Observer (观察者)
  3.subscribe (订阅)
  4.Event（事件）
  
解释：Observable和Observer通过subscribe()方法实现订阅关系，从而Observable可以在需要的时候发出事件来通知Observer

回调事件：
  1.onNext
    普通事件。
    
  2.onCompleted
    事件队列完成。RxJava不仅把每个事件单独处理，还会把它们看做一个队列。RxJava规定，
    当不会再有新的onNext()发出时，需要触发 onCompleted()方法作为标志。
    
  3.onError
    事件队列异常。在事件处理过程中出异常时，onError()会被触发，同时队列自动终止，不允许再有事件发出。
    
  注意：
  onCompleted()和onError()二者也是互斥的，即在队列中调用了其中一个，就不应该再调用另一个
```
![image1](http://ww3.sinaimg.cn/mw1024/52eb2279jw1f2rx46dspqj20gn04qaad.jpg)
### 基本实现过程
#### 创建观察者Observer
```java
//观察者Observer
Observer<String> observer = new Observer<String>() {
    @Override
    public void onNext(String s) {
        Log.d(tag, "Item: " + s);
    }

    @Override
    public void onCompleted() {
        Log.d(tag, "Completed!");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(tag, "Error!");
    }
};
//观察者Observer的抽象类Subscriber
Subscriber<String> subscriber = new Subscriber<String>() {
    @Override
    public void onNext(String s) {
        Log.d(tag, "Item: " + s);
    }

    @Override
    public void onCompleted() {
        Log.d(tag, "Completed!");
    }

    @Override
    public void onError(Throwable e) {
        Log.d(tag, "Error!");
    }
};
```
```
观察者Observer和抽象类Subscriber的异同点：

  1.其实他们是一样的。因为在RxJava的Subscribe过程中，Observer也总是会先被转换成一个Subscriber再使用。
  
  2.Subscriber会增加一个方法onStart();
    它会在 subscribe 刚开始，而事件还未发送之前被调用，可以用于做一些准备工作，例如数据的清零或重置。
    注意它的线程在subscribe的线程中。如果需要在做一些主线程相关的业务，则需要doOnSubscribe()方法。
    
  3.unsubscribe()
    用于取消订阅。
    因为在 subscribe() 之后， Observable 会持有 Subscriber 的引用，这个引用如果不能及时被释放，将有内存泄露的风险。
    所以最好在onPause或者onDestroy中取消订阅来避免内存泄漏。
```
#### 创建被观察者Observable
```
它决定了什么时候触发事件以及触发怎么样的事件。
```
```java
//传入了一个Observable.OnSubscribe对象，因为这个对象会被储存在Observable中
//当Observable被订阅的时候，OnSubscribe的Call()方法就会被调用。
//所以，由被观察者Observable调用了观察者Observer的回调方法，就实现了由被观察者向观察者的事件传递，即观察者模式。
Observable observable = Observable.create(new Observable.OnSubscribe<String>() {
    @Override
    public void call(Subscriber<? super String> subscriber) {
        subscriber.onNext("Hello");
        subscriber.onNext("Hi");
        subscriber.onNext("Aloha");
        subscriber.onCompleted();
    }
});
```
```
create是创造时间序列的基本方法。其中RxJava还封装了一些：
  1.just(T...)
    将传入的参数依次发出来。
    
  2.from(T[])
    将传入的数组拆分为对象后，依次发出来。
```
#### Subscribe (订阅)
```java
observable.subscribe(observer);
// 或者：
observable.subscribe(subscriber);

//注意，是“被观察者”订阅“观察者”
```
```
源码伪代码展示：
```
```java
// 注意：这不是 subscribe() 的源码，而是将源码中与性能、兼容性、扩展性有关的代码剔除后的核心代码。
// 如果需要看源码，可以去 RxJava 的 GitHub 仓库下载。
public Subscription subscribe(Subscriber subscriber) {
    subscriber.onStart();
    onSubscribe.call(subscriber);
    return subscriber;
}
```
```
可以看出：
  1.subscriber.onStart();最先调用，也就是subscriber的初始方法。
  2.observable开始调用call方法，这里即开始回调了。
  3.将传入的Subscriber以Subscription返回，方便unsubscribe();
```
![image3](http://ww3.sinaimg.cn/mw1024/52eb2279jw1f2rx4ay0hrg20ig08wk4q.gif)
#### 自动根据定义创建出 Subscriber
```java
Action1<String> onNextAction = new Action1<String>() {
    // onNext()
    @Override
    public void call(String s) {
        Log.d(tag, s);
    }
};
Action1<Throwable> onErrorAction = new Action1<Throwable>() {
    // onError()
    @Override
    public void call(Throwable throwable) {
        // Error handling
    }
};
Action0 onCompletedAction = new Action0() {
    // onCompleted()
    @Override
    public void call() {
        Log.d(tag, "completed");
    }
};

// 自动创建 Subscriber ，并使用 onNextAction 来定义 onNext()
observable.subscribe(onNextAction);
// 自动创建 Subscriber ，并使用 onNextAction 和 onErrorAction 来定义 onNext() 和 onError()
observable.subscribe(onNextAction, onErrorAction);
// 自动创建 Subscriber ，并使用 onNextAction、 onErrorAction 和 onCompletedAction 来定义 onNext()、 onError() 和 onCompleted()
observable.subscribe(onNextAction, onErrorAction, onCompletedAction);
```
```
Action0:
  无参无返回。且能自动创建Subscriber。
Action1:
  有参无返回。且能自动创建Subscriber。
```
#### 场景实例
```java
//打印字符串数组

//将字符串数组names中的所有字符串依次打印出来
String[] names = ...;
Observable.from(names)
    .subscribe(new Action1<String>() {
        @Override
        public void call(String name) {
            Log.d(tag, name);
        }
    });

//由指定的一个drawable文件id drawableRes取得图片，并显示在ImageView中，并在出现异常的时候打印Toast报错
int drawableRes = ...;
ImageView imageView = ...;
Observable.create(new OnSubscribe<Drawable>() {
    @Override
    public void call(Subscriber<? super Drawable> subscriber) {
        Drawable drawable = getTheme().getDrawable(drawableRes));
        subscriber.onNext(drawable);
        subscriber.onCompleted();
    }
}).subscribe(new Observer<Drawable>() {
    @Override
    public void onNext(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(Throwable e) {
        Toast.makeText(activity, "Error!", Toast.LENGTH_SHORT).show();
    }
});
```
```
注意：
  以上的展示，都是没有进行线程切换的实例。故没有啥用。接下来我们就展示一个中线程切换的工具Scheduler。
```
### 线程控制Scheduler
```
在RxJava中，如果不进行线程切换，那么默认都是在一个线程中执行的。即，在哪个线程调用subscribe()，就在哪个线程生产事件。
在哪个线程生产事件，就在哪个线程消费事件。如果需要切换线程，就需要用到Scheduler调度器。

其中，线程控制Scheduler可以控制每一段代码运行在什么样的线程，内置了一下：
  1.Schedulers.immediate()
    直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
    
  2.Schedulers.newThread()
    总是启用新线程，并在新线程执行操作。
    
  3.Schedulers.io()
    I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
    io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程。
    
  4.Schedulers.computation()
    计算所使用的 Scheduler。
    
  5.Android主线程
    AndroidSchedulers.mainThread()，它指定的操作将在Android主线程运行。
```
```java
Observable.just(1, 2, 3, 4)
    .subscribeOn(Schedulers.io()) // 指定 subscribe()发生在IO线程，及被创建的事件会在IO线程发出
    .observeOn(AndroidSchedulers.mainThread()) // 指定Subscriber的回调发生在主线程，即在主线程中进行打印
    .subscribe(new Action1<Integer>() {
        @Override
        public void call(Integer number) {
            Log.d(tag, "number:" + number);
        }
    });
    
//以上情况多适用于，“后台线程取数据，前台展示数据”的程序策略
```
### 变换
```
核心功能：
  所谓变换，就是将事件序列中的对象或整个序列进行加工处理，转换成不同的事件或事件序列。
```
```java
Observable.just("images/logo.png") // 输入类型 String
    .map(new Func1<String, Bitmap>() {
        @Override
        public Bitmap call(String filePath) { // 参数类型 String
            return getBitmapFromPath(filePath); // 返回类型 Bitmap
        }
    })
    .subscribe(new Action1<Bitmap>() {
        @Override
        public void call(Bitmap bitmap) { // 参数类型 Bitmap
            showBitmap(bitmap);
        }
    });
```
```
FuncX和ActionX同样是Rxjava的一个接口，他们的根本区别是，FuncX包装的是有返回值的方法。
上述代码中，map()方法将参数中的String转换为Bitmap。
这是一个单个时间的转换，而RxJava还可以对整个时间进行转换。
```
#### 变换方法 --- map()
![](http://ww1.sinaimg.cn/mw1024/52eb2279jw1f2rx4fitvfj20hw0ea0tg.jpg)
#### 变换方法 --- flatMap()
```java
Student[] students = ...;
Subscriber<Course> subscriber = new Subscriber<Course>() {
    @Override
    public void onNext(Course course) {
        Log.d(tag, course.getName());
    }
    ...
};

Observable.from(students)
    .flatMap(new Func1<Student, Observable<Course>>() {
        @Override
        public Observable<Course> call(Student student) {
            //这里通过flatmap，将Student转换为一个Observable，且和外部的Observable进行合并平铺
            return Observable.from(student.getCourses());
        }
    })
    .subscribe(subscriber);
```
![](http://ww1.sinaimg.cn/mw1024/52eb2279jw1f2rx4i8da2j20hg0dydgx.jpg)




























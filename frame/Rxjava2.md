# RxJava2的相关知识点和部分操作符介绍（整理至[原文](https://www.jianshu.com/p/0cd258eecf60)）
***
## 列表
* [针对于RxJava1的异同](#针对于rxjava1的异同)
  * [接口变化](#接口变化)
  * [还是观察者模式](#还是观察者模式)
  * [Observable](#observable)
  * [简化订阅方法](#简化订阅方法)
  * [线程调度](#线程调度)
  * [内置的线程调度](#内置的线程调度)
* [再看操作符](#再看操作符)
  * [转换对象之Map](#转换对象之map)
  * [不交错顺序发射之Concat](#不交错顺序发射之concat)
  * [结合多个数据源之Zip](#结合多个数据源之zip)
  * [心跳任务之Interval](#心跳任务之interval)
***
## 针对于RxJava1的异同
### 接口变化
```
他们分别是：
  1.Publisher
  2.Subscriber
  3.Subscription
  4.Processor
  
最核心的是：Publisher和Subscriber
  Publisher：可以发出一系列的事件。
  Subscriber：负责和处理这些事件。
  
其中Publisher和Flowable，他们只是背压。且他们都是被观察者。

背压的解释：
  背压是指在异步场景中，被观察者发送事件速度远快于观察者的处理速度的情况下，一种告诉上游的被观察者降低发送速度的策略。
  
对于RxJava1，RxJava2把Observable拆分Observable和Flowable。
```
### 还是观察者模式
```
两种观察者模式：
  1.Observable ( 被观察者 ) / Observer ( 观察者 ) --- 不支持背压
  2.Flowable （被观察者）/ Subscriber （观察者） --- 支持背压
  
在 RxJava 2.x 中，Observable 用于订阅 Observer，不再支持背压（1.x 中可以使用背压策略），
而 Flowable 用于订阅 Subscriber ， 是支持背压（Backpressure）的。
```
### Observable
```
相对与RxJava1，取消了Subscriber。转为改为了：
  ObservableEmmiter，俗称发射器。
  
同时创建观察者的时候，取消了Subscriber。改为了：
  Observer，并且还多了一个Disposable参数。
  
示例代码：
```
```java
Observable.create(new ObservableOnSubscribe<Integer>() { // 第一步：初始化Observable
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
            //ObservableEmitter为发射器
                Log.e(TAG, "Observable emit 1" + "\n");
                e.onNext(1);
                Log.e(TAG, "Observable emit 2" + "\n");
                e.onNext(2);
                Log.e(TAG, "Observable emit 3" + "\n");
                e.onNext(3);
                e.onComplete();
                Log.e(TAG, "Observable emit 4" + "\n" );
                e.onNext(4);
            }
        }).subscribe(new Observer<Integer>() { // 第三步：订阅
            //取消了Subscriber，改为了Observer
            //第二步：初始化Observer
            private int i;
            private Disposable mDisposable;

            @Override
            public void onSubscribe(@NonNull Disposable d) { 
            //多了一个Disposable参数
                mDisposable = d;
            }

            @Override
            public void onNext(@NonNull Integer integer) {
                i++;
                if (i == 2) {
                    // 在RxJava 2.x 中，新增的Disposable可以做到切断的操作，让Observer观察者不再接收上游事件
                    mDisposable.dispose();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e(TAG, "onError : value : " + e.getMessage() + "\n" );
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "onComplete" + "\n" );
            }
        });
```
### 简化订阅方法
```
具体的相关改动：
  1.ActionX ---> Consumer
  2.BiConsumer则为接收2个值
  3.FunX ---> Function用户变换对象
  4.Predicate用于判断
```
### 线程调度
```
基本和RxJava1一样。

简单的归纳：
  1.subscribeOn() 指定的就是发射事件的线程，observerOn 指定的就是订阅者接收事件的线程。
  2.多次指定发射事件的线程只有第一次指定的有效，也就是说多次调用 subscribeOn() 只有第一次的有效，其余的会被忽略。
  3.但多次指定订阅者接收线程是可以的，也就是说每调用一次 observerOn()，下游的线程就会切换一次。
  
代码示例：
```
```java
Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {
                Log.e(TAG, "Observable thread is : " + Thread.currentThread().getName());
                e.onNext(1);
                e.onComplete();
            }
        }).subscribeOn(Schedulers.newThread())//这个才有用，指定发送线程仅第一次有效
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())//每次指定，紧接的有效
                .doOnNext(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e(TAG, "After observeOn(mainThread)，Current thread is " + Thread.currentThread().getName());
                    }
                })
                .observeOn(Schedulers.io())//每次指定，紧接的有效
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@NonNull Integer integer) throws Exception {
                        Log.e(TAG, "After observeOn(io)，Current thread is " + Thread.currentThread().getName());
                    }
                });
                
//结果打印
 E/RxThreadActivity: Observable thread is : RxNewThreadScheduler-1
 E/RxThreadActivity: After observeOn(mainThread)，Current thread is main
 E/RxThreadActivity: After observeOn(io)，Current thread is RxCachedThreadScheduler-2
```
### 内置的线程调度
```
1.Schedulers.io() 代表io操作的线程, 通常用于网络,读写文件等io密集型的操作；
2.Schedulers.computation() 代表CPU计算密集型的操作, 例如需要大量计算的操作；
3.Schedulers.newThread() 代表一个常规的新线程；
4.AndroidSchedulers.mainThread() 代表Android的主线程
```
***
## 再看操作符
### 转换对象之Map
```
Map 操作符可以将一个 Observable 对象通过某种关系转换为另一个Observable 对象。基本和RxJava1是相同的。

但也有不同点：
  Func1 -- > Function
  Func2 -- > BiFunction
  
示例：
```
```java
Observable.create(new ObservableOnSubscribe<Response>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<Response> e) throws Exception {
                Builder builder = new Builder()
                        .url("http://api.avatardata.cn/MobilePlace/LookUp?key=ec47b85086be4dc8b5d941f5abd37a4e&mobileNumber=13021671512")
                        .get();
                Request request = builder.build();
                Call call = new OkHttpClient().newCall(request);
                Response response = call.execute();
                e.onNext(response);
            }
        }).map(new Function<Response, MobileAddress>() {//这里进行和转换：Response --> MobileAddress
                    @Override
                    public MobileAddress apply(@NonNull Response response) throws Exception {
                        if (response.isSuccessful()) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                Log.e(TAG, "map:转换前:" + response.body());
                                return new Gson().fromJson(body.string(), MobileAddress.class);
                            }
                        }
                        return null;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .doOnNext(new Consumer<MobileAddress>() {//在IO线程执行
                    @Override
                    public void accept(@NonNull MobileAddress s) throws Exception {
                        Log.e(TAG, "doOnNext: 保存成功：" + s.toString() + "\n");
                    }
                }).subscribeOn(Schedulers.io())//指定doOnNext和发射端线程
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MobileAddress>() {
                    @Override
                    public void accept(@NonNull MobileAddress data) throws Exception {
                        Log.e(TAG, "成功:" + data.toString() + "\n");
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        Log.e(TAG, "失败：" + throwable.getMessage() + "\n");
                    }
                });
```
### 不交错顺序发射之concat
```
concat 可以做到不交错的发射两个甚至多个 Observable 的发射事件，
并且只有前一个 Observable 终止(onComplete) 后才会订阅下一个 Observable。

采用 concat 操作符先读取缓存再通过网络请求获取数据。

代码实例：
```
```java
//读取缓存
Observable<FoodList> cache = Observable.create(new ObservableOnSubscribe<FoodList>() {
    @Override
    public void subscribe(@NonNull ObservableEmitter<FoodList> e) throws Exception {
        Log.e(TAG, "create当前线程:"+Thread.currentThread().getName() );
        FoodList data = CacheManager.getInstance().getFoodListData();

        // 在操作符 concat 中，只有调用 onComplete 之后才会执行下一个 Observable
        if (data != null){ // 如果缓存数据不为空，则直接读取缓存数据，而不读取网络数据
            isFromNet = false;
            Log.e(TAG, "\nsubscribe: 读取缓存数据:" );
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRxOperatorsText.append("\nsubscribe: 读取缓存数据:\n");
                }
            });

            e.onNext(data);
        }else {
            isFromNet = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRxOperatorsText.append("\nsubscribe: 读取网络数据:\n");
                }
            });
            Log.e(TAG, "\nsubscribe: 读取网络数据:" );
            e.onComplete();
        }
    }
});

//请求网络
Observable<FoodList> network = Rx2AndroidNetworking
                .get("http://www.tngou.net/api/food/list")
                .addQueryParameter("rows",10+"")
                .build()
                .getObjectObservable(FoodList.class);
                
//不交错发射
//两个Observable的泛型应当保持一致

//这里不交错的顺序发射，先发射cache，如果直接onNext，则不发射network。反之，如果onComplete则会发射第二个network
Observable.concat(cache,network)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(new Consumer<FoodList>() {
        @Override
        public void accept(@NonNull FoodList tngouBeen) throws Exception {
            //根据上文进行提示用户
            if (isFromNet){
                mRxOperatorsText.append("accept : 网络获取数据设置缓存: \n");
                CacheManager.getInstance().setFoodListData(tngouBeen);
            }

            mRxOperatorsText.append("accept: 读取数据成功:" + tngouBeen.toString()+"\n");
        }
    }, new Consumer<Throwable>() {
        @Override
        public void accept(@NonNull Throwable throwable) throws Exception {
            mRxOperatorsText.append("accept: 读取数据失败："+throwable.getMessage()+"\n");
        }
    });
```
### 结合多个数据源之Zip
```
zip 操作符可以将多个 Observable 的数据结合为一个数据源再发射出去。

代码示例：
```
```java
//发射源1
Observable<MobileAddress> observable1 = Rx2AndroidNetworking.get("http://api.avatardata.cn/MobilePlace/LookUp?key=ec47b85086be4dc8b5d941f5abd37a4e&mobileNumber=13021671512")
                .build()
                .getObjectObservable(MobileAddress.class);

//发射源2
Observable<CategoryResult> observable2 = Network.getGankApi()
        .getCategoryData("Android",1,1);

//将两个发射源（MobileAddress + CategoryResult）合并至一个String
Observable.zip(observable1, observable2, new BiFunction<MobileAddress, CategoryResult, String>() {
    @Override
    public String apply(@NonNull MobileAddress mobileAddress, @NonNull CategoryResult categoryResult) 
    throws Exception {
        //这里合并了
        return "合并后的数据为：手机归属地："+mobileAddress.getResult().getMobilearea()+
        "人名："+categoryResult.results.get(0).who;
    }}).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {
                Log.e(TAG, "accept: 成功：" + s+"\n");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                Log.e(TAG, "accept: 失败：" + throwable+"\n");
            }
        });
```
### 心跳任务之Interval
```java
private Disposable mDisposable;
@Override
protected void doSomething() {
    //每一秒发射一次
    mDisposable = Flowable.interval(1, TimeUnit.SECONDS)
            .doOnNext(new Consumer<Long>() {
                @Override
                public void accept(@NonNull Long aLong) throws Exception {
                    Log.e(TAG, "accept: doOnNext : "+aLong );
                }
            })
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Long>() {
                @Override
                public void accept(@NonNull Long aLong) throws Exception {
                    Log.e(TAG, "accept: 设置文本 ："+aLong );
                    mRxOperatorsText.append("accept: 设置文本 ："+aLong +"\n");
                }
            });
}

/**
 * 销毁时停止心跳
 */
@Override
protected void onDestroy() {
    super.onDestroy();
    if (mDisposable != null){
        //截断发射
        mDisposable.dispose();
    }
}
```

































# RecylerView和ListView的使用对比分析
## 基础使用对比
### ListView
* 继承重写BaseAdapter类
* 自定义ViewHolder和ConvertView一起完成复用的工作
### RecyclerView
* 继承重写 RecyclerView.Adapter 和 RecyclerView.ViewHolder
* 设置布局管理器，控制布局效果
```java
// 第一步：继承重写 RecyclerView.Adapter
public class AuthorRecyclerAdapter extends RecyclerView.Adapter<AuthorRecyclerAdapter.AuthorViewHolder> {
    ...
    @Override
    public AuthorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ...
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AuthorViewHolder holder, int position) {
        ...
        //这里进行view的相关操作
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    // 继承重写RecyclerView.ViewHolder
    class AuthorViewHolder extends RecyclerView.ViewHolder {
        ...
        public AuthorViewHolder(View itemView) {
            super(itemView);
            ...
            //这里完成控件的绑定
        }
    }
}

//如何使用？
mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
mRecyclerAdapter = new AuthorRecyclerAdapter(mData);

// 第二步：设置布局管理器，控制布局效果
LinearLayoutManager linearLayoutManager = new LinearLayoutManager(RecyclerDemoActivity.this);
linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
mRecyclerView.setLayoutManager(linearLayoutManager);

mRecyclerView.setAdapter(mRecyclerAdapter);
```
### 使用区别
* ViewHolder 的编写规范化了
* RecyclerView 复用 Item 的工作 Google 全帮你搞定，不再需要像 ListView 那样自己调用 setTag
* RecyclerView 需要多出一步 LayoutManager 的设置工作
## 布局效果(RecyclerView.LayoutManager)
>关键在于RecyclerView.LayoutManager类中，RecyclerView 在使用过程中要比 ListView 多一个 setLayoutManager 步骤，这个 LayoutManager 就是用于控制我们 RecyclerView 最终的展示效果的
### LinearLayoutManager（线性布局效果）、GridLayoutManager（网格布局效果）、StaggeredGridLayoutManager（瀑布流布局效果）
>常见的一些LayoutManager的一些API
```java
canScrollHorizontally();//能否横向滚动
canScrollVertically();//能否纵向滚动
scrollToPosition(int position);//滚动到指定位置

setOrientation(int orientation);//设置滚动的方向
getOrientation();//获取滚动方向

findViewByPosition(int position);//获取指定位置的Item View
findFirstCompletelyVisibleItemPosition();//获取第一个完全可见的Item位置
findFirstVisibleItemPosition();//获取第一个可见Item的位置
findLastCompletelyVisibleItemPosition();//获取最后一个完全可见的Item位置
findLastVisibleItemPosition();//获取最后一个可见Item的位置
```
## 空数据处理(ListView,RecyclerView并没有这类的API)
>ListView 提供了 setEmptyView 这个 API 来让我们处理 Adapter 中数据为空的情况，只需轻轻一 set 就能搞定一切。代码设置和效果如下:
```java
mListView = (ListView) findViewById(R.id.listview);
mListView.setEmptyView(findViewById(R.id.empty_layout));//设置内容为空时显示的视图
```
## HeaderView和FooterView(ListView可以直接设置，而RecyclerView没有)
### ListView
```java
listView.addFooterView(View v);
listView.addHeaderView(View v);
```
## 局部刷新对比
### ListView并没有直接可调用的API进行设置，但是可以使用自定义的方法（updateItemView）进行更新：
```java
public class AuthorListAdapter extends BaseAdapter {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ...
        return convertView;
    }

    /**
     * 更新Item视图，减少不必要的重绘
     *
     * @param listView
     * @param position
     */
    public void updateItemView(ListView listView, int position) {
        //换算成 Item View 在 ViewGroup 中的 index
        int index = position - listView.getFirstVisiblePosition();
        if (index >= 0 && index < listView.getChildCount()) {
            //更新数据
            AuthorInfo authorInfo = mAuthorInfoList.get(position);
            authorInfo.setNickName("Google Android");
            authorInfo.setMotto("My name is Android .");
            authorInfo.setPortrait(R.mipmap.ic_launcher);
            //更新单个Item
            View itemView = listView.getChildAt(index);
            getView(position, itemView, listView);
        }
    }

}
```
### RecyclerView可以直接进行Api的调用
>RecyclerView.Adapter 则我们提供了 notifyItemChanged 用于更新单个 Item View 的刷新，我们可以省去自己写局部更新的工作。
```java
notifyItemChanged(int position);
```
## 动画效果
### ListView自身没有提供封装好的API来实现动画效果切换，这里推荐一个第三方框架：
>[ListViewAnimations](https://github.com/nhaarman/ListViewAnimations)
### RecyclerView有直接提供的Api进行调用
```java
notifyItemChanged;
notifyItemInserted;
notifyItemMoved;
notifyItemRangeChanged;
notifyItemChanged;
...
```
### RecyclerView自定义Item动画(推荐：[recyclerview-animators](https://github.com/wasabeef/recyclerview-animators))
* 继承 RecyclerView.ItemAnimator 类，并实现相应的方法
* 调用 RecyclerView 的 setItemAnimator(RecyclerView.ItemAnimator animator) 方法设置完即可实现自定义的动画效果
### RecyclerView ItemTouchHelper(用于滑动和删除RecyclerView Item的工具类)
* 创建 ItemTouchHelper 实例，同时实现 ItemTouchHelper.SimpleCallback 中的抽象方法，用于初始化 ItemTouchHelper
* 调用 ItemTouchHelper 的 attachToRecyclerView 方法关联上 RecyclerView 即可
```java
//ItemTouchHelper 用于实现 RecyclerView Item 拖曳效果的类
ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        //actionState : action状态类型，有三类 ACTION_STATE_DRAG （拖曳），ACTION_STATE_SWIPE（滑动），ACTION_STATE_IDLE（静止）
        int dragFlags = makeFlag(ItemTouchHelper.ACTION_STATE_DRAG, ItemTouchHelper.UP | ItemTouchHelper.DOWN
                | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//支持上下左右的拖曳
        int swipeFlags = makeMovementFlags(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);//表示支持左右的滑动
        return makeMovementFlags(dragFlags, swipeFlags);//直接返回0表示不支持拖曳和滑动
    }

    /**
     * @param recyclerView attach的RecyclerView
     * @param viewHolder 拖动的Item
     * @param target 放置Item的目标位置
     * @return
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition();//要拖曳的位置
        int toPosition = target.getAdapterPosition();//要放置的目标位置
        Collections.swap(mData, fromPosition, toPosition);//做数据的交换
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /**
     * @param viewHolder 滑动移除的Item
     * @param direction
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int position = viewHolder.getAdapterPosition();//获取要滑动删除的Item位置
        mData.remove(position);//删除数据
        notifyItemRemoved(position);
    }

});
//关联RecyclerView
itemTouchHelper.attachToRecyclerView(mRecyclerView);
```
#### 说明
* getMovementFlags：用于告诉系统，我们的 RecyclerView 到底是支持滑动还是拖曳
* onSwiped：滑动时的回调
* onMove：拖拽时的回调
## 监听Item的事件
### ListView
>ListView 为我们准备了几个专门用于监听 Item 的回调接口，如单击、长按、选中某个 Item 等
```java
setOnItemClickListener;
setOnItemLongClickListener;
setOnItemSelectListener;
```
### RecyclerView
>而再来看看 RecyclerView ，它并没有像 ListView 提供太多关于 Item 的某种事件监听，唯一的就是 addOnItemTouchListener。
```java
addOnItemTouchListener;
```
>RecyclerView可以自己将点击事件暴露出去。
## 总结
* 布局效果对比(RecyclerView封装了三种效果)
* 空数据处理
* HeaderView 和 FooterView
* 局部刷新
* 动画效果
* 监听 Item 的事件
## 摘录至
[RecyclerView 和 ListView 使用对比分析](https://www.jianshu.com/p/f592f3715ae2)


























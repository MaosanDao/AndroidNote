# 使用RecyclerView 加载不同的Item布局

## 第一步
```java
//首先继承自
RecyclerView.Adapter<RecyclerView.ViewHolder>
```
## 根据自己的需求建立两个以上的ViewHolder
```java
//第一个ViewHolder
public class CardViewHolder extends RecyclerView.ViewHolder
//第二个ViewHolder
public class NoDeviceViewHolder extends RecyclerView.ViewHolder
```
## getItemViewType
```java
//返回的int值，是在onCreateViewHolder中的viewType
@Override
public int getItemViewType(int position) {
    if (mDevices.size() == 0) {
        return 0;
    } else {
        return 1;
    }
}
```
## onCreateViewHolder
```java
//根据上面的viewType来加载不同的ViewHolder
public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
if (viewType == 0) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_no_device_card_layout
            , parent, false);
    return new NoDeviceViewHolder(view);
} else {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_card_layout
            , parent, false);
    return new CardViewHolder(view);
}
```
## onBindViewHolder
```java
//使用instanceof来判断ViewHolder的种类来加载数据
public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position)
```

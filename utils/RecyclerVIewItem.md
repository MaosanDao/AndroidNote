# RecylerView间距工具类
## 代码
```java
/**
 * Comment: 间距工具类
 *
 * @author Vangelis.Wang in UpCan
 * @date 2018/7/31
 * Email:Pei.wang@icanup.cn
 */

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    /**
     * 为横向间的距离
     */
    private int leftRight;

    /**
     * 为纵向间距离
     */
    private int topBottom;

    public SpacesItemDecoration(int leftRight, int topBottom) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        //竖直方向的
        if (layoutManager.getOrientation() == LinearLayoutManager.VERTICAL) {
            //最后一项需要 bottom
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.bottom = topBottom;
            }
            outRect.top = topBottom;
            outRect.left = leftRight;
            outRect.right = leftRight;
        } else {
            //最后一项需要right
            if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
                outRect.right = leftRight;
            }
            outRect.top = topBottom;
            outRect.left = leftRight;
            outRect.bottom = topBottom;
        }
    }
}
```
## 使用
```java
RecylerView.addItemDecoration(new SpacesItemDecoration(12, 12));
```

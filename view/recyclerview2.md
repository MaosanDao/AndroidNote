#  RecyclerView 自动换行和间距处理

## 自动换行工具类
```java
/**
 * Comment: 超过屏幕自动换行的LayoutManager
 *
 * @author Vangelis.Wang in Make1
 * @date 2018/4/27
 * Email:Vangelis.wang@make1.cn
 */
public class AutoLineFeedLayoutManager extends RecyclerView.LayoutManager {
    private final String TAG = this.getClass().getName();
    private SparseArray<View> cachedViews = new SparseArray();
    private SparseArray<Rect> layoutPoints = new SparseArray<>();
    private int totalWidth;
    private int totalHeight;
    private int mContentHeight;
    private int mOffset;
    private boolean mIsFullyLayout;

    public AutoLineFeedLayoutManager(Context context, boolean isFullyLayout) {
        mIsFullyLayout = isFullyLayout;
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return true;
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        for (int i = 0; i < getItemCount(); ++i) {
            View v = cachedViews.get(i);
            Rect rect = layoutPoints.get(i);
            layoutDecorated(v, rect.left, rect.top, rect.right, rect.bottom);
        }

    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int scrollHorizontallyBy(int dx, RecyclerView.Recycler recycler, RecyclerView.State state) {
        return dx;
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int shouldOffset = 0;
        if (mContentHeight - totalHeight > 0) {
            int targetOffset = mOffset + dy;
            if (targetOffset < 0) {
                targetOffset = 0;
            } else if (targetOffset > (mContentHeight - totalHeight)) {
                targetOffset = (mContentHeight - totalHeight);
            }
            shouldOffset = targetOffset - mOffset;
            offsetChildrenVertical(-shouldOffset);
            mOffset = targetOffset;
        }

        if (mIsFullyLayout) {
            shouldOffset = dy;
        }
        return shouldOffset;
    }

    @Override
    public boolean canScrollHorizontally() {
        return true;
    }

    @Override
    public boolean canScrollVertically() {
        return true;
    }

    @Override
    public void onAdapterChanged(RecyclerView.Adapter oldAdapter, RecyclerView.Adapter newAdapter) {
        removeAllViews();
    }

    @Override
    public void onMeasure(RecyclerView.Recycler recycler, RecyclerView.State state, int widthSpec, int heightSpec) {
        super.onMeasure(recycler, state, widthSpec, heightSpec);

        final int widthMode = View.MeasureSpec.getMode(widthSpec);
        final int heightMode = View.MeasureSpec.getMode(heightSpec);
        final int widthSize = View.MeasureSpec.getSize(widthSpec);
        final int heightSize = View.MeasureSpec.getSize(heightSpec);

        int height;

        switch (widthMode) {
            case View.MeasureSpec.UNSPECIFIED:
                break;
            case View.MeasureSpec.AT_MOST:
                break;
            case View.MeasureSpec.EXACTLY:
                break;
            default:
        }

        removeAndRecycleAllViews(recycler);
        recycler.clear();
        cachedViews.clear();

        mContentHeight = 0;

        totalWidth = widthSize - getPaddingRight() - getPaddingLeft();

        int left = getPaddingLeft();
        int top = getPaddingTop();

        int maxTop = top;

        for (int i = 0; i < getItemCount(); ++i) {
            View v = recycler.getViewForPosition(i);
            addView(v);
            measureChildWithMargins(v, 0, 0);
            cachedViews.put(i, v);
        }

        for (int i = 0; i < getItemCount(); ++i) {
            View v = cachedViews.get(i);

            int w = getDecoratedMeasuredWidth(v);
            int h = getDecoratedMeasuredHeight(v);

            if (w > totalWidth - left) {
                left = getPaddingLeft();
                top = maxTop;
            }

            Rect rect = new Rect(left, top, left + w, top + h);
            layoutPoints.put(i, rect);

            left = left + w;

            if (top + h >= maxTop) {
                maxTop = top + h;
            }

        }

        mContentHeight = maxTop - getPaddingTop();

        height = mContentHeight + getPaddingTop() + getPaddingBottom();

        switch (heightMode) {
            case View.MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case View.MeasureSpec.AT_MOST:
                if (height > heightSize) {
                    height = heightSize;
                }
                break;
            case View.MeasureSpec.UNSPECIFIED:
                break;
            default:
        }

        totalHeight = height - getPaddingTop() - getPaddingBottom();

        setMeasuredDimension(widthSize, height);
    }
}
```

## 间距工具类

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
    private int space;

    /**
     * 为纵向间距离
     */
    private int topBottom;

    public SpacesItemDecoration(int leftRight, int topBottom) {
        this.leftRight = leftRight;
        this.topBottom = topBottom;
    }

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void onDraw(@NotNull Canvas c, @NotNull RecyclerView parent, @NotNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
    }

    @Override
    public void getItemOffsets(@NotNull Rect outRect, @NotNull View view, RecyclerView parent, @NotNull RecyclerView.State state) {
        if (parent.getLayoutManager() instanceof LinearLayoutManager) {
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
            //横向的
        } else {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildItemId(view) == 0) {
                outRect.top = space;
            }
        }
    }
}
```

## 使用方法
```kotlin
 mWorkAppGroupAdapter = WorkAppGroupAdapter()
mWorkAppGroupAdapter.refreshDatas(groupNameList)
work_auth_type_list.layoutManager =
    LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
work_auth_type_list.adapter = mWorkAppGroupAdapter
work_auth_type_list.addItemDecoration(SpacesItemDecoration(0,AutoSizeUtils.mm2px(activity,31F)))
```

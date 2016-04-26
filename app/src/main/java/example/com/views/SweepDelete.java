package example.com.views;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by GEEKER on 2016/4/26.
 */
public class SweepDelete extends ViewGroup {
    private View contenview, right;
    private ViewDragHelper dragHelper;
    private boolean isopened=false;
    private SweepListener sweepListener;

    public SweepDelete(Context context) {
        this(context, null);
    }

    public SweepDelete(Context context, AttributeSet attrs) {
        super(context, attrs);
        dragHelper = ViewDragHelper.create(this, new MyDragCall());
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        contenview = getChildAt(0);
        right = getChildAt(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //设置左侧布局的大小和位置
        contenview.measure(widthMeasureSpec, heightMeasureSpec);

        //设置右侧布局的大小和布局
        int widthspec = MeasureSpec.makeMeasureSpec(right.getLayoutParams().width, MeasureSpec
                .EXACTLY);
        right.measure(widthspec, heightMeasureSpec);
        //设置自己的布局和大小
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize
                (heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        contenview.layout(0, 0, contenview.getMeasuredWidth(), contenview.getMeasuredHeight());
        right.layout(contenview.getMeasuredWidth(), 0, contenview.getMeasuredWidth() + right
                .getMeasuredWidth(), right.getMeasuredHeight());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        dragHelper.processTouchEvent(event);
        return true;
    }

    private class MyDragCall extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return child == contenview || child == right;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            if (child == contenview) {
                if (left > 0) {
                    return 0;
                } else if (left < -right.getMeasuredWidth()) {
                    return -right.getMeasuredWidth();
                }
            } else if (child == right) {
                if (left < contenview.getMeasuredWidth() - right.getMeasuredWidth()) {
                    return contenview.getMeasuredWidth() - right.getMeasuredWidth();
                } else if (left > contenview.getMeasuredWidth()) {
                    return contenview.getMeasuredWidth();
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            if (changedView == contenview) {
                int distance = contenview.getMeasuredWidth() + left;
                right.layout(distance, 0, distance + right.getMeasuredWidth(), right
                        .getMeasuredHeight());
            } else if (changedView == right) {
                int distance = left - contenview.getMeasuredWidth();
                contenview.layout(distance, 0, contenview.getMeasuredWidth() + distance,
                        contenview.getMeasuredHeight());
            }
            ViewCompat.postInvalidateOnAnimation(SweepDelete.this);
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (-contenview.getLeft() > right.getMeasuredWidth() / 2f) {
//                contenview.layout(0, 0, contenview.getMeasuredWidth(), contenview
//                        .getMeasuredHeight());
//                right.layout(contenview.getMeasuredWidth(), 0, contenview.getMeasuredWidth()
//                        + right.getMeasuredWidth(), right.getMeasuredHeight());
                open();
            }else{
                //使用下面的方法实现平滑移动
//                contenview.layout(-right.getMeasuredWidth(), 0, contenview.getMeasuredWidth()
//                        -right.getMeasuredWidth(), contenview.getMeasuredHeight());
//                right.layout(contenview.getMeasuredWidth()-right.getMeasuredWidth(), 0,
//                        contenview.getMeasuredWidth(), right.getMeasuredHeight());

                close();
            }

        }

    }

    public void open() {
        isopened=true;
        if (sweepListener!=null){
            sweepListener.isOpened(this,isopened);
        }
        dragHelper.smoothSlideViewTo(contenview,-right.getMeasuredWidth(), 0);
        dragHelper.smoothSlideViewTo(right,contenview.getMeasuredWidth()-right.getMeasuredWidth(), 0);
        ViewCompat.postInvalidateOnAnimation(SweepDelete.this);
    }

    public void close() {
        isopened=false;
        if (sweepListener!=null){
            sweepListener.isOpened(this,isopened);
        }
        dragHelper.smoothSlideViewTo(contenview,0,0);
        dragHelper.smoothSlideViewTo(right,contenview.getMeasuredWidth(),0);
        ViewCompat.postInvalidateOnAnimation(SweepDelete.this);
    }

    @Override
    public void computeScroll() {
        if (dragHelper.continueSettling(true)){
            postInvalidateOnAnimation();
        }
    }

    //通过接口的方式 实现在MainActivity中监听SweepView的打开和关闭的状态变化 同时做出相应的操作
    public void setSweepListener(SweepListener sweepListener){this.sweepListener=sweepListener;}
    public interface SweepListener{
        abstract void isOpened(SweepDelete sweepDelete,boolean isOpened);
    }
}

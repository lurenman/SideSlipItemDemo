package com.example.administrator.sideslipitemdemo;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * Created by cy on 2017/3/30.
 * 侧滑菜单
 */

public class SideSlipItem extends LinearLayout {
    private static final String TAG = "cy======SideSlipItem";
    private ViewDragHelper helper;
    private View delectView;
    private ViewGroup centerLayout;
    private int measuredWidth;
    private int measuredHeight;
    private int delectWidth;
    private SlideState currentState = SlideState.CLOSE;//默认状态是关闭的
    private SlideState preState = SlideState.CLOSE;//上一次状态
    private OtherItemSlideChnageListener changeListener;
    private GestureDetector gestureDetector;
    private float downX;
    private ItemClickListener itemClickListener;
    private int position;

    public SideSlipItem(Context context) {
        this(context, null);
    }

    public SideSlipItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideSlipItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //（1）创建dragHelper
        helper = ViewDragHelper.create(this, 1.0f, mCallback);
        gestureDetector = new GestureDetector(context, mSimpleOnGestureListener);
    }

    /**
     * 摆放孩子view的位子，让“删除按钮”一开始处于屏幕的右屏幕外面,但是调用这个方法要拿到
     * 孩子布局，所以要调onFinishInflate
     * <p>
     * TODO 说闲话:结局listview跟条目的手势冲突（可以拓展到其他领域）有两种办法，第一种从listview角度
     * TODO 出发，判断要不要拦截事件，从子布局角度触发，要不要请求父布局不要拦截事件
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        centerLayout.layout(0, 0, measuredWidth, measuredHeight);
        delectView.layout(measuredWidth, 0, measuredWidth + delectWidth, measuredHeight);
        mCallback.tryCaptureView(centerLayout, R.id.long_layout);
    }

    /**
     * 布局加载完成之后就会走这个方法
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //拿到删除按钮
        delectView = getChildAt(0);
        //拿到正常显示的那个布局
        centerLayout = (ViewGroup) getChildAt(1);
    }

    public void getFoucous() {
        closeDelete(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        //删除按钮的宽度就是条目横向最大的拖拽范围
        delectWidth = delectView.getMeasuredWidth();
    }

    //(4)重写draghelper的方法来处理事件
    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        //是否捕捉view,返回true代表捕捉，基本都是返回true
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            Logger.i(TAG, "tryCaptureView");
            child.setOnClickListener(mOnClickListener);
            return true;
        }

        //clamp 固定。这个方法是限制view的水平移动位置的
        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            //限制最大拖拽范围
            if (child == centerLayout) {
                //如果手指拖拽的是长的
                if (left < -delectWidth) {
                    left = -delectWidth;
                } else if (left > 0) {
                    left = 0;
                }
            } else {
                if (left < measuredWidth - delectWidth) {
                    left = measuredWidth - delectWidth;
                } else if (left > measuredWidth) {
                    left = measuredWidth;
                }
            }
            return left;
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            if (changedView == centerLayout) {
                //拖动中间那个长的
                //当拖动长的，把长的偏移量给“删除按钮”
                delectView.offsetLeftAndRight(dx);
            } else {
                //拖动的是“删除按钮”
                //拖动“删除按钮”，把偏移量给长的那个view
                //因为设置了删除事件，所以这个侧滑功能其实已经无效了
                centerLayout.offsetLeftAndRight(dx);
            }
            //当另一个条目打开的时候，执行接口回调，关闭上一个条目
            excuteItemChangeListener(centerLayout.getLeft());
            /**
             * 强制重绘，因为下面的那个打开和关闭是指对单一的那个控件进行操作，虽然另一个控件也会进行移动
             * 但是移动的效果不理想，我用小米5测试了，确实不理想，有点坑爹，所以需要强制重绘
             */
            invalidate();
        }

        //松手的时候走这个方法
        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            if (xvel == 0 && centerLayout.getLeft() < -delectWidth * 0.5f) {
                //直接在没有移动速度的情况下松手并且长view的位置靠左超过最大范围一半（也就是“删除按钮出现了一半以上”）
                //TODO 打开
                openDelete();
            } else if (xvel < 0) {
                //速度小于0，松手的时候是从右向左滑动
                // TODO 打开
                openDelete();
            } else {
                //TODO 关闭
                closeDelete(true);
            }
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }
    };

    private void excuteItemChangeListener(int left) {
        preState = currentState;//保存上一次状态
        currentState = updateCurrentState(left);//更新当前状态
        if (changeListener != null) {
            if (currentState == SlideState.OPEN && preState != currentState) {
                changeListener.openItem(this);
            } else if (currentState == SlideState.CLOSE && preState != currentState) {
                changeListener.closeItem(this);
            }
        }
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface ItemClickListener {
        void clickItem(int position);

//        void delteItem();
    }

    private SlideState updateCurrentState(int left) {
        if (left == -delectWidth) {
            //打开
            return SlideState.OPEN;
        } else if (left == 0) {
            return SlideState.CLOSE;
        }
        return SlideState.SLIDDING;
    }

    public void closeDelete(boolean isNeedAnimation) {
        if (isNeedAnimation) {
            //需要关闭动画
            if (helper.smoothSlideViewTo(centerLayout, 0, 0)) {
                //因为滑动的过程是一个持续改变的界面的过程，所以要一直重绘界面
                ViewCompat.postInvalidateOnAnimation(this);
            }
        } else {
            centerLayout.layout(0, 0, measuredWidth, measuredHeight);
            delectView.layout(measuredWidth, 0, measuredWidth + delectWidth, measuredHeight);
            currentState = SlideState.CLOSE;
            if (changeListener != null)
                changeListener.closeItem(this);
        }
    }

    private void openDelete() {
        //TODO 屏幕的左上角是(0,0)
        //参数一：滑动谁（因为在onViewPositionChanged 里面做了绑定，所以滑动谁另一个都会动）
        //参数二：最终的位置（最终里屏幕左边的位置）
        //参数三：最终高度
        if (helper.smoothSlideViewTo(centerLayout, -delectWidth, 0)) {
            //因为滑动的过程是一个持续改变的界面的过程，所以要一直重绘界面
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    //这个是重绘，但是看不懂播客上是怎么表示的
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (helper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    /**
     * (2)决定拦不拦截事件
     *
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        //让viewDragViewHelper来决定是否拦截事件
        return helper.shouldInterceptTouchEvent(motionEvent);
    }


    /**
     * (3)在拦截的基础上处理事件,返回值为true代表处理这个事件
     *
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
//        return super.onTouchEvent(event);//原生
        gestureDetector.onTouchEvent(motionEvent);
        helper.processTouchEvent(motionEvent);
        Logger.i(TAG, "触摸了");
        return true;
    }


    /**
     * 当其他条目打开的时候的一个监听，关闭上一个打开的条目
     */
    public interface OtherItemSlideChnageListener {
        void openItem(SideSlipItem item);

        void closeItem(SideSlipItem item);
    }

    /**
     * 用枚举来表示集中状态
     */
    public enum SlideState {
        OPEN, CLOSE, SLIDDING
    }

    public void setChangeListener(OtherItemSlideChnageListener changeListener) {
        this.changeListener = changeListener;
    }

    private GestureDetector.SimpleOnGestureListener mSimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if (Math.abs(distanceX) > Math.abs(distanceY)) {
                //水平移动距离大于垂直移动距离，那么就请求父布局不要拦截触摸事件
                requestDisallowInterceptTouchEvent(true);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    };

    private View.OnClickListener mOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.long_layout:
                    if (currentState == SlideState.OPEN) {
                        closeDelete(true);
                    } else {
                        if (itemClickListener != null)
                            itemClickListener.clickItem(position);
                    }
                    break;
                case R.id.tv_swipe_delete:
//                    if (currentState == SlideState.OPEN) {
//                        closeDelete(true);
//                    }
//                    if (itemClickListener != null)
//                        itemClickListener.delteItem();
                    break;
            }
        }
    };
}

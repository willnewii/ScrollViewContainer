package scrollviewcontainer.will.com.library;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.AbsListView;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;


public class ScrollViewContainer extends RelativeLayout {

    /**
     * 自动上滑
     */
    public static final int AUTO_UP = 0;
    /**
     * 自动下滑
     */
    public static final int AUTO_DOWN = 1;
    /**
     * 动画完成
     */
    public static final int DONE = 2;
    /**
     * 动画速度
     */
    public static final float SPEED = 6.5f;

    private boolean isMeasured = false;

    /**
     * 用于计算手滑动的速度
     */
    private VelocityTracker vt;

    private int mViewHeight;
    private int mViewWidth;

    private View topView;
    private View bottomView;

    //是否滑动到ScrollViewContainer的顶部,false,只是滑动到bottomListenerView 的顶部
    private boolean isGoTop = true;
    private View goTopView;

    private View topListenerView;
    private View bottomListenerView;

    public boolean canPullDown;
    private boolean canPullUp;
    private int state = DONE;

    /**
     * 记录当前展示的是哪个view，0是topView，1是bottomView
     */
    private int mCurrentViewIndex = 0;
    /**
     * 手滑动距离，这个是控制布局的主要变量
     */
    private float mMoveLen;
    private MyTimer mTimer;
    private float mLastY;
    /**
     * 用于控制是否变动布局的另一个条件，mEvents==0时布局可以拖拽了，mEvents==-1时可以舍弃将要到来的第一个move事件，
     * 这点是去除多点拖动剧变的关键
     */
    private int mEvents;

    public ScrollViewContainer(Context context) {
        super(context);
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollViewContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mTimer = new MyTimer(handler);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (vt == null)
                    vt = VelocityTracker.obtain();
                else
                    vt.clear();
                mLastY = ev.getY();
                try {
                    vt.addMovement(ev);
                } catch (Exception e) {

                }
                mEvents = 0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_POINTER_UP:
                // 多一只手指按下或抬起时舍弃将要到来的第一个事件move，防止多点拖拽的bug
                mEvents = -1;
                break;
            case MotionEvent.ACTION_MOVE:
                vt.addMovement(ev);
                if (canPullUp && mCurrentViewIndex == 0 && mEvents == 0) {
                    mMoveLen += (ev.getY() - mLastY);
                    // 防止上下越界
                    if (mMoveLen > 0) {
                        mMoveLen = 0;
                        mCurrentViewIndex = 0;
                    } else if (mMoveLen < -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        mCurrentViewIndex = 1;

                    }
                    if (mMoveLen < -8) {
                        // 防止事件冲突
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else if (canPullDown && mCurrentViewIndex == 1 && mEvents == 0) {
                    mMoveLen += (ev.getY() - mLastY);
                    // 防止上下越界
                    if (mMoveLen < -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        mCurrentViewIndex = 1;
                    } else if (mMoveLen > 0) {
                        mMoveLen = 0;
                        mCurrentViewIndex = 0;
                    }
                    if (mMoveLen > 8 - mViewHeight) {
                        // 防止事件冲突
                        ev.setAction(MotionEvent.ACTION_CANCEL);
                    }
                } else
                    mEvents++;
                mLastY = ev.getY();
                requestLayout();
                break;
            case MotionEvent.ACTION_UP:
                mLastY = ev.getY();
                vt.addMovement(ev);
                vt.computeCurrentVelocity(700);
                // 获取Y方向的速度
                float mYV = vt.getYVelocity();
                if (mMoveLen == 0 || mMoveLen == -mViewHeight)
                    break;
                if (Math.abs(mYV) < 500) {
                    //速度小于一定值的时候当作静止释放，这时候两个View往哪移动取决于滑动的距离
                    if (mMoveLen <= -mViewHeight / 2) {
                        state = AUTO_UP;
                    } else if (mMoveLen > -mViewHeight / 2) {
                        state = AUTO_DOWN;
                    }
                } else {
                    // 抬起手指时速度方向决定两个View往哪移动
                    if (mYV < 0)
                        state = AUTO_UP;
                    else
                        state = AUTO_DOWN;
                }
                mTimer.schedule(2);
                try {
                    //vt.recycle();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

        }
        super.dispatchTouchEvent(ev);
        return true;
    }

    /**
     * 确定topView和bottomView 位置/高度
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        topView.layout(0, (int) mMoveLen, mViewWidth, topView.getMeasuredHeight() + (int) mMoveLen);

        bottomView.layout(0, topView.getMeasuredHeight() + (int) mMoveLen,
                mViewWidth, topView.getMeasuredHeight() + (int) mMoveLen
                        + bottomView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isMeasured) {
            isMeasured = true;

            mViewHeight = getMeasuredHeight();
            mViewWidth = getMeasuredWidth();

            topView = getChildAt(0);

            bottomView = getChildAt(1);

        }
    }

    /**
     * 指定监听滑动的View
     *
     * @param v
     */
    public void setTopViewListener(View v) {
        topListenerView = v;
        topListenerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (v instanceof MyScrollView) {
                    MyScrollView sv = (MyScrollView) v;
                    if (sv.isScrolltoBottom() && mCurrentViewIndex == 0)
                        canPullUp = true;
                    else if (sv.isNotFill()) // 不满一屏的情况
                        canPullUp = true;
                    else
                        canPullUp = false;
                } else {
                    canPullUp = true;
                }
                return false;
            }
        });
    }

    /**
     * 指定监听滑动的View
     */
    public void setBottomViewListener(View v) {
        if (v instanceof MyScrollView) {
            //当ScrollView 高度不足一屏幕
            if (((MyScrollView)v).isNotFill()) {
                canPullDown = true;
            }
            ((MyScrollView) v).setOnScrollChangedListener(new MyScrollView.ScrollViewListener() {
                @Override
                public void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy) {
                    if (y == 0 && mCurrentViewIndex == 1) {
                        canPullDown = true;
                        if (isGoTop) {
                            goContainerUp();
                        }
                    } else {
                        canPullDown = false;
                    }
                }
            });
        } else if (v instanceof AbsListView) {
            ((AbsListView) v).setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    switch (scrollState) {
                        case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                            if (view.getChildCount() > 0) {
                                if (view.getChildAt(0).getTop() == 0 && mCurrentViewIndex == 1) {
                                    canPullDown = true;
                                    if (isGoTop) {
                                        goContainerUp();
                                    }
                                } else {
                                    canPullDown = false;

                                }
                            } else {
                                canPullDown = true;
                            }
                            break;
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

                }
            });
        }
        bottomListenerView = v;
        /*bottomListenerView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                 {
                    if (v.getScrollY() == 0 && mCurrentViewIndex == 1) {
                        canPullDown = true;
                    } else {
                        canPullDown = false;
                    }
                }
                return false;
            }
        });*/
    }

    /**
     * 设置回到顶部的View
     *
     * @param mView
     */
    public void setgoTopListener(View mView) {

        if (mView == null) {
            return;
        }

        if (mCurrentViewIndex != 1) {
            mView.setVisibility(View.INVISIBLE);
        }

        mView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomListenerView instanceof AbsListView) {
                    ((AbsListView) bottomListenerView).smoothScrollToPosition(0);
                        isGoTop = true;
                } else if (bottomListenerView instanceof MyScrollView) {
                    ((MyScrollView) bottomListenerView).smoothScrollTo(0, 0);
                        isGoTop = true;
                }
            }
        });
        goTopView = mView;
    }

    public void goContainerUp() {
        state = AUTO_DOWN;
        mTimer.schedule(3);
        isGoTop = false;
    }


    class MyTimer {
        private Handler handler;
        private Timer timer;
        private MyTask mTask;

        public MyTimer(Handler handler) {
            this.handler = handler;
            timer = new Timer();
        }

        public void schedule(long period) {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
            mTask = new MyTask(handler);
            timer.schedule(mTask, 0, period);
        }

        public void cancel() {
            if (mTask != null) {
                mTask.cancel();
                mTask = null;
            }
        }

        class MyTask extends TimerTask {
            private Handler handler;

            public MyTask(Handler handler) {
                this.handler = handler;
            }

            @Override
            public void run() {
                handler.obtainMessage().sendToTarget();
            }

        }

    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (mMoveLen != 0) {
                if (state == AUTO_UP) {
                    mMoveLen -= SPEED;
                    if (mMoveLen <= -mViewHeight) {
                        mMoveLen = -mViewHeight;
                        state = DONE;
                        mCurrentViewIndex = 1;
                        if (goTopView != null) {
                            goTopView.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (state == AUTO_DOWN) {
                    mMoveLen += SPEED;
                    if (mMoveLen >= 0) {
                        mMoveLen = 0;
                        state = DONE;
                        mCurrentViewIndex = 0;
                        if (goTopView != null) {
                            goTopView.setVisibility(View.INVISIBLE);
                        }
                    }
                } else {
                    mTimer.cancel();
                }
            }
            requestLayout();
        }

    };

}

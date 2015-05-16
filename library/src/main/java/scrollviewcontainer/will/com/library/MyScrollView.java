package scrollviewcontainer.will.com.library;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 添加滑动监听事件
 * Created by zhangweiwei on 15/5/16.
 */
public class MyScrollView extends ScrollView {

    private ScrollViewListener mScrollViewListener;

    public MyScrollView(Context context) {
        super(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void setOnScrollChangedListener(ScrollViewListener scrollViewListener) {
        this.mScrollViewListener = scrollViewListener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (mScrollViewListener != null) {
            mScrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    public interface ScrollViewListener {

        void onScrollChanged(MyScrollView scrollView, int x, int y, int oldx, int oldy);

    }

    /**
     * 是否滑动到底部
     * @return
     */
    public boolean isScrolltoBottom(){
        if(getScrollY() == (getChildAt(0).getMeasuredHeight() - getMeasuredHeight())){
            return true ;
        }else{
            return false ;
        }
    }

    /**
     * ScrollView 内容不足一屏幕
     * @return
     */
    public boolean isNotFill(){
        if (getChildAt(0).getMeasuredHeight() <= getMeasuredHeight()) {
            return true ;
        }
        return false ;
    }

}
//getScrollY() 滚动距离

//getMeasuredHeight() scrollView的高度

//getChildAt(0).getMeasuredHeight() scrollView的内容高度



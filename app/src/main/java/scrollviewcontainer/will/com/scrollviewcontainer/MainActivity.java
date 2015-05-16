package scrollviewcontainer.will.com.scrollviewcontainer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import scrollviewcontainer.will.com.library.ScrollViewContainer;


public class MainActivity extends AppCompatActivity {

    private ScrollViewContainer mContainer;

    private View mTopView;
    private View mBottomView;

    private View goTop ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainer = (ScrollViewContainer) findViewById(R.id.mContainer);

        mTopView = findViewById(R.id.top);
        mBottomView = findViewById(R.id.bottom);

        /**
         * 实际开发中,ScrollViewContainer中会是两个很复杂的View
         * 所以,你可以通过下面的方法绑定mTopView/mBottomView 相应你要监听滑动的子View
         * 如果直接传入mTopView/mBottomView 会是整体滑动
         *
         * 目前支持  absListView MyScrollView(就是ScrollView)
         */
        mContainer.setTopViewListener(findViewById(R.id.top_scroll));
        mContainer.setBottomViewListener(findViewById(R.id.bottom_scroll));

        mContainer.setgoTopListener(findViewById(R.id.goTop));
    }


}

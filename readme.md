##Android ScrollViewContainer

-----

ScrollViewContainer,仿照手机淘宝商品详情“继续拖动,查看图文详情”的控件。

基本实现是定义一个Layout管理两个View,我们称之为TopView和BottomView. 当TopView滑动到底部之后,再次向上滑动就进入BottomView.反之,当BottomView滑动到顶部之后,再次向下滑动,就进入TopView.

代码修改自[陈靖_-Android自定义控件实战——仿淘宝商品浏览界面](http://blog.csdn.net/zhongkejingwang/article/details/38656929)这位大神,核心代码都是他的,我只是增加了一些扩展,现在你可以放入更复杂的布局,不局限于ScrollView.还有类似淘宝滑动到顶部的功能.

目前支持 absListView 和 ScrollView ,不过ScrollView需要用到library里面的MyScrollView.

---

###Demo

![NumberProgressBar](http://ww3.sinaimg.cn/mw690/610dc034jw1efyrd8n7i7g20cz02mq5f.gif)


[Download Demo](https://github.com/daimajia/NumberProgressBar/releases/download/v1.0/NumberProgressBar-Demo-v1.0.apk)


###Usage
----

乖乖下载代码吧...

首先创建一个布局,在实际项目开发中,topView,bottomView会是一个很复杂的View,所以我习惯用include,看着干净;-)

```java
<scrollviewcontainer.will.com.library.ScrollViewContainer
        android:id="@+id/mContainer"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <LinearLayout
            android:id="@+id/top"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:orientation="vertical">

            <scrollviewcontainer.will.com.library.MyScrollView
                android:id="@+id/top_scroll"
                android:layout_width="match_parent"
                android:layout_height="0dp"
                android:layout_weight="1"
                android:orientation="vertical">

                <LinearLayout
                    android:layout_width="match_parent"
                    android:layout_height="match_parent"
                    android:orientation="vertical">

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="100dp"
                        android:text="测试一下" />

                    <TextView
                        android:layout_width="match_parent"
                        android:layout_height="100dp"
                        android:text="测试一下" />


                </LinearLayout>
            </scrollviewcontainer.will.com.library.MyScrollView>

            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:gravity="center"
                android:padding="10dp"
                android:text="下滑查看更多" />
        </LinearLayout>

        <include layout="@layout/incloud_bottom" />
    </scrollviewcontainer.will.com.library.ScrollViewContainer>

```
然后在Activity中设置你需要绑定的监听滑动的View

```java

        mContainer = (ScrollViewContainer) findViewById(R.id.mContainer);

        mTopView = findViewById(R.id.top);
        mBottomView = findViewById(R.id.bottom);

        mContainer.setTopViewListener(findViewById(R.id.top_scroll));
        mContainer.setBottomViewListener(findViewById(R.id.bottom_scroll));
```

### About me:
用github好久了,一直在like别人的project.今年准备多写写代码~  多open.  有没人有就不知道了...


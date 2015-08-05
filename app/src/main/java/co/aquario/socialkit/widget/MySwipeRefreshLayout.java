package co.aquario.socialkit.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import co.aquario.socialkit.R;

/**
 * Created by Mac on 8/4/15.
 */
public class MySwipeRefreshLayout extends SwipeRefreshLayout {
    public MySwipeRefreshLayout(Context context) {
        super(context);
        init();
    }

    public MySwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setColorSchemeResources(R.color.red_error, R.color.blue_normal, R.color.green_complete);
        //setProgressBackgroundColorSchemeResource(R.color.md_pink_100);
        setSize(LARGE);
    }

    // สั่งให้เริ่มงาน
    public void startRefresh() {
        setRefreshing(true);
    }

    // สั่งให้หยุดทำงาน
    public void refreshComplete() {
        setRefreshing(false);
    }

}

package co.aquario.socialkit.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.ksoichiro.android.observablescrollview.CacheFragmentStatePagerAdapter;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.nineoldandroids.view.ViewHelper;
import com.squareup.picasso.Picasso;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.FeedFragment;
import co.aquario.socialkit.fragment.FriendFragment;
import co.aquario.socialkit.fragment.ViewPagerFragment;
import co.aquario.socialkit.util.EndpointManager;
import co.aquario.socialkit.util.PrefManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainTimelineActivity extends ActionBarActivity implements ObservableScrollViewCallbacks {

    private static final float MAX_TEXT_SCALE_DELTA = 0.3f;

    private View mImageView;
    private View mOverlayView;
    private CircleImageView mProfileImageView;
    private TextView mTitleView;
    private TouchInterceptionFrameLayout mInterceptionLayout;

    private int mSlop;
    private int mFlexibleSpaceHeight;
    private int mTabHeight;
    private boolean mScrolled;

    private Toolbar toolbar;
    private VideoView videoview;

    private Activity activity;
    private Drawer.Result result;

    ViewPagerFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_timeline);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        activity = this;

        setSupportActionBar(toolbar);

        ViewCompat.setElevation(findViewById(R.id.header), getResources().getDimension(R.dimen.toolbar_elevation));

        mImageView = findViewById(R.id.image);
        mOverlayView = findViewById(R.id.overlay);
        // Padding for ViewPager must be set outside the ViewPager itself
        // because with padding, EdgeEffect of ViewPager become strange.
        mFlexibleSpaceHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mTabHeight = getResources().getDimensionPixelSize(R.dimen.tab_height);
        findViewById(R.id.pager_wrapper).setPadding(0, mFlexibleSpaceHeight, 0, 0);
        mTitleView = (TextView) findViewById(R.id.title);
        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);

        PrefManager pref = MainApplication.get(this).getPrefManager();

        String avatarUrl = pref.avatar().getOr("");
        String coverUrl = pref.cover().getOr("");
        String name = pref.name().getOr("");

        setTitle(name);
        //mTitleView.setText(name);
        Picasso.with(this).load(EndpointManager.getPath(coverUrl)).into((ImageView) mImageView);
        Picasso.with(this).load(EndpointManager.getPath(avatarUrl)).into(mProfileImageView);


        ViewConfiguration vc = ViewConfiguration.get(this);
        mSlop = vc.getScaledTouchSlop();
        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.container);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                updateFlexibleSpace();
            }
        });

        fragment = new ViewPagerFragment();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.pager_wrapper, fragment);
        transaction.commit();
    }

    protected int getActionBarSize() {
        TypedValue typedValue = new TypedValue();
        int[] textSizeAttr = new int[]{R.attr.actionBarSize};
        int indexOfAttrTextSize = 0;
        TypedArray a = obtainStyledAttributes(typedValue.data, textSizeAttr);
        int actionBarSize = a.getDimensionPixelSize(indexOfAttrTextSize, -1);
        a.recycle();
        return actionBarSize;
    }

    protected int getScreenHeight() {
        return findViewById(android.R.id.content).getHeight();
    }


    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

    private TouchInterceptionFrameLayout.TouchInterceptionListener mInterceptionListener = new TouchInterceptionFrameLayout.TouchInterceptionListener() {
        @Override
        public boolean shouldInterceptTouchEvent(MotionEvent ev, boolean moving, float diffX, float diffY) {
            if (!mScrolled && mSlop < Math.abs(diffX) && Math.abs(diffY) < Math.abs(diffX)) {
                // Horizontal scroll is maybe handled by ViewPager
                return false;
            }

            Scrollable scrollable = getCurrentScrollable();
            if (scrollable == null) {
                mScrolled = false;
                return false;
            }

            // If interceptionLayout can move, it should intercept.
            // And once it begins to move, horizontal scroll shouldn't work any longer.
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            int translationY = (int) ViewHelper.getTranslationY(mInterceptionLayout);
            boolean scrollingUp = 0 < diffY;
            boolean scrollingDown = diffY < 0;
            if (scrollingUp) {
                if (translationY < 0) {
                    mScrolled = true;
                    return true;
                }
            } else if (scrollingDown) {
                if (-flexibleSpace < translationY) {
                    mScrolled = true;
                    return true;
                }
            }
            mScrolled = false;
            return false;
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            int flexibleSpace = mFlexibleSpaceHeight - mTabHeight;
            float translationY = ScrollUtils.getFloat(ViewHelper.getTranslationY(mInterceptionLayout) + diffY, -flexibleSpace, 0);
            updateFlexibleSpace(translationY);
            if (translationY < 0) {
                FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
                lp.height = (int) (-translationY + getScreenHeight());
                mInterceptionLayout.requestLayout();
            }
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            mScrolled = false;
        }
    };

    private Scrollable getCurrentScrollable() {
        Fragment fragment = getCurrentFragment();
        if (fragment == null) {
            return null;
        }
        View view = fragment.getView();
        if (view == null) {
            return null;
        }
        return (Scrollable) view.findViewById(R.id.scroll);
    }

    private void updateFlexibleSpace() {
        updateFlexibleSpace(ViewHelper.getTranslationY(mInterceptionLayout));
    }

    private void updateFlexibleSpace(float translationY) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);
        int minOverlayTransitionY = getActionBarSize() - mOverlayView.getHeight();
        ViewHelper.setTranslationY(mImageView, ScrollUtils.getFloat(-translationY / 2, minOverlayTransitionY, 0));

        // Change alpha of overlay
        float flexibleRange = mFlexibleSpaceHeight - getActionBarSize();
        //ViewHelper.setAlpha(mOverlayView, ScrollUtils.getFloat(-translationY / flexibleRange, 0, 1));
        //ViewHelper.setAlpha(mOverlayView, 1.0f);

        // Scale title text
        float scale = 1 + ScrollUtils.getFloat((flexibleRange + translationY - mTabHeight) / flexibleRange, 0, MAX_TEXT_SCALE_DELTA);
        setPivotXToTitle();
        ViewHelper.setPivotY(mTitleView, 0);
        ViewHelper.setScaleX(mTitleView, scale);
        ViewHelper.setScaleY(mTitleView, scale);
    }

    private Fragment getCurrentFragment() {
        return fragment;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void setPivotXToTitle() {
        Configuration config = getResources().getConfiguration();
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 <= Build.VERSION.SDK_INT
                && config.getLayoutDirection() == View.LAYOUT_DIRECTION_RTL) {
            ViewHelper.setPivotX(mTitleView, findViewById(android.R.id.content).getWidth());
        } else {
            ViewHelper.setPivotX(mTitleView, 0);
        }
    }

    /**
     * This adapter provides two types of fragments as an example.
     * {@linkplain #createItem(int)} should be modified if you use this example for your app.
     */
    private static class NavigationAdapter extends CacheFragmentStatePagerAdapter {


        private static final String[] TITLES = new String[]{"12 Posts", "24 Follower", "25 Following", "15 Friends"
        //        , "12 Loves", "5 Groups"
        };

        public NavigationAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        protected Fragment createItem(int position) {
            //Fragment f;
            final int pattern = position % 5;
            switch (pattern) {
                case 0:
                    return FeedFragment.newInstance("");
                case 1:
                    return FriendFragment.newInstance("FOLLOWER", "6");
                case 2:
                    return FriendFragment.newInstance("FOLLOWING", "6");
                case 3:
                    return FriendFragment.newInstance("FRIEND", "6");
                /*
                case 4:
                    return MainFragment.newInstance("");
                case 5:
                    return MainFragment.newInstance("");
                    */
                default:
                    return FeedFragment.newInstance("");
            }
            //return f;
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }
    }
}

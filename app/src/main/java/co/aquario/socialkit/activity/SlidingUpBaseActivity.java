/*
 * Copyright 2014 Soichiro Kashima
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package co.aquario.socialkit.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.AQUtility;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.github.ksoichiro.android.observablescrollview.Scrollable;
import com.github.ksoichiro.android.observablescrollview.TouchInterceptionFrameLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.MainApplication;
import co.aquario.socialkit.R;
import co.aquario.socialkit.event.LoadTimelineEvent;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Live;
import co.aquario.socialkit.util.PrefManager;
import de.hdodenhof.circleimageview.CircleImageView;

public abstract class SlidingUpBaseActivity<S extends Scrollable> extends BaseActivity implements ObservableScrollViewCallbacks {

    private static final String STATE_SLIDING_STATE = "slidingState";
    private static final int SLIDING_STATE_TOP = 0;
    private static final int SLIDING_STATE_MIDDLE = 1;
    private static final int SLIDING_STATE_BOTTOM = 2;

    private View mHeader;
    private View mHeaderBar;
    private View mHeaderOverlay;
    private View mHeaderFlexibleSpace;
    private TextView mTitle;
    private TextView mSubTitle;
    private TextView mToolbarTitle;
    private View mImageView;
    private View mFab;
    private Toolbar mToolbar;
    private S mScrollable;
    private TouchInterceptionFrameLayout mInterceptionLayout;
    private CircleImageView mProfileImageView;

    // Fields that just keep constants like resource values
    private int mActionBarSize;
    private int mIntersectionHeight;
    private int mHeaderBarHeight;
    private int mSlidingSlop;
    private int mSlidingHeaderBlueSize;
    private int mColorPrimary;
    private int mFlexibleSpaceImageHeight;
    private int mToolbarColor;
    private int mFabMargin;

    // Fields that needs to saved
    private int mSlidingState;

    // Temporary states
    private boolean mFabIsShown;
    private boolean mMoved;
    private float mInitialY;
    private float mMovedDistanceY;
    private float mScrollYOnDownMotion;

    // These flags are used for changing header colors.
    private boolean mHeaderColorIsChanging;
    private boolean mHeaderColorChangedToBottom;
    private boolean mHeaderIsAtBottom;
    private boolean mHeaderIsNotAtBottom;

    private Activity mActivity;
    private Drawer.Result result;

    ArrayList<Live> artistList = new ArrayList<Live>();

    public static SparseArray<Bitmap> photoCache = new SparseArray<>(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mActivity = this;



        setSupportActionBar(mToolbar);
        ViewHelper.setScaleY(mToolbar, 0);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToolbarColor = getResources().getColor(R.color.primary);
        mToolbar.setBackgroundColor(Color.TRANSPARENT);

        mFlexibleSpaceImageHeight = getResources().getDimensionPixelSize(R.dimen.flexible_space_image_height);
        mIntersectionHeight = getResources().getDimensionPixelSize(R.dimen.intersection_height);
        mHeaderBarHeight = getResources().getDimensionPixelSize(R.dimen.header_bar_height);
        mSlidingSlop = getResources().getDimensionPixelSize(R.dimen.sliding_slop);
        mActionBarSize = getActionBarSize();
        mColorPrimary = getResources().getColor(R.color.primary);
        mSlidingHeaderBlueSize = getResources().getDimensionPixelSize(R.dimen.sliding_overlay_blur_size);

        mHeader = findViewById(R.id.header);
        mHeaderBar = findViewById(R.id.header_bar);
        mHeaderOverlay = findViewById(R.id.header_overlay);
        mHeaderFlexibleSpace = findViewById(R.id.header_flexible_space);
        mImageView = findViewById(R.id.image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideOnClick();
            }
        });
        mProfileImageView = (CircleImageView) findViewById(R.id.profile_image);
        mProfileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideOnClick();
            }
        });

        mScrollable = createScrollable();

        mFab = findViewById(R.id.fab);
        mFabMargin = getResources().getDimensionPixelSize(R.dimen.margin_standard);

        mInterceptionLayout = (TouchInterceptionFrameLayout) findViewById(R.id.scroll_wrapper);
        mInterceptionLayout.setScrollInterceptionListener(mInterceptionListener);
        mTitle = (TextView) findViewById(R.id.title);
        mSubTitle = (TextView) findViewById(R.id.sub_title);
        //mTitle.setText(getTitle());
        mToolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        mToolbarTitle.setText(mTitle.getText());

        ViewHelper.setAlpha(mToolbarTitle, 0);
        ViewHelper.setTranslationY(mTitle, (mHeaderBarHeight - mActionBarSize) / 2);

        if (savedInstanceState == null) {
            mSlidingState = SLIDING_STATE_BOTTOM;
        }

        ScrollUtils.addOnGlobalLayoutListener(mInterceptionLayout, new Runnable() {
            @Override
            public void run() {
                if (mFab != null) {
                    ViewHelper.setTranslationX(mFab, mTitle.getWidth() - mFabMargin - mFab.getWidth());
                    ViewHelper.setTranslationY(mFab, ViewHelper.getX(mTitle) - (mFab.getHeight() / 2));
                }
                changeSlidingState(mSlidingState, false);
            }
        });

        PrefManager pref = MainApplication.get(this).getPrefManager();



        String userId = getIntent().getExtras().getString("userId");
        String avatarUrl = getIntent().getExtras().getString("avatar");
        String coverUrl = getIntent().getExtras().getString("cover");
        String name = getIntent().getExtras().getString("name");
        String username = getIntent().getExtras().getString("username");

        if(userId == null) {
            userId = pref.userId().getOr("6");
        }

        if(name == null) {
            name = pref.name().getOr("");
        }

        if(username == null) {
            username = pref.username().getOr("");
        }

        if(avatarUrl == null) {
            avatarUrl = pref.avatar().getOr("");
        }

        if(coverUrl == null) {
            coverUrl = pref.cover().getOr("");
        }

        Log.e("userIdProfilePage",userId);


        mToolbar.setTitle(name + "");
        setTitle(name + "");
        mTitle.setText(name + "");
        mSubTitle.setText("@"+ username + "");

        Picasso.with(this).load(coverUrl).into((ImageView) mImageView);
        Picasso.with(this).load(avatarUrl).into(mProfileImageView);

        AQuery aq = new AQuery(this);
        String url = "http://api.vdomax.com/live/history" + userId;

        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setInverseBackgroundForced(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setTitle("Downloading live history");
        dialog.setMessage("กำลังดาวน์โหลดประวัติการถ่ายทอดสด..");
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setMax(100);

        aq.progress(dialog).ajax(url, JSONObject.class, this, "getJson");

        ApiBus.getInstance().post(new LoadTimelineEvent(Integer.parseInt(userId),"",1,50,false));
        Log.e("FIREFIRE","FIRE");

        changeSlidingState(SLIDING_STATE_MIDDLE, true);

    }

    public void getJson(String url, JSONObject jo, AjaxStatus status)
            throws JSONException {
        AQUtility.debug("jo", jo);
        if (jo != null) {
            String nameLive;
            JSONArray ja = jo.getJSONArray("history");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject obj = ja.optJSONObject(i);

                nameLive = obj.optString("name");
                String userId = obj.getString("user_id");
                String urlLive = obj.optString("url");
                String photoLive = obj.optString("thumb");
                long timestamp = obj.optLong("date");

                JSONObject media = obj.optJSONObject("duration");

                String hours = media.optString("hours");
                String minutes = media.optString("minutes");
                String seconds = media.optString("seconds");

                Live liveList = new Live(urlLive,photoLive,nameLive,hours,minutes,seconds,Long.toString(timestamp),null,null,null);
                artistList.add(liveList);

            }

            /*
            ViewPagerFragment fragment = new ViewPagerFragment();
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.sub_container, fragment);

            FragmentTransactionExtended fragmentTransactionExtended = new FragmentTransactionExtended(this, transaction, mainFragment, fragment, R.id.sub_container);
            fragmentTransactionExtended.addTransition(FragmentTransactionExtended.GLIDE);
            fragmentTransactionExtended.commit();

            transaction.commit();
            */


            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.list_container,
                            ListFragment.newInstance(artistList)).commit();



            //activityLiveHistory.notifyDataSetChanged();
            AQUtility.debug("done");

        } else {
            AQUtility.debug("error!");
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // All the related temporary states can be restored by slidingState
        mSlidingState = savedInstanceState.getInt(STATE_SLIDING_STATE);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_SLIDING_STATE, mSlidingState);
        super.onSaveInstanceState(outState);
    }

    protected abstract int getLayoutResId();

    protected abstract S createScrollable();

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
            final int minInterceptionLayoutY = -mIntersectionHeight;
            return minInterceptionLayoutY < (int) ViewHelper.getY(mInterceptionLayout)
                    || (moving && mScrollable.getCurrentScrollY() - diffY < 0);
        }

        @Override
        public void onDownMotionEvent(MotionEvent ev) {
            mScrollYOnDownMotion = mScrollable.getCurrentScrollY();
            mInitialY = ViewHelper.getTranslationY(mInterceptionLayout);
        }

        @Override
        public void onMoveMotionEvent(MotionEvent ev, float diffX, float diffY) {
            mMoved = true;
            float translationY = ViewHelper.getTranslationY(mInterceptionLayout) - mScrollYOnDownMotion + diffY;
            if (translationY < -mIntersectionHeight) {
                translationY = -mIntersectionHeight;
            } else if (getScreenHeight() - mHeaderBarHeight < translationY) {
                translationY = getScreenHeight() - mHeaderBarHeight;
            }

            slideTo(translationY, true);

            mMovedDistanceY = ViewHelper.getTranslationY(mInterceptionLayout) - mInitialY;
        }

        @Override
        public void onUpOrCancelMotionEvent(MotionEvent ev) {
            if (!mMoved) {
                // Invoke slide animation only on header view
                Rect outRect = new Rect();
                mHeader.getHitRect(outRect);
                if (outRect.contains((int) ev.getX(), (int) ev.getY())) {
                    slideOnClick();
                }
            } else {
                stickToAnchors();
            }
            mMoved = false;
        }
    };

    private void changeSlidingState(final int slidingState, boolean animated) {
        mSlidingState = slidingState;
        float translationY = 0;
        switch (slidingState) {
            case SLIDING_STATE_TOP:
                translationY = 0;
                break;
            case SLIDING_STATE_MIDDLE:
                translationY = getAnchorYImage();
                break;
            case SLIDING_STATE_BOTTOM:
                translationY = getAnchorYBottom();
                break;
        }
        if (animated) {
            slideWithAnimation(translationY);
        } else {
            slideTo(translationY, false);
        }
    }

    private void slideOnClick() {
        float translationY = ViewHelper.getTranslationY(mInterceptionLayout);
        if (translationY == getAnchorYBottom()) {
            changeSlidingState(SLIDING_STATE_MIDDLE, true);
        } else if (translationY == getAnchorYImage()) {
            changeSlidingState(SLIDING_STATE_BOTTOM, true);
        }
    }

    private void stickToAnchors() {
        // Slide to some points automatically
        if (0 < mMovedDistanceY) {
            // Sliding down
            if (mSlidingSlop < mMovedDistanceY) {
                // Sliding down to an anchor
                if (getAnchorYImage() < ViewHelper.getTranslationY(mInterceptionLayout)) {
                    changeSlidingState(SLIDING_STATE_BOTTOM, true);
                } else {
                    changeSlidingState(SLIDING_STATE_MIDDLE, true);
                }
            } else {
                // Sliding up(back) to an anchor
                if (getAnchorYImage() < ViewHelper.getTranslationY(mInterceptionLayout)) {
                    changeSlidingState(SLIDING_STATE_MIDDLE, true);
                } else {
                    changeSlidingState(SLIDING_STATE_TOP, true);
                }
            }
        } else if (mMovedDistanceY < 0) {
            // Sliding up
            if (mMovedDistanceY < -mSlidingSlop) {
                // Sliding up to an anchor
                if (getAnchorYImage() < ViewHelper.getTranslationY(mInterceptionLayout)) {
                    changeSlidingState(SLIDING_STATE_MIDDLE, true);
                } else {
                    changeSlidingState(SLIDING_STATE_TOP, true);
                }
            } else {
                // Sliding down(back) to an anchor
                if (getAnchorYImage() < ViewHelper.getTranslationY(mInterceptionLayout)) {
                    changeSlidingState(SLIDING_STATE_BOTTOM, true);
                } else {
                    changeSlidingState(SLIDING_STATE_MIDDLE, true);
                }
            }
        }
    }

    private void slideTo(float translationY, final boolean animated) {
        ViewHelper.setTranslationY(mInterceptionLayout, translationY);

        if (translationY < 0) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mInterceptionLayout.getLayoutParams();
            lp.height = (int) -translationY + getScreenHeight();
            mInterceptionLayout.requestLayout();
        }

        // Translate title
        float hiddenHeight = translationY < 0 ? -translationY : 0;
        ViewHelper.setTranslationY(mTitle, Math.min(mIntersectionHeight, (mHeaderBarHeight + hiddenHeight - mActionBarSize) / 2));

        // Translate image
        float imageAnimatableHeight = getScreenHeight() - mHeaderBarHeight;
        float imageTranslationScale = imageAnimatableHeight / (imageAnimatableHeight - mImageView.getHeight());
        float imageTranslationY = Math.max(0, imageAnimatableHeight - (imageAnimatableHeight - translationY) * imageTranslationScale);
        ViewHelper.setTranslationY(mImageView, imageTranslationY);
        ViewHelper.setTranslationY(mProfileImageView, imageTranslationY);

        // Show/hide FAB
        if (ViewHelper.getTranslationY(mInterceptionLayout) < mFlexibleSpaceImageHeight) {
            hideFab(animated);
        } else {
            if (animated) {
                ViewPropertyAnimator.animate(mToolbar).scaleY(0).setDuration(200).start();
            } else {
                ViewHelper.setScaleY(mToolbar, 0);
            }
            showFab(animated);
        }
        if (ViewHelper.getTranslationY(mInterceptionLayout) <= mFlexibleSpaceImageHeight) {
            if (animated) {
                ViewPropertyAnimator.animate(mToolbar).scaleY(1).setDuration(200).start();
            } else {
                ViewHelper.setScaleY(mToolbar, 1);
            }
            mToolbar.setBackgroundColor(ScrollUtils.getColorWithAlpha(0, mToolbarColor));
        }

        changeToolbarTitleVisibility();
        changeHeaderBarColorAnimated(animated);
        changeHeaderOverlay();
    }

    private void slideWithAnimation(float toY) {
        float layoutTranslationY = ViewHelper.getTranslationY(mInterceptionLayout);
        if (layoutTranslationY != toY) {
            ValueAnimator animator = ValueAnimator.ofFloat(ViewHelper.getTranslationY(mInterceptionLayout), toY).setDuration(200);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    slideTo((float) animation.getAnimatedValue(), true);
                }
            });
            animator.start();
        }
    }

    private void changeToolbarTitleVisibility() {
        if (ViewHelper.getTranslationY(mInterceptionLayout) <= mIntersectionHeight) {
            if (ViewHelper.getAlpha(mToolbarTitle) != 1) {
                ViewPropertyAnimator.animate(mToolbarTitle).cancel();
                ViewPropertyAnimator.animate(mToolbarTitle).alpha(1).setDuration(200).start();
            }
        } else if (ViewHelper.getAlpha(mToolbarTitle) != 0) {
            ViewPropertyAnimator.animate(mToolbarTitle).cancel();
            ViewPropertyAnimator.animate(mToolbarTitle).alpha(0).setDuration(200).start();
        } else {
            ViewHelper.setAlpha(mToolbarTitle, 0);
        }
    }

    private void changeHeaderBarColorAnimated(boolean animated) {
        if (mHeaderColorIsChanging) {
            return;
        }
        boolean shouldBeWhite = getAnchorYBottom() == ViewHelper.getTranslationY(mInterceptionLayout);
        if (!mHeaderIsAtBottom && !mHeaderColorChangedToBottom && shouldBeWhite) {
            mHeaderIsAtBottom = true;
            mHeaderIsNotAtBottom = false;
            if (animated) {
                ValueAnimator animator = ValueAnimator.ofFloat(0, 1).setDuration(100);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float alpha = (float) animation.getAnimatedValue();
                        mHeaderColorIsChanging = (alpha != 1);
                        changeHeaderBarColor(alpha);
                    }
                });
                animator.start();
            } else {
                changeHeaderBarColor(1);
            }
        } else if (!mHeaderIsNotAtBottom && !shouldBeWhite) {
            mHeaderIsAtBottom = false;
            mHeaderIsNotAtBottom = true;
            if (animated) {
                ValueAnimator animator = ValueAnimator.ofFloat(1, 0).setDuration(100);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float alpha = (float) animation.getAnimatedValue();
                        mHeaderColorIsChanging = (alpha != 0);
                        changeHeaderBarColor(alpha);
                    }
                });
                animator.start();
            } else {
                changeHeaderBarColor(0);
            }
        }
    }

    private void changeHeaderBarColor(float alpha) {
        mHeaderBar.setBackgroundColor(ScrollUtils.mixColors(mColorPrimary, Color.WHITE, alpha));
        mTitle.setTextColor(ScrollUtils.mixColors(Color.WHITE, Color.BLACK, alpha));
        mHeaderColorChangedToBottom = (alpha == 1);
    }

    private void changeHeaderOverlay() {
        final float translationY = ViewHelper.getTranslationY(mInterceptionLayout);
        if (translationY <= mToolbar.getHeight() - mSlidingHeaderBlueSize) {
            mHeaderOverlay.setVisibility(View.VISIBLE);
            mHeaderFlexibleSpace.getLayoutParams().height = (int) (mToolbar.getHeight() - mSlidingHeaderBlueSize - translationY);
            mHeaderFlexibleSpace.requestLayout();
            mHeaderOverlay.requestLayout();
        } else {
            mHeaderOverlay.setVisibility(View.INVISIBLE);
        }
    }

    private void showFab(boolean animated) {
        if (mFab == null) {
            return;
        }
        if (!mFabIsShown) {
            if (animated) {
                ViewPropertyAnimator.animate(mFab).cancel();
                ViewPropertyAnimator.animate(mFab).scaleX(1).scaleY(1).setDuration(200).start();
            } else {
                ViewHelper.setScaleX(mFab, 1);
                ViewHelper.setScaleY(mFab, 1);
            }
            mFabIsShown = true;
        } else {
            // Ensure that FAB is shown
            ViewHelper.setScaleX(mFab, 1);
            ViewHelper.setScaleY(mFab, 1);
        }
    }

    private void hideFab(boolean animated) {
        if (mFab == null) {
            return;
        }
        if (mFabIsShown) {
            if (animated) {
                ViewPropertyAnimator.animate(mFab).cancel();
                ViewPropertyAnimator.animate(mFab).scaleX(0).scaleY(0).setDuration(200).start();
            } else {
                ViewHelper.setScaleX(mFab, 0);
                ViewHelper.setScaleY(mFab, 0);
            }
            mFabIsShown = false;
        } else {
            // Ensure that FAB is hidden
            ViewHelper.setScaleX(mFab, 0);
            ViewHelper.setScaleY(mFab, 0);
        }
    }

    private float getAnchorYBottom() {
        return getScreenHeight() - mHeaderBarHeight;
    }

    private float getAnchorYImage() {
        return mImageView.getHeight();
    }

    public static class ListFragment extends Fragment {
        private static List<Live> artistListinList = new ArrayList<Live>();

        public static ListFragment newInstance(List<Live> artistList) {
            artistListinList = artistList;
            return new ListFragment();
        }

        /*
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final SlidingUpBaseActivity activity = (SlidingUpBaseActivity) getActivity();
            LiveHistoryListAdapter adapter = new LiveHistoryListAdapter(
                    activity, artistListinList);


            ListView listView = (ListView) LayoutInflater
                    .from(activity)
                    .inflate(R.layout.fragment_video_recycler, container, false);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view,
                                        int position, long id) {
                    ImageView coverImage = (ImageView) view.findViewById(R.id.image_live);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, coverImage, "cover");

                    Intent watch = new Intent(activity,VideoViewFragment.class);
                    watch.putExtra("url",artistListinList.get(position).getUrlLive());
                    activity.startActivity(watch, options.toBundle());
                    // /activity.showDetails(artistListinList,position);
                }
            });
            return listView;
        }
        */
    }
}

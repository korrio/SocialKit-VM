package co.aquario.socialkit.fragment.pager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.view.HomeTabPagerItem;
import co.aquario.socialkit.fragment.view.HomeViewPagerNiceTabAdapter;
import me.amiee.nicetab.NiceTabLayout;
import me.amiee.nicetab.NiceTabStrip;

public class HomeViewPagerNiceTabFragment extends BaseFragment {
    private static final String USER_ID = "USER_ID";
    public LinearLayout mTabsLinearLayout;
    private List<HomeTabPagerItem> mTabs = new ArrayList<>();
    private String userId = "";

    public static HomeViewPagerNiceTabFragment newInstance(String userId){
        HomeViewPagerNiceTabFragment mFragment = new HomeViewPagerNiceTabFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID,userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
        } else {
            userId = prefManager.userId().getOr("0");
        }

        mTabs.add(new HomeTabPagerItem(0, getString(R.string.post),userId));
        mTabs.add(new HomeTabPagerItem(1, getString(R.string.follower),userId));
        mTabs.add(new HomeTabPagerItem(2, getString(R.string.following),userId));
        mTabs.add(new HomeTabPagerItem(3, getString(R.string.friend),userId));
        mTabs.add(new HomeTabPagerItem(4, getString(R.string.page),userId));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager_nice_tab, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    //public PagerSlidingTabStrip mSlidingTabLayout;
    public NiceTabLayout mNiceTabLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	
    	mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new HomeViewPagerNiceTabAdapter(getChildFragmentManager(), mTabs));
        //mViewPager.setPageTransformer(true, toolbar ForegroundToBackgroundTransformer());

        mNiceTabLayout = (NiceTabLayout) view.findViewById(R.id.sliding_tabs);
        mNiceTabLayout.setViewPager(mViewPager);
        mNiceTabLayout.setShowDivider(false);
        mNiceTabLayout.setTabStripColorize(new NiceTabStrip.TabStripColorize() {

            @Override
            public int getIndicatorColor(int position) {
                return Color.WHITE;
            }

            @Override
            public int getDividerColor(int position) {
                return Color.WHITE;
            }
        });
        mNiceTabLayout.setTabColorize(new NiceTabLayout.TabColorize() {

            @Override
            public int getDefaultTabColor(int position) {
                return Color.WHITE;

            }

            @Override
            public int getSelectedTabColor(int position) {
                return Color.WHITE;
            }
        });

        //mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        //mSlidingTabLayout.setTextColorResource(R.color.white);

//        mSlidingTabLayout.setAllCaps(false);
//        mSlidingTabLayout.setShouldExpand(true);
//        mSlidingTabLayout.setFillViewport(true);
//        //mSlidingTabLayout.setDistributeEvenly(true);
//
//        //mSlidingTabLayout.setBackgroundResource(R.color.black_semi_transparent);
//
//        //mSlidingTabLayout.setIndicatorColorResource(getResources().getColor(android.R.color.transparent));
//        mSlidingTabLayout.setDividerColor(getResources().getColor(android.R.color.transparent));
//
//        mSlidingTabLayout.setViewPager(mViewPager);


        //mTabsLinearLayout = ((LinearLayout) mNiceTabLayout.getChildAt(0));
        //ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(0);
        //ib.setImageResource(R.drawable.ic_action_post_white);

        getActivity().setTitle("Home");

        mNiceTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        getActivity().setTitle("Home");
                        break;
                    case 1:
                        getActivity().setTitle("Followers");
                        break;
                    case 2:
                        getActivity().setTitle("Followings");
                        break;
                    case 3:
                        getActivity().setTitle("Friends");
                        break;
                    case 4:
                        getActivity().setTitle("Pages");
                        break;
                }

//                for(int i=0; i < mTabsLinearLayout.getChildCount(); i++){
//                    ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(i);
//                    switch (i){
//                        case 0:
//                            if(i == position)
//                                ib.setImageResource(R.drawable.ic_action_post_white);
//                            else
//                                ib.setImageResource(R.drawable.ic_action_post_white);
//                            break;
//                        case 1:
//                            if(i == position)
//                                ib.setImageResource(R.drawable.ic_action_following_white);
//                            else
//                                ib.setImageResource(R.drawable.ic_action_following_white);
//                            break;
//                        case 2:
//                            if(i == position)
//                                ib.setImageResource(R.drawable.ic_action_follower_white);
//                            else
//                                ib.setImageResource(R.drawable.ic_action_follower_white);
//                            break;
//                        case 3:
//                            if(i == position)
//                                ib.setImageResource(R.drawable.ic_action_friend_white);
//                            else
//                                ib.setImageResource(R.drawable.ic_action_friend_white);
//                            break;
//                        case 4:
//                            if(i == position)
//                                ib.setImageResource(R.drawable.ic_heart_white);
//                            else
//                                ib.setImageResource(R.drawable.ic_heart_white);
//                            break;
//
//                        default:
//                            break;
//                    }
//
//                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("HomePageState", state + "");
            }


        });
    }

}
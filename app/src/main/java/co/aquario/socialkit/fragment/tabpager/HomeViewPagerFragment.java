package co.aquario.socialkit.fragment.tabpager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.view.HomeTabPagerItem;
import co.aquario.socialkit.fragment.view.HomeViewPagerAdapter;

public class HomeViewPagerFragment extends BaseFragment {
    private static final String USER_ID = "USER_ID";
    public LinearLayout mTabsLinearLayout;
    private List<HomeTabPagerItem> mTabs = new ArrayList<>();
    private String userId = "";

    public static HomeViewPagerFragment newInstance(String userId){
        HomeViewPagerFragment mFragment = new HomeViewPagerFragment();
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
            userId = prefManager.userId().getOr("6");
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

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    public PagerSlidingTabStrip mSlidingTabLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	
    	mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new HomeViewPagerAdapter(getChildFragmentManager(), mTabs));
        //mViewPager.setPageTransformer(true, toolbar ForegroundToBackgroundTransformer());

        mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        //mSlidingTabLayout.setTextColorResource(R.color.white);

        mSlidingTabLayout.setAllCaps(false);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setFillViewport(true);
        //mSlidingTabLayout.setDistributeEvenly(true);

        //mSlidingTabLayout.setBackgroundResource(R.color.black_semi_transparent);

        //mSlidingTabLayout.setIndicatorColorResource(getResources().getColor(android.R.color.transparent));
        mSlidingTabLayout.setDividerColor(getResources().getColor(android.R.color.transparent));

        mSlidingTabLayout.setViewPager(mViewPager);


        mTabsLinearLayout = ((LinearLayout) mSlidingTabLayout.getChildAt(0));
        ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(0);
        ib.setImageResource(R.drawable.ic_action_post_white);

        getActivity().setTitle("Home");

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

                for(int i=0; i < mTabsLinearLayout.getChildCount(); i++){
                    ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(i);
                    switch (i){
                        case 0:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_post_white);
                            else
                                ib.setImageResource(R.drawable.ic_action_post_white);
                            break;
                        case 1:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_following_white);
                            else
                                ib.setImageResource(R.drawable.ic_action_following_white);
                            break;
                        case 2:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_follower_white);
                            else
                                ib.setImageResource(R.drawable.ic_action_follower_white);
                            break;
                        case 3:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_friend_white);
                            else
                                ib.setImageResource(R.drawable.ic_action_friend_white);
                            break;
                        case 4:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_heart_white);
                            else
                                ib.setImageResource(R.drawable.ic_heart_white);
                            break;

                        default:
                            break;
                    }

                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("HomePageState", state + "");
            }


        });
    }

}
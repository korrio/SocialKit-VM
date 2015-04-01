package co.aquario.socialkit.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.view.TabPagerItem;
import co.aquario.socialkit.adapter.view.ViewPagerAdapter;

public class ViewPagerFragment extends BaseFragment {
	private List<TabPagerItem> mTabs = new ArrayList<>();

    private static final String USER_ID = "USER_ID";

    private String userId = "";

    public static ViewPagerFragment newInstance(String userId){
        ViewPagerFragment mFragment = new ViewPagerFragment();
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
            userId = prefManager.userId().getOr("1301");
        }

        mTabs.add(new TabPagerItem(0, getString(R.string.post),userId));
        mTabs.add(new TabPagerItem(1, getString(R.string.follower),userId));
        mTabs.add(new TabPagerItem(2, getString(R.string.following),userId));
        mTabs.add(new TabPagerItem(3, getString(R.string.friend),userId));
        mTabs.add(new TabPagerItem(4, getString(R.string.page),userId));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    LinearLayout mTabsLinearLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	
    	mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new ViewPagerAdapter(getChildFragmentManager(), mTabs));

        PagerSlidingTabStrip mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        //mSlidingTabLayout.setTextColorResource(R.color.white);

        mSlidingTabLayout.setAllCaps(false);
        mSlidingTabLayout.setShouldExpand(true);
        //mSlidingTabLayout.setFillViewport(true);
        //mSlidingTabLayout.setDistributeEvenly(true)

        mSlidingTabLayout.setBackgroundResource(R.color.white);

        mSlidingTabLayout.setIndicatorColorResource(R.color.indigo_700);
        mSlidingTabLayout.setDividerColor(getResources().getColor(android.R.color.transparent));

        mSlidingTabLayout.setViewPager(mViewPager);


        mTabsLinearLayout = ((LinearLayout) mSlidingTabLayout.getChildAt(0));
        ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(0);
        ib.setImageResource(R.drawable.ic_action_post);



        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                for(int i=0; i < mTabsLinearLayout.getChildCount(); i++){
                    ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(i);
                    switch (i){
                        case 0:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_post_highlighted);
                            else
                                ib.setImageResource(R.drawable.ic_action_post);
                            break;
                        case 1:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_follower_highlighted);
                            else
                                ib.setImageResource(R.drawable.ic_action_follower);
                            break;
                        case 2:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_following_highlighted);
                            else
                                ib.setImageResource(R.drawable.ic_action_following);
                            break;
                        case 3:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_action_friend_highlighted);
                            else
                                ib.setImageResource(R.drawable.ic_action_friend);
                            break;
                        case 4:
                            if(i == position)
                                ib.setImageResource(R.drawable.ic_heart_red);
                            else
                                ib.setImageResource(R.drawable.ic_heart_outline_grey);
                            break;

                        default:
                            break;
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
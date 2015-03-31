package co.aquario.socialkit.fragment.main;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.adapter.view.SocialTabPagerItem;
import co.aquario.socialkit.adapter.view.SocialViewPagerAdapter;
import co.aquario.socialkit.fragment.BaseFragment;

public class SocialFragment extends BaseFragment {
	private List<SocialTabPagerItem> mTabs = new ArrayList<>();

    private static final String USER_ID = "USER_ID";

    private String userId = "";

    public static SocialFragment newInstance(String userId){
        SocialFragment mFragment = new SocialFragment();
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

        mTabs.add(new SocialTabPagerItem(0, getString(R.string.friend),userId));
        mTabs.add(new SocialTabPagerItem(1, getString(R.string.following),userId));
        mTabs.add(new SocialTabPagerItem(2, getString(R.string.follower),userId));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT ));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	
    	mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new SocialViewPagerAdapter(getChildFragmentManager(), mTabs));

        PagerSlidingTabStrip mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mSlidingTabLayout.setTextColorResource(R.color.white);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
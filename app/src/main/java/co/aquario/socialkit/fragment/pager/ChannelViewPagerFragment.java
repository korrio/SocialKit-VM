package co.aquario.socialkit.fragment.pager;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.view.ChannelTabPagerItem;
import co.aquario.socialkit.fragment.view.ChannelViewPagerAdapter;

public class ChannelViewPagerFragment extends BaseFragment {
	private List<ChannelTabPagerItem> mTabs = new ArrayList<>();

    public static ChannelViewPagerFragment newInstance(){
        ChannelViewPagerFragment mFragment = new ChannelViewPagerFragment();
        Bundle mBundle = new Bundle();
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabs.add(new ChannelTabPagerItem(0, "NOW LIVE"));
        mTabs.add(new ChannelTabPagerItem(1, "MOST FOLLOWER"));
        //mTabs.add(toolbar ChannelTabPagerItem(2, "WHO LIVE"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT ));
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);
    	
    	mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new ChannelViewPagerAdapter(getChildFragmentManager(), mTabs));

        PagerSlidingTabStrip mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mSlidingTabLayout.setTextColorResource(R.color.white);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
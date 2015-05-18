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
import co.aquario.socialkit.adapter.view.VideoTabPagerItem;
import co.aquario.socialkit.adapter.view.VideoViewPagerAdapter;
import co.aquario.socialkit.fragment.BaseFragment;

public class VideoViewPagerFragment extends BaseFragment {
	private List<VideoTabPagerItem> mTabs = new ArrayList<>();

    private static final String USER_ID = "USER_ID";
    private String userId = "";

    public static VideoViewPagerFragment newInstance(String userId){
        VideoViewPagerFragment mFragment = new VideoViewPagerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID,userId);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabs.add(new VideoTabPagerItem(0, "NEWEST"));
        mTabs.add(new VideoTabPagerItem(1, "MOST VIEW"));
        mTabs.add(new VideoTabPagerItem(2, "MOST LOVE"));

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
        mViewPager.setAdapter(new VideoViewPagerAdapter(getChildFragmentManager(), mTabs));

        PagerSlidingTabStrip mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mSlidingTabLayout.setTextColorResource(R.color.white);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
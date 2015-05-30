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

public class SocialViewPagerFragment extends BaseFragment {
    private static final String SORT_TYPE = "SORT_TYPE";
    private List<SocialTabPagerItem> mTabs = new ArrayList<>();
    private String userId = "";



    public static SocialViewPagerFragment newInstance(String sort){
        SocialViewPagerFragment mFragment = new SocialViewPagerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(SORT_TYPE,sort);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabs.add(new SocialTabPagerItem(0, "Newest", "N"));
        mTabs.add(new SocialTabPagerItem(1, "Most Followers", "F"));
        mTabs.add(new SocialTabPagerItem(2, "Alphabets", "A"));

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
        mViewPager.setAdapter(new SocialViewPagerAdapter(getChildFragmentManager(), mTabs));

        PagerSlidingTabStrip mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mSlidingTabLayout.setTextColorResource(R.color.white);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
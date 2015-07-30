package co.aquario.socialkit.activity.search;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

import co.aquario.socialkit.R;

public class SearchViewPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

	private List<SearchTabPagerItem> mTabs;
	public SearchViewPagerAdapter(FragmentManager fragmentManager, List<SearchTabPagerItem> mTabs) {
		super(fragmentManager);
		this.mTabs = mTabs;
	}

    @Override
    public Fragment getItem(int i) {
        return mTabs.get(i).createFragment();
    }

    @Override
    public int getCount() {
        return mTabs.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabs.get(position).getTitle();
    }


    @Override
    public int getPageIconResId(int i) {
        switch (i) {
            case 0:
                return R.drawable.ic_action_friend_white;
            case 1:
                return R.drawable.ic_action_post_white;
            case 2:
                return R.drawable.ic_whatshot_white_24dp;
        }

        return 0;
    }


}
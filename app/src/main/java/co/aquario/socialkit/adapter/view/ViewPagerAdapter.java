package co.aquario.socialkit.adapter.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;

import java.util.List;

import co.aquario.socialkit.R;

public class ViewPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

	private List<TabPagerItem> mTabs;
	public ViewPagerAdapter(FragmentManager fragmentManager, List<TabPagerItem> mTabs) {
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
                return R.drawable.ic_action_post;
            case 1:
                return R.drawable.ic_action_follower;
            case 2:
                return R.drawable.ic_action_following;
            case 3:
                return R.drawable.ic_action_friend;
            case 4:
                return R.drawable.ic_heart_outline_grey;
        }

        return 0;
    }
}
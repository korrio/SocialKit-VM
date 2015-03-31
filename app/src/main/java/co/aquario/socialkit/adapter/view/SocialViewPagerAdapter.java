package co.aquario.socialkit.adapter.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

public class SocialViewPagerAdapter extends FragmentPagerAdapter {

	private List<SocialTabPagerItem> mTabs;
	public SocialViewPagerAdapter(FragmentManager fragmentManager, List<SocialTabPagerItem> mTabs) {
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
}
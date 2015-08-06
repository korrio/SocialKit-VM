package co.aquario.socialkit.fragment.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import co.aquario.socialkit.R;
import me.amiee.nicetab.NiceTabLayout;

public class HomeViewPagerNiceTabAdapter extends FragmentPagerAdapter implements NiceTabLayout.IconTabProvider {

	private List<HomeTabPagerItem> mTabs;
	public HomeViewPagerNiceTabAdapter(FragmentManager fragmentManager, List<HomeTabPagerItem> mTabs) {
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
                return R.drawable.ic_action_post_white;
            case 1:
                return R.drawable.ic_action_following_white;
            case 2:
                return R.drawable.ic_action_follower_white;
            case 3:
                return R.drawable.ic_action_friend_white;
            case 4:
                return R.drawable.ic_heart_white;
        }

        return 0;
    }


}
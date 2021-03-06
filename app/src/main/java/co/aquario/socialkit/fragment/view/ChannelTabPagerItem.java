package co.aquario.socialkit.fragment.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.fragment.LiveHistoryFragment;
import co.aquario.socialkit.fragment.main.ChannelFragment;

public class ChannelTabPagerItem {

	private final CharSequence mTitle;
    private final int position;
    //private final String userId;

    private Fragment[] listFragments;
    public ChannelTabPagerItem(int position, CharSequence title) {
        this.mTitle = title;
        this.position = position;
        //this.userId = userId;

        listFragments = new Fragment[] {
                new ChannelFragment().newInstance("NOW LIVE",0),
                new ChannelFragment().newInstance("MOST FOLLOWER",1),
                new LiveHistoryFragment().newInstance("0")};

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    //public String getUserId() { return userId;}
}

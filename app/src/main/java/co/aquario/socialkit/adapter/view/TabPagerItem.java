package co.aquario.socialkit.adapter.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.fragment.FeedFragment;
import co.aquario.socialkit.fragment.FriendFragment;

public class TabPagerItem {
	
	private final CharSequence mTitle;
    private final int position;
    private final String userId;
        
    private Fragment[] listFragments;
    public TabPagerItem(int position, CharSequence title, String userId) {
        this.mTitle = title;
        this.position = position;
        this.userId = userId;

        listFragments = new Fragment[] {
                new FeedFragment().newInstance(title.toString()),
                new FriendFragment().newInstance("FOLLOWER",userId),
                new FriendFragment().newInstance("FOLLOWING",userId),
                new FriendFragment().newInstance("FRIEND",userId),
                new FriendFragment().newInstance("PAGE",userId)};

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public String getUserId() { return userId;}
}

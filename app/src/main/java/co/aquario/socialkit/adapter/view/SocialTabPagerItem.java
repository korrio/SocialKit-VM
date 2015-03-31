package co.aquario.socialkit.adapter.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.fragment.FriendFragment;

public class SocialTabPagerItem {

	private final CharSequence mTitle;
    private final int position;
    private final String userId;

    private Fragment[] listFragments;
    public SocialTabPagerItem(int position, CharSequence title, String userId) {
        this.mTitle = title;
        this.position = position;
        this.userId = userId;

        listFragments = new Fragment[] {
                new FriendFragment().newInstance("FRIEND",userId),
                new FriendFragment().newInstance("FOLLOWING",userId),
                new FriendFragment().newInstance("FOLLOWER",userId)};

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public String getUserId() { return userId;}
}

package co.aquario.socialkit.fragment.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.main.FriendFragment;

public class HomeTabPagerItem {
	
	private final CharSequence mTitle;
    private final int position;
    private final String userId;
        
    private Fragment[] listFragments;
    public HomeTabPagerItem(int position, CharSequence title, String userId) {
        this.mTitle = title;
        this.position = position;
        this.userId = userId;

        listFragments = new Fragment[] {
                new FeedFragment().newInstance(userId, true),
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

    int getAndroidIconResId() {
        return R.drawable.chat_icon;
    }
}

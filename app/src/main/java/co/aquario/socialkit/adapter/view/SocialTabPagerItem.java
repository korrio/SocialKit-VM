package co.aquario.socialkit.adapter.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.fragment.main.FriendSocialFragment;

public class SocialTabPagerItem {

	private final CharSequence mTitle;
    private final int position;

    private Fragment[] listFragments;
    public SocialTabPagerItem(int position, CharSequence title, String sort) {
        this.mTitle = title;
        this.position = position;

        listFragments = new Fragment[] {
                new FriendSocialFragment().newInstance("N",""),
                new FriendSocialFragment().newInstance("F",""),
                new FriendSocialFragment().newInstance("A","")};

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

}

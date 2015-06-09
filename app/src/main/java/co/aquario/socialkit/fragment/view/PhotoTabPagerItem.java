package co.aquario.socialkit.fragment.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.fragment.main.PhotoFreeFlowFragment;

public class PhotoTabPagerItem {

	private final CharSequence mTitle;
    private final int position;
    //private final String userId;

    private Fragment[] listFragments;
    public PhotoTabPagerItem(int position, CharSequence title) {
        this.mTitle = title;
        this.position = position;
        //this.userId = userId;

        listFragments = new Fragment[] {
                new PhotoFreeFlowFragment().newInstance("N"),
                new PhotoFreeFlowFragment().newInstance("V"),
                new PhotoFreeFlowFragment().newInstance("L")};

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    //public String getUserId() { return userId;}
}

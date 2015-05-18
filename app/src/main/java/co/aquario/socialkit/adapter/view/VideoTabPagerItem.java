package co.aquario.socialkit.adapter.view;

import android.support.v4.app.Fragment;

import co.aquario.socialkit.fragment.main.VideoFragment;

public class VideoTabPagerItem {

	private final CharSequence mTitle;
    private final int position;
    //private final String userId;

    private Fragment[] listFragments;
    public VideoTabPagerItem(int position, CharSequence title) {
        this.mTitle = title;
        this.position = position;
        //this.userId = userId;

        listFragments = new Fragment[] {
                new VideoFragment().newInstance("","N"),
                new VideoFragment().newInstance("","V"),
                new VideoFragment().newInstance("","L")};

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    //public String getUserId() { return userId;}
}

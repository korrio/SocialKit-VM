package co.aquario.socialkit.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;

import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.PhotoFragmentGrid;
import co.aquario.socialkit.fragment.main.PhotoFragmentList;


public class FragmentPagerAdapterPhotoGrid extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {
    private final int[] ICONS = {R.drawable.ic_list, R.drawable.ic_grid};


    public FragmentPagerAdapterPhotoGrid(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0)? "Tab 1" : "Tab2" ;
    }
    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public Fragment getItem(int position) {
        return (position == 0)? new PhotoFragmentList() : new PhotoFragmentGrid() ;
    }

    @Override
    public int getPageIconResId(int position) {
        return ICONS[position];
    }
}
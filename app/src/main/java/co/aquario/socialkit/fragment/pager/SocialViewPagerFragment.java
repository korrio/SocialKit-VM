package co.aquario.socialkit.fragment.pager;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.VMApp;
import co.aquario.socialkit.activity.search.SearchPagerFragment;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.fragment.view.SocialTabPagerItem;
import co.aquario.socialkit.fragment.view.SocialViewPagerAdapter;
import co.aquario.socialkit.util.Utils;


public class SocialViewPagerFragment extends BaseFragment {
    private static final String SORT_TYPE = "SORT_TYPE";
    private List<SocialTabPagerItem> mTabs = new ArrayList<>();
    private String userId = "";

    public static SocialViewPagerFragment newInstance(String sort){
        SocialViewPagerFragment mFragment = new SocialViewPagerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(SORT_TYPE,sort);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTabs.add(new SocialTabPagerItem(0, "Recent", "N"));
        mTabs.add(new SocialTabPagerItem(1, "Most Followed", "F"));
        mTabs.add(new SocialTabPagerItem(2, "A-Z", "A"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager, container, false);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
        return rootView;
    }

    Toolbar toolbar;
    SearchView mSearchView;
    void setupToolbar() {
        if(((BaseActivity)getActivity()).getToolbar() != null) {
            toolbar = ((BaseActivity)getActivity()).getToolbar();
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_search);
            //toolbar.setTitle("Search: " + query);

            mSearchView = (SearchView) toolbar.getMenu().findItem(R.id.action_search).getActionView();
            MenuItemCompat.expandActionView(toolbar.getMenu().findItem(R.id.action_search));
            //mSearchView.setQueryHint(oldQuery);
            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    if (getActivity() != null) {
                        Utils.hideKeyboard(getActivity());

                        SearchPagerFragment fragment = new SearchPagerFragment().newInstance(VMApp.mPref.userId().getOr("0"), s);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.sub_container, fragment, "MAIN_SEARCH").addToBackStack(null).commit();
                        toolbar.setTitle('"' + s + '"');
                        toolbar.setSubtitle("Searching");
                        //oldQuery = s;
                    }

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    return false;
                }
            });

            MenuItemCompat.expandActionView(toolbar.getMenu().findItem(R.id.action_search));
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	ViewPager mViewPager = (ViewPager) view.findViewById(R.id.pager);

        if(getActivity().getClass().getSimpleName().equals("MainActivity") || getActivity().getClass().getSimpleName().equals("BaseActivity"))
            setupToolbar();
    	
    	mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new SocialViewPagerAdapter(getChildFragmentManager(), mTabs));
        //mViewPager.setPageTransformer(true, toolbar ForegroundToBackgroundTransformer());

        PagerSlidingTabStrip mSlidingTabLayout = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        mSlidingTabLayout.setTextColorResource(R.color.white);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
}
package co.aquario.socialkit.activity.search;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.BaseActivity;
import co.aquario.socialkit.R;
import co.aquario.socialkit.fragment.main.BaseFragment;
import co.aquario.socialkit.handler.ApiBus;
import co.aquario.socialkit.model.Hashtag;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.model.User;

public class SearchPagerFragment extends BaseFragment {
    private static final String USER_ID = "USER_ID";
    private static final String SEARCH_QUERY = "SEARCH_QUERY";
    public LinearLayout mTabsLinearLayout;
    private List<SearchTabPagerItem> mTabs = new ArrayList<>();
    private String userId = "";
    private String query;

    public static SearchPagerFragment newInstance(String userId, String query){
        SearchPagerFragment mFragment = new SearchPagerFragment();
        Bundle mBundle = new Bundle();
        mBundle.putString(USER_ID,userId);
        mBundle.putString(SEARCH_QUERY,query);
        mFragment.setArguments(mBundle);
        return mFragment;
    }

    boolean isLoading = false;

    Toolbar toolbar;
    void setupToolbar() {
        if(((BaseActivity)getActivity()).getToolbar() != null) {
            toolbar = ((BaseActivity)getActivity()).getToolbar();
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_search);
            toolbar.setTitle("Search: " + query);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getString(USER_ID);
            query = getArguments().getString(SEARCH_QUERY);
        } else {
            userId = prefManager.userId().getOr("0");
            query = "";
        }

        ApiBus.getInstance().postQueue(new GetSearchEvent(query));
        isLoading = true;

        //

    }

    View rootView;
    ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_viewpager_loading, container, false);
        this.rootView = rootView;

        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        setupToolbar();

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);

        if(isLoading && progressBar != null)
            progressBar.setVisibility(View.VISIBLE);

        return rootView;
    }

    ArrayList<User> listUser = new ArrayList<>();
    ArrayList<PostStory> listStory = new ArrayList<>();
    ArrayList<Hashtag> listHashtag = new ArrayList<>();

    @Subscribe public void onGetSearchResultSusccess(GetSearchEventSuccess event) {
        Log.e("smg","smg");
        if(event.result.getUser() != null) {
            //Log.e("search_result",event.result.getUser().size()+ "");
            listUser = event.result.getUser();
            listStory = event.result.getStory();
            listHashtag = event.result.getHashtag();
        }

        isLoading = false;

        if(progressBar != null)
            progressBar.setVisibility(View.GONE);

        //listUser = event.result.getUser();
        mTabs.add(new SearchTabPagerItem(0, "USER",userId,listUser,listStory,listHashtag));
        mTabs.add(new SearchTabPagerItem(1, "STORY",userId,listUser,listStory,listHashtag));
        mTabs.add(new SearchTabPagerItem(2, "HASHTAG",userId,listUser,listStory,listHashtag));
        initTabs();
    }

    @Override
    public void onResume() {
        super.onResume();
        //ApiBus.getInstance().postQueue(new GetSearchEvent("youlove"));
    }

    public PagerSlidingTabStrip mSlidingTabLayout;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }

    private void initTabs() {
        ViewPager mViewPager = (ViewPager) rootView.findViewById(R.id.pager);


        mViewPager.setOffscreenPageLimit(6);
        mViewPager.setAdapter(new SearchViewPagerAdapter(getChildFragmentManager(), mTabs));
        //mViewPager.setPageTransformer(true, toolbar ForegroundToBackgroundTransformer());

        mSlidingTabLayout = (PagerSlidingTabStrip) rootView.findViewById(R.id.tabs);
        //mSlidingTabLayout.setTextColorResource(R.color.white);

        mSlidingTabLayout.setAllCaps(false);
        mSlidingTabLayout.setShouldExpand(true);
        mSlidingTabLayout.setFillViewport(true);
        //mSlidingTabLayout.setDistributeEvenly(true);

        //mSlidingTabLayout.setBackgroundResource(R.color.black_semi_transparent);

        //mSlidingTabLayout.setIndicatorColorResource(getResources().getColor(android.R.color.transparent));
        mSlidingTabLayout.setDividerColor(getResources().getColor(android.R.color.transparent));

        mSlidingTabLayout.setViewPager(mViewPager);


        //mTabsLinearLayout = ((LinearLayout) mSlidingTabLayout.getChildAt(0));
        //ImageButton ib = (ImageButton) mTabsLinearLayout.getChildAt(0);
        //ib.setImageResource(R.drawable.ic_action_post_white);

        getActivity().setTitle("Home");

        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {



            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.e("SearchPageState", state + "");
            }


        });
    }

}
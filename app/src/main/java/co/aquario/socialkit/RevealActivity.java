package co.aquario.socialkit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;
import com.quinny898.library.persistentsearch.SearchBox;
import com.quinny898.library.persistentsearch.SearchBox.MenuListener;
import com.quinny898.library.persistentsearch.SearchBox.SearchListener;
import com.quinny898.library.persistentsearch.SearchResult;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.fragment.main.FriendSocialFragment;
import co.aquario.socialkit.fragment.main.VideoFragment;
import co.aquario.socialkit.model.Hashtag;
import co.aquario.socialkit.model.User;
import co.aquario.socialkit.model.Video;

/**
 * Created by Mac on 6/8/15.
 */
public class RevealActivity extends ActionBarActivity {

    private SearchBox search;
    private Toolbar toolbar;

    public String query;
    public String q;
    public static final String KEY_QUERY = "query";

    ViewGroup tab;
    ViewPager viewPager;
    SmartTabLayout viewPagerTab;
    FragmentPagerItems pages;
    FragmentPagerItemAdapter adapter;

    Bundle userBundle;
    Bundle hashtagBundle;
    Bundle storyBundle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveal);
        getString(R.string.app_name);

        query = getIntent().getStringExtra(KEY_QUERY);
        setTitle(query);

        search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        this.setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return true;
            }
        });

        //toolbar.setTitle(demo.titleResId);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        tab = (ViewGroup) findViewById(R.id.tab);
        tab.addView(LayoutInflater.from(this).inflate(R.layout.demo_custom_tab_icons, tab, false));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
        setupPager(viewPagerTab);
        setupSearch();

        if(!query.equals("")) {
            search.setSearchString(query);
            search.toggleSearch();
        }


    }

    public static void startActivity(Context context, String query) {
        Intent intent = new Intent(context, RevealActivity.class);
        intent.putExtra(KEY_QUERY, query);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }



    public void setupPager(SmartTabLayout layout) {


        final LayoutInflater inflater = LayoutInflater.from(layout.getContext());
        final Resources res = layout.getContext().getResources();

        layout.setCustomTabView(new SmartTabLayout.TabProvider() {
            @Override
            public View createTabView(ViewGroup container, int position, PagerAdapter adapter) {
                ImageView icon = (ImageView) inflater.inflate(R.layout.custom_tab_icon, container, false);
                switch (position) {
                    case 0:
                        icon.setImageDrawable(res.getDrawable(R.drawable.ic_home_white_24dp));
                        break;

                    case 1:
                        icon.setImageDrawable(res.getDrawable(R.drawable.ic_person_white_24dp));
                        break;
                    case 2:
                        icon.setImageDrawable(res.getDrawable(R.drawable.ic_flash_on_white_24dp));
                        break;
                    default:
                        throw new IllegalStateException("Invalid position: " + position);
                }
                return icon;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            search.toggleSearch();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    List<Hashtag> listHashtag = new ArrayList<>();
    List<User> listUser = new ArrayList<>();
    List<Video> listStory = new ArrayList<>();


    public void setupSearch() {
        this.query = query;
        toolbar.setTitle(query);
        search.revealFromMenuItem(R.id.action_search, this);
        SearchResult option = new SearchResult("epic ", getResources().getDrawable(
                R.drawable.ic_history));
        search.addSearchable(option);

        SearchResult option2 = new SearchResult("ดี ", getResources().getDrawable(
                R.drawable.ic_history));
        search.addSearchable(option2);

        search.setMenuListener(new MenuListener() {

            @Override
            public void onMenuClick() {
                // Hamburger has been clicked
                Toast.makeText(RevealActivity.this, "Menu click",
                        Toast.LENGTH_LONG).show();
            }

        });
        search.setSearchListener(new SearchListener() {

            @Override
            public void onSearchOpened() {
                // Use this to tint the screen

            }

            @Override
            public void onSearchClosed() {
                // Use this to un-tint the screen
                closeSearch();
            }

            @Override
            public void onSearchTermChanged() {
                // React to the search term changing
                // Called after it has updated results
            }

            @Override
            public void onSearch(final String searchTerm) {
                Toast.makeText(RevealActivity.this, searchTerm + " Searched",
                        Toast.LENGTH_LONG).show();
                toolbar.setTitle(searchTerm);
                q = searchTerm;

                startSearch(searchTerm);


            }

            @Override
            public void onSearchCleared() {

            }

        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1234 && resultCode == RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            search.populateEditText(matches);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void startSearch(final String q) {
        OkHttpClient okHttpClient = new OkHttpClient();

        Request.Builder builder = new Request.Builder();
        Request request = builder.url("https://www.vdomax.com/ajax.php?t=search&a=mobile&q="+q).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                updateView("Error - " + e.getMessage());
            }

            @Override
            public void onResponse(Response response) {
                if (response.isSuccessful()) {
                    try {
                        //updateView(response.body().string());
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            JSONArray hashtags = obj.optJSONObject("result").optJSONArray("hashtag");
                            JSONArray users = obj.optJSONObject("result").optJSONArray("user");
                            JSONArray stories = obj.optJSONObject("result").optJSONArray("story");


                            for(int j = 0; j < hashtags.length();j++) {
                                JSONObject b = (JSONObject) hashtags.get(j);
                                String id = b.optString("id");
                                String hash  = b.optString("hash");
                                String tag = b.optString("tag");

                                Hashtag ht = new Hashtag(id,hash,tag);
                                listHashtag.add(ht);
                            }

                            for(int k = 0; k < users.length();k++) {
                                JSONObject b = (JSONObject) users.get(k);
                                String id = b.optString("id");
                                String avatar  = b.optString("avatar");
                                String name = b.optString("name");
                                String username = b.optString("username");

                                User user = new User();
                                user.setAvatar(avatar);
                                user.setName(name);
                                user.setUsername(username);
                                user.setId(id);
                                listUser.add(user);
                            }

                            for(int l = 0; l < stories.length();l++) {
                                JSONObject b = (JSONObject) stories.get(l);
                                String postId = b.optString("id");
                                String id = b.optString("youtube_video_id");
                                String title  = b.optString("youtube_title");
                                String desc = b.optString("youtube_description");
                                String view = b.optString("view");
                                String userId = b.optJSONObject("publisher").optString("id");
                                String username = b.optJSONObject("publisher").optString("username");
                                String avatar = b.optJSONObject("publisher").optString("avatar");
                                String name = b.optJSONObject("publisher").optString("name");

                                Video video = new Video();
                                video.setYoutubeId(id);
                                video.setTitle(title);
                                video.setDesc(desc);
                                video.setpAvatar(avatar);
                                video.setpUserId(userId);
                                video.setpName(name);
                                video.setView(view);

                                video.setPostId(postId);

                                listStory.add(video);
                            }

                            userBundle = new Bundle();
                            userBundle.putString("TYPE", "SEARCH");
                            userBundle.putParcelable("LIST", Parcels.wrap(listUser));

                            hashtagBundle = new Bundle();
                            hashtagBundle.putString("TYPE", "SEARCH");
                            hashtagBundle.putParcelable("LIST", Parcels.wrap(listHashtag));

                            storyBundle = new Bundle();
                            storyBundle.putString("TYPE", "SEARCH");
                            storyBundle.putString("KEYWORD_SEARCH",q);
                            storyBundle.putParcelable("LIST", Parcels.wrap(listStory));

                            updateView("");



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        //updateView("Error - " + e.getMessage());
                    }
                } else {
                    updateView("Not Success - code : " + response.code());
                }
            }

            public void updateView(final String strResult) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pages = new FragmentPagerItems(getApplicationContext());

                        pages.add(FragmentPagerItem.of("User", FriendSocialFragment.class,userBundle));
                        pages.add(FragmentPagerItem.of("Story", VideoFragment.class,storyBundle));
                        pages.add(FragmentPagerItem.of("Hashtag", VideoFragment.class,storyBundle));

                        adapter = new FragmentPagerItemAdapter(
                                getSupportFragmentManager(), pages);

                        viewPager.setAdapter(adapter);
                        viewPagerTab.setViewPager(viewPager);
                    }
                });
            }
        });
    }

    protected void closeSearch() {
        search.hideCircularly(this);
        if(search.getSearchText().isEmpty())toolbar.setTitle("");
    }

}

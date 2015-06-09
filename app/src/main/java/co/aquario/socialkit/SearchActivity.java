package co.aquario.socialkit;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
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

public class SearchActivity extends BaseActivity {
	Boolean isSearch;
	private SearchBox search;

	public static final String KEY_QUERY = "query";

	ViewGroup tab;
	ViewPager viewPager;
	SmartTabLayout viewPagerTab;
	FragmentPagerItems pages;
	FragmentPagerItemAdapter adapter;

	Bundle userBundle;
	Bundle hashtagBundle;
	Bundle storyBundle;

	List<Hashtag> listHashtag = new ArrayList<>();
	List<User> listUser = new ArrayList<>();
	List<Video> listStory = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		search = (SearchBox) findViewById(R.id.searchbox);
        search.enableVoiceRecognition(this);
		for(int x = 0; x < 10; x++){
			SearchResult option = new SearchResult("Result " + Integer.toString(x), getResources().getDrawable(R.drawable.ic_history));
			search.addSearchable(option);
		}		
		search.setMenuListener(new MenuListener(){

			@Override
			public void onMenuClick() {
				//Hamburger has been clicked
				Toast.makeText(SearchActivity.this, "Menu click", Toast.LENGTH_LONG).show();
			}
			
		});
		search.setSearchListener(new SearchListener(){

			@Override
			public void onSearchOpened() {
				//Use this to tint the screen
			}

			@Override
			public void onSearchClosed() {
				//Use this to un-tint the screen
			}

			@Override
			public void onSearchTermChanged() {
				//React to the search term changing
				//Called after it has updated results
			}

			@Override
			public void onSearch(String searchTerm) {
				Toast.makeText(SearchActivity.this, searchTerm +" Searched", Toast.LENGTH_LONG).show();
				startSearch(searchTerm);
				
			}

			@Override
			public void onSearchCleared() {
				//Called when the clear button is clicked
				
			}
			
		});

		tab = (ViewGroup) findViewById(R.id.tab);
		tab.addView(LayoutInflater.from(this).inflate(R.layout.demo_custom_tab_icons, tab, false));

		viewPager = (ViewPager) findViewById(R.id.viewpager);
		viewPagerTab = (SmartTabLayout) findViewById(R.id.viewpagertab);
		setup(viewPagerTab);

		//openSearch();
	}

	public void setup(SmartTabLayout layout) {


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

	public static void startActivity(Context context, String query) {
		Intent intent = new Intent(context, SearchActivity.class);
		intent.putExtra(KEY_QUERY, query);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
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

							if(hashtags != null)
							for (int j = 0; j < hashtags.length(); j++) {
								JSONObject b = (JSONObject) hashtags.get(j);
								String id = b.optString("id");
								String hash = b.optString("hash");
								String tag = b.optString("tag");

								Hashtag ht = new Hashtag(id, hash, tag);
								listHashtag.add(ht);
							}

							if(users != null)
							for (int k = 0; k < users.length(); k++) {
								JSONObject b = (JSONObject) users.get(k);
								String id = b.optString("id");
								String avatar = b.optString("avatar");
								String name = b.optString("name");
								String username = b.optString("username");

								User user = new User();
								user.setAvatar(avatar);
								user.setName(name);
								user.setUsername(username);
								user.setId(id);
								listUser.add(user);
							}

							if(stories != null)
							for (int l = 0; l < stories.length(); l++) {
								JSONObject b = (JSONObject) stories.get(l);
								String postId = b.optString("id");
								String id = b.optString("youtube_video_id");
								String title = b.optString("youtube_title");
								String desc = b.optString("youtube_description");
								String view = b.optString("view");
								String userId = b.optJSONObject("publisher").optString("id");
								String username = b.optJSONObject("publisher").optString("username");
								String avatar = b.optJSONObject("publisher").optString("avatar_url");
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
							userBundle.putString("USER_ID", getPref(getApplicationContext()).userId().getOr("0"));
							userBundle.putParcelable("LIST", Parcels.wrap(listUser));

							hashtagBundle = new Bundle();
							hashtagBundle.putString("TYPE", "SEARCH");
							hashtagBundle.putParcelable("LIST", Parcels.wrap(listHashtag));

							storyBundle = new Bundle();
							storyBundle.putString("TYPE", "SEARCH");
							storyBundle.putString("KEYWORD_SEARCH", q);
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

						pages.add(FragmentPagerItem.of("User", FriendSocialFragment.class, userBundle));
						pages.add(FragmentPagerItem.of("Story", VideoFragment.class, storyBundle));
						pages.add(FragmentPagerItem.of("Hashtag", VideoFragment.class, storyBundle));

						adapter = new FragmentPagerItemAdapter(
								getSupportFragmentManager(), pages);

						viewPager.setAdapter(adapter);
						viewPagerTab.setViewPager(viewPager);
					}
				});
			}
		});
	}
	
	public void reveal(View v){
		startActivity(new Intent(this, RevealActivity.class));
	}

	
}

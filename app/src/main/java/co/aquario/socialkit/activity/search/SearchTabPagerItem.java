package co.aquario.socialkit.activity.search;

import android.support.v4.app.Fragment;

import java.util.ArrayList;

import co.aquario.socialkit.fragment.main.FeedFragment;
import co.aquario.socialkit.fragment.main.FriendFragment;
import co.aquario.socialkit.model.Hashtag;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.model.User;

public class SearchTabPagerItem {
	
	private final CharSequence mTitle;
    private final int position;
    private final String userId;
        
    private Fragment[] listFragments;
    public SearchTabPagerItem(int position, CharSequence title, String userId,ArrayList<User> userList,ArrayList<PostStory> storyList,ArrayList<Hashtag> hashtagList) {
        this.mTitle = title;
        this.position = position;
        this.userId = userId;

       // ArrayList<User> userList;
        //ArrayList<PostStory> storyList;
        //ArrayList<Hashtag> hashtagList;

        listFragments = new Fragment[] {
                new FriendFragment().newInstance("SEARCH",userId,userList),
                new FeedFragment().newInstance("SEARCH",true,storyList),
                new HashtagFragment().newInstance(hashtagList)
                 };

    }

    public Fragment createFragment() {
		return listFragments[position];
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public String getUserId() { return userId;}
}

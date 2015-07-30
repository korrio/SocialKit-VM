package co.aquario.socialkit.activity.search;

import java.util.ArrayList;

import co.aquario.socialkit.model.Hashtag;
import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.model.User;

/**
 * Created by Mac on 7/30/15.
 */
public class SearchDataResponse {

    private ArrayList<PostStory> story;
    private ArrayList<Hashtag> hashtag;
    private ArrayList<User> user;

    public void setStory(ArrayList<PostStory> story) {
        this.story = story;
    }
//
    public void setHashtag(ArrayList<Hashtag> hashtag) {
        this.hashtag = hashtag;
    }

    public void setUser(ArrayList<User> user) {
        this.user = user;
    }

    public ArrayList<PostStory> getStory() {
        return story;
    }

    public ArrayList<Hashtag> getHashtag() {
        return hashtag;
    }

    public ArrayList<User> getUser() {
        return user;
    }


}

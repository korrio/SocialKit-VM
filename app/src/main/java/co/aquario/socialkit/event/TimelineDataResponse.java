package co.aquario.socialkit.event;

import java.util.List;

import co.aquario.socialkit.model.PostStory;
import co.aquario.socialkit.model.User;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class TimelineDataResponse {

    private String status;

    private User user;

    private List<PostStory> posts;

    public TimelineDataResponse(String status, User user, List<PostStory> posts) {
        this.status = status;
        this.user = user;
        this.posts = posts;
    }

    public String getStatus() {
        return status;
    }

    public User getUser() {
        return user;
    }

    public List<PostStory> getPosts() {
        return posts;
    }
}

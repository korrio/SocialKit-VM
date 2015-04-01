package co.aquario.socialkit.event;

import co.aquario.socialkit.model.PostStory;

/**
 * Created by Mac on 3/12/15.
 */
public class TimelineDataEvent {
    private PostStory post;

    public TimelineDataEvent(PostStory post) {
        this.post = post;
    }

    public PostStory getPost() {
        return post;
    }
}

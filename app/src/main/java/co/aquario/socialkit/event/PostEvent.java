package co.aquario.socialkit.event;

import co.aquario.socialkit.model.PostStory;

/**
 * Created by Mac on 3/20/15.
 */
public class PostEvent {
    private PostStory post;

    public PostEvent(PostStory post) {
        this.post = post;
    }

    public PostStory getPost() {
        return post;
    }
}

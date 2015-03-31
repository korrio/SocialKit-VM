package co.aquario.socialkit.event;

import co.aquario.socialkit.model.PostStory;

/**
 * Created by Mac on 3/3/15.
 */
public class GetStorySuccessEvent {
    private PostStory post;

    public GetStorySuccessEvent(PostStory post) {
        this.post = post;
    }

    public PostStory getPost() {
        return post;
    }
}

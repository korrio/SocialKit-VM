package co.aquario.socialkit.event;

import co.aquario.socialkit.model.PostStory;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class StoryDataResponse {

    private PostStory post;

    public PostStory getPost() {
        return post;
    }

    public StoryDataResponse(PostStory post) {
        this.post = post;
    }
}

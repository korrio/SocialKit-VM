package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/25/15.
 */
public class PostShareEvent {
    private String userId;
    private String postId;

    public PostShareEvent(String userId, String postId) {
        this.userId = userId;
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

}

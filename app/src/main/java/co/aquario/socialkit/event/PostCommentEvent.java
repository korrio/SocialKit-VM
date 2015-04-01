package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/25/15.
 */
public class PostCommentEvent {
    private String userId;
    private String postId;
    private String postText;

    public PostCommentEvent(String postText,String userId, String postId) {
        this.userId = userId;
        this.postId = postId;
        this.postText = postText;
    }

    public String getUserId() {
        return userId;
    }

    public String getPostId() {
        return postId;
    }

    public String getPostText() {
        return postText;
    }
}

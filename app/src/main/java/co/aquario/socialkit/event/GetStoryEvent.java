package co.aquario.socialkit.event;

public class GetStoryEvent {
    private String postId;

    public GetStoryEvent(String postId) {
        this.postId = postId;
    }

    public String getPostId() {
        return postId;
    }
}

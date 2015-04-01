package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/28/15.
 */
public class FollowUserSuccessEvent {
    private String userId;

    public FollowUserSuccessEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}

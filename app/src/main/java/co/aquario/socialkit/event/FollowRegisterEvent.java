package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/28/15.
 */
public class FollowRegisterEvent {
    private String userId;

    public FollowRegisterEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}

package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/28/15.
 */
public class UnfollowUserSuccessEvent {
    private String userId;

    public UnfollowUserSuccessEvent(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}

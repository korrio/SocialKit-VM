package co.aquario.socialkit.event;

public class GetUserProfileEvent {
    private String userId;

    public GetUserProfileEvent(String userId) {
        this.userId = userId;
    }
    public String getUserId() {
        return userId;
    }
}

package co.aquario.socialkit.event;

public class GetUserProfileEvent {
    private String userId;
    private String username;

    public GetUserProfileEvent(String userId) {
        this.userId = userId;
        this.username = "";
    }

    public GetUserProfileEvent(String username,boolean isFromUsername) {
        this.userId = "";
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }
}

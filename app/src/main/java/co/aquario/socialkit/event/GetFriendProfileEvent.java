package co.aquario.socialkit.event;

public class GetFriendProfileEvent {
    private String userId;
    private String username;

    public GetFriendProfileEvent(String userId) {
        this.userId = userId;
        this.username = "";
    }

    public GetFriendProfileEvent(String username, boolean isFromUsername) {
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

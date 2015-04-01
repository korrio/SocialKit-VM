package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadFriendListSuccessEvent {
    private String type;
    private FriendListDataResponse friendListData;

    public LoadFriendListSuccessEvent(FriendListDataResponse friendListData,String type) {
        this.friendListData = friendListData;
        this.type = type;
    }

    public FriendListDataResponse getFriendListData() {
        return friendListData;
    }

    public String getType() {
        return type;
    }
}


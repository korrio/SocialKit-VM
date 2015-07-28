package co.aquario.chatui.event_chat.response;

import co.aquario.chatui.model.FindFriends;

/**
 * Created by Mac on 7/28/15.
 */
public class SearchUserEventSuccess {
    public FindFriends user;

    public SearchUserEventSuccess(FindFriends user) {
        this.user = user;
    }
}

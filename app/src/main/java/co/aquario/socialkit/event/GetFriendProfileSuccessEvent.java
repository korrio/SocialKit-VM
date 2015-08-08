package co.aquario.socialkit.event;

import co.aquario.socialkit.model.BadgeCount;
import co.aquario.socialkit.model.User;

/**
 * Created by Mac on 3/3/15.
 */
public class GetFriendProfileSuccessEvent {
    private User user;
    private BadgeCount count;

    public GetFriendProfileSuccessEvent(User user, BadgeCount count) {
        this.user = user;
        this.count = count;
    }

    public User getUser() {
        return user;
    }

    public BadgeCount getCount() {
        return count;
    }
}

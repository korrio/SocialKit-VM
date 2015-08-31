package co.aquario.chatui.event_chat.request;

import java.util.ArrayList;

public class InviteFriendToGroupChatEvent {
    public ArrayList<String> userIds;
    public String userId;
    public String groupName;
    public ArrayList<Integer> userIdsInt = new ArrayList<>();

    public InviteFriendToGroupChatEvent(String userId, ArrayList<String> userIds, String name) {
        this.userIds = userIds;
        this.userId = userId;
        this.groupName = name;

        userIdsInt = new ArrayList<>();
        for(int i = 0 ; i < userIds.size() ; i ++) {
            userIdsInt.add(Integer.parseInt(userIds.get(i)));

        }
    }
}

package co.aquario.chatapp.event.response;

import java.util.List;

/**
 * Created by Mac on 7/28/15.
 */
public class ChatInfo {

    /**
     * conversationMembers : [{"inviteFlag":0,"extension":"jpg","username":"kdmz","activeFlag":0,"name":"Mr. Newton","userId":3,"conversationId":231,"adminFlag":0,"inviteAcceptFlag":0,"canInviteFlag":0,"avatar":"photos/2014/11/UyNpq_8030_b4d22bb574aed5fdd900a274930252f6_thumb"},{"inviteFlag":0,"extension":"jpg","username":"korrio","activeFlag":0,"name":"korr korr","userId":6,"conversationId":231,"adminFlag":0,"inviteAcceptFlag":0,"canInviteFlag":0,"avatar":"photos/2015/07/yzjf8_114065_1e79995d05e039ba806c7c84e671b14d_thumb"},{"inviteFlag":0,"extension":"jpg","username":"koredevman","activeFlag":0,"name":"Worachet Saengprab","userId":1,"conversationId":231,"adminFlag":0,"inviteAcceptFlag":0,"canInviteFlag":0,"avatar":"photos/2014/10/1Dutm_1_c4ca4238a0b923820dcc509a6f75849b_thumb"}]
     * id : 231
     * createdBy :
     * createdAt : 2015-07-17T15:12:32.000Z
     * conversationType : PUBLIC
     * name :
     * conversationId : 231
     * memberType : GROUP
     * avatar : /config_uploads/default/no_group_avatar.jpg
     */
    private List<ConversationMembersEntity> conversationMembers;
    private int id;
    private String createdBy;
    private String createdAt;
    private String conversationType;
    private String name;
    private int conversationId;
    private String memberType;
    private String avatar;

    public void setConversationMembers(List<ConversationMembersEntity> conversationMembers) {
        this.conversationMembers = conversationMembers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public void setMemberType(String memberType) {
        this.memberType = memberType;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public List<ConversationMembersEntity> getConversationMembers() {
        return conversationMembers;
    }

    public int getId() {
        return id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getConversationType() {
        return conversationType;
    }

    public String getName() {
        return name;
    }

    public int getConversationId() {
        return conversationId;
    }

    public String getMemberType() {
        return memberType;
    }

    public String getAvatar() {
        return avatar;
    }

    public static class ConversationMembersEntity {
        /**
         * inviteFlag : 0
         * extension : jpg
         * username : kdmz
         * activeFlag : 0
         * name : Mr. Newton
         * userId : 3
         * conversationId : 231
         * adminFlag : 0
         * inviteAcceptFlag : 0
         * canInviteFlag : 0
         * avatar : photos/2014/11/UyNpq_8030_b4d22bb574aed5fdd900a274930252f6_thumb
         */
        private int inviteFlag;
        private String extension;
        private String username;
        private int activeFlag;
        private String name;
        private int userId;
        private int conversationId;
        private int adminFlag;
        private int inviteAcceptFlag;
        private int canInviteFlag;
        private String avatar;

        public void setInviteFlag(int inviteFlag) {
            this.inviteFlag = inviteFlag;
        }

        public void setExtension(String extension) {
            this.extension = extension;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setActiveFlag(int activeFlag) {
            this.activeFlag = activeFlag;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public void setConversationId(int conversationId) {
            this.conversationId = conversationId;
        }

        public void setAdminFlag(int adminFlag) {
            this.adminFlag = adminFlag;
        }

        public void setInviteAcceptFlag(int inviteAcceptFlag) {
            this.inviteAcceptFlag = inviteAcceptFlag;
        }

        public void setCanInviteFlag(int canInviteFlag) {
            this.canInviteFlag = canInviteFlag;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public int getInviteFlag() {
            return inviteFlag;
        }

        public String getExtension() {
            return extension;
        }

        public String getUsername() {
            return username;
        }

        public int getActiveFlag() {
            return activeFlag;
        }

        public String getName() {
            return name;
        }

        public int getUserId() {
            return userId;
        }

        public int getConversationId() {
            return conversationId;
        }

        public int getAdminFlag() {
            return adminFlag;
        }

        public int getInviteAcceptFlag() {
            return inviteAcceptFlag;
        }

        public int getCanInviteFlag() {
            return canInviteFlag;
        }

        public String getAvatar() {
            return avatar;
        }
    }
}

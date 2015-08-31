package co.aquario.chatui;

public class Country {

    public String userId;
    public String username = null;
    public String name = null;
    public String avatar = null;
    public boolean selected = false;

    public Country(String userId, String username, String name, String avatar, boolean selected) {
        this.userId = userId;
        this.username = username;
        this.name = name;
        this.avatar = avatar;
        this.selected = selected;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
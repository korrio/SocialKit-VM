package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by Mac on 3/10/15.
 */
@Parcel
public class User extends BaseModel {

    @Expose
    public String id;
    @Expose
    public String username;
    @Expose
    public String about;
    @Expose
    public String active;
    @Expose
    public String email;
    @Expose
    public String language;
    @SerializedName("last_logged")
    @Expose
    public String lastLogged;
    @Expose
    public String name;
    @Expose
    public String time;
    @Expose
    public String timestamp;
    @Expose
    public String timezone;
    @Expose
    public String type;
    @Expose
    public String verified;
    @Expose
    public String avatar;
    @Expose
    public String cover;
    @Expose
    public String live;
    @Expose
    public String gender;
    @Expose
    public String birthday;
    @Expose
    public boolean online;
    @Expose
    @SerializedName("is_live")
    public boolean isLive;

    public boolean isFollowing;

    public User() {

    }

    @ParcelConstructor
    public User(String id, String username, String about, String active, String email, String language, String lastLogged, String name, String time, String timestamp, String timezone, String type, String verified, String avatar, String cover, String live, String gender, String birthday, boolean online, boolean isLive) {
        this.id = id;
        this.username = username;
        this.about = about;
        this.active = active;
        this.email = email;
        this.language = language;
        this.lastLogged = lastLogged;
        this.name = name;
        this.time = time;
        this.timestamp = timestamp;
        this.timezone = timezone;
        this.type = type;
        this.verified = verified;
        this.avatar = avatar;
        this.cover = cover;
        this.live = live;
        this.gender = gender;
        this.birthday = birthday;
        this.online = online;
        this.isLive = isLive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastLogged() {
        return lastLogged;
    }

    public void setLastLogged(String lastLogged) {
        this.lastLogged = lastLogged;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getLive() {
        return live;
    }

    public void setLive(String live) {
        this.live = live;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public boolean isLive() {
        return isLive;
    }

    public void setIsLive(boolean isLive) {
        this.isLive = isLive;
    }

    public boolean getIsFollowing() {
        return false;
    }


    public void setIsFollowing(boolean isFollowing) {
        this.isFollowing = isFollowing;
    }

    public String getAvatarUrl() {
        return EndpointManager.getAvatarPath(avatar);
    }

    public String getCoverUrl() {
        return EndpointManager.getAvatarPath(cover);
    }
}

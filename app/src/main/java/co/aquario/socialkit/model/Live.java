package co.aquario.socialkit.model;

/**
 * Created by root1 on 3/5/15.
 */
public class Live {

    String userId;
    String urlLive;
    String photoLive;
    String nameLive;
    String hours;
    String minutes;
    String seconds;
    String timestamp;
    String avatar;
    String date;

    public Live(String urlLive, String photoLive, String nameLive, String hours, String minutes, String seconds, String timestamp,String avatar,String date,String userId) {
        this.urlLive = urlLive;
        this.photoLive = photoLive;
        this.nameLive = nameLive;
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.timestamp = timestamp;
        this.avatar = avatar;
        this.date = date;
        this.userId = userId;
    }

    public String getUrlLive() {
        return urlLive;
    }

    public String getPhotoLive() {
        return photoLive;
    }

    public String getNameLive() {
        return nameLive;
    }

    public String getHours() {
        return hours;
    }

    public String getMinutes() {
        return minutes;
    }

    public String getSeconds() {
        return seconds;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

package co.aquario.chatapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 7/19/15.
 */
public class NotiCount {
    @Expose
    @SerializedName("noti_unread")
    public int notiCount;
    @Expose
    @SerializedName("chat_unread")
    public int chatCount;
    @Expose
    @SerializedName("follow_unread")
    public int followCount;
}

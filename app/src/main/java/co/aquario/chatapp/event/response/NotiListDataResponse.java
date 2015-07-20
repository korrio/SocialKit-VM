package co.aquario.chatapp.event.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.aquario.chatapp.model.Noti;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class NotiListDataResponse {

    @Expose
    public int page;
    @Expose
    public int count;
    @Expose
    @SerializedName("total_unread")
    public int unreadCount;
    public List<Noti> data;

}

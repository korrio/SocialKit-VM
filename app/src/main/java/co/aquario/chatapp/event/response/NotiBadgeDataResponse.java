package co.aquario.chatapp.event.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import co.aquario.chatapp.model.NotiCount;

/**
 * Created by Mac on 7/19/15.
 */
public class NotiBadgeDataResponse {
    @Expose
    @SerializedName("user_id")
    public String userId;
    public NotiCount count;
}

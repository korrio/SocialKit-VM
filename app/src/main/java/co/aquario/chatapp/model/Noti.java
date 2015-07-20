package co.aquario.chatapp.model;

import com.google.gson.annotations.Expose;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by Mac on 7/19/15.
 */
public class Noti {

    /*
    "id": "595",
"from_id": "15522",
"from_name": "yut",
"to_id": "6",
"msg": "คุณได้รับการชวนเข้ากรุ้ป oooo",
"type": "520",
"post_id": "281",
"extra": "",
"time_received": "1437293419",
"time_readed": null,
"readed": "0",
"ago": "6 hours ago"
     */
    @Expose
    public String id;
    @Expose
    public String from_id;
    @Expose
    public String from_name;
    @Expose
    public String from_avatar;
    @Expose
    public String to_id;
    @Expose
    public String msg;
    @Expose
    public String type;
    @Expose
    public String post_id;
    @Expose
    public String extra;
    @Expose
    public String time_received;
    @Expose
    public String time_readed;
    @Expose
    public String readed;
    @Expose
    public String ago;

    public String getAvatarUrl() {
        return EndpointManager.getAvatarPath(from_avatar);
    }
}

package co.aquario.socialkit.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

import co.aquario.socialkit.model.User;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class FriendListDataResponse {

    @Expose
    public String status;
    @Expose
    public String page;
    @Expose
    @SerializedName("per_page")
    public String perPage;
    @Expose
    public String pages;
    @Expose
    public String total;
    @Expose
    public ArrayList<User> users;

}

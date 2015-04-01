package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 3/10/15.
 */
public class Youtube {
    @Expose
    public String id;
    @Expose
    public String title;
    @Expose
    @SerializedName("description")
    public String desc;
    @Expose
    public String thumbnail;
}

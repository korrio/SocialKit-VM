package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 7/24/15.
 */
public class UploadPostCallback {
    @Expose
    public int status;
    @Expose
    @SerializedName("post_id")
    public int postId;
}

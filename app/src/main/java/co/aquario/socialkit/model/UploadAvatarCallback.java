package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 7/24/15.
 */
public class UploadAvatarCallback {
    @Expose
    public int status;
    @Expose
    @SerializedName("avatar_url")
    public String avatarUrl;
}

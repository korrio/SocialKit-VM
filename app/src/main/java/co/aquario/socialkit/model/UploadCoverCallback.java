package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 7/24/15.
 */
public class UploadCoverCallback {
    @Expose
    public int status;
    @Expose
    @SerializedName("cover_url")
    public String coverUrl;
    @Expose
    @SerializedName("actual_cover_url")
    public String coverUrlFull;
}

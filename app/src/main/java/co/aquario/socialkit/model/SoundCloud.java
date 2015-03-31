package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 3/10/15.
 */
public class SoundCloud {
    @Expose
    public String title;
    @Expose
    public String uri;
    @SerializedName("track_id")
    @Expose
    public String trackId;
    @SerializedName("stream_url")
    @Expose
    public String streamUrl;
}

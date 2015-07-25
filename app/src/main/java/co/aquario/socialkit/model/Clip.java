package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by Mac on 3/10/15.
 */
@Parcel
public class Clip extends BaseModel {
    @Expose
    public String id;
    @Expose
    public String url;
    @Expose
    @SerializedName("thumbnail")
    public String thumb;
    @SerializedName("url_thumb")
    public String url_thumb;
    @Expose
    public String type;

    public String getThumbUrl() {
        return EndpointManager.getAvatarPath(url_thumb);
    }
}

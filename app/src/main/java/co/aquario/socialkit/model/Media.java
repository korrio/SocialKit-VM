package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;

import co.aquario.socialkit.util.EndpointManager;

/**
 * Created by root1 on 2/22/15.
 */
@Parcel
public class Media {

    @Expose
    public String id;
    @Expose
    public String active;
    @Expose
    public String albumId;
    @Expose
    public String clipId;
    @Expose
    public String extension;
    @Expose
    public String name;
    @Expose
    public String postId;
    @Expose
    public String temp;
    @Expose
    public String timeline;
    @Expose
    public String type;
    @Expose
    public String url;
    @Expose
    public String url_thumb;
    @Expose
    @SerializedName("album_photos")
    public List<Media> album;



    public String getFullUrl() {
        return EndpointManager.getAvatarPath(url);
    }

    public String getThumbUrl() {
        return EndpointManager.getAvatarPath(url_thumb);
    }




}

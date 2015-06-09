package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 5/19/15.
 */
public class PhotoStory extends BaseModel {

    @Expose
    public String text;

    @Expose
    public Media media;

    @Expose
    public String media_type;

    @Expose
    @SerializedName("publisher")
    public Author author;

    public String getPath() {
        return "https://www.vdomax.com/" + media.url;
    }

    public String getThumbPath() {
        return "https://www.vdomax.com/" + media.url_thumb;
    }

}

package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 5/19/15.
 */
public class PhotoStory extends BaseModel {

    @Expose
    public Media media;

    public String getPath() {
        return "https://www.vdomax.com/" + media.url_thumb;
    }

}

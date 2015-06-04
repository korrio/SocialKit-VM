package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 6/3/15.
 */
public class Photo {
    @Expose
    public String id;
    @Expose
    @SerializedName("publisher")
    public Author author;
    @Expose
    public Media media;

}

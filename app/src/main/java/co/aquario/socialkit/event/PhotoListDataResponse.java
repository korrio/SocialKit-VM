package co.aquario.socialkit.event;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import co.aquario.socialkit.model.Photo;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class PhotoListDataResponse {

    @Expose
    public String status;
    @Expose
    public String page;
    @Expose
    @SerializedName("per_page")
    public String perPage;
    @Expose
    public String pages;
    @Expose
    public String total;
    @Expose
    public String sort;
    @Expose
    public List<Photo> photos;

}

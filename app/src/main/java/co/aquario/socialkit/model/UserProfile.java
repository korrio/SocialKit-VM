package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;


public class UserProfile extends BaseModel {
    @Expose
    public String id;
    @Expose
    public String name;
    //@SerializedName("avatar_url")
    @Expose
    public String avatar;
    //@SerializedName("cover_url")
    @Expose
    public String cover;
    @Expose
    public String username;
    @Expose
    public String email;

}

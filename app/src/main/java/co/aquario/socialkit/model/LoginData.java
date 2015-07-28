package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Mac on 3/3/15.
 */
public class LoginData {
    @Expose public String status;
    @Expose public String message;
    @Expose public String token;
    @Expose public String state;
    @SerializedName("api_token")
    @Expose public String apiToken;
    @Expose public UserProfile user;
}

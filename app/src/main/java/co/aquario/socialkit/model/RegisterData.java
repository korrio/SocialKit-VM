package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 3/3/15.
 */
public class RegisterData {
    @Expose public String status;
    @Expose public String message;
    @Expose public UserProfile user;
}

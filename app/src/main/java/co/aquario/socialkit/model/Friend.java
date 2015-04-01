package co.aquario.socialkit.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by Mac on 3/10/15.
 */
public class Friend extends PaginatedCollection {
    @Expose
    public ArrayList<User> users;
}

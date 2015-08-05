package co.aquario.socialkit.event.mention;

import com.arabagile.typeahead.model.MentionUser;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by matthewlogan on 9/3/14.
 */
public class MentionListDataResponse {

    @Expose
    public String status;
    @Expose
    public ArrayList<MentionUser> mentions;

}

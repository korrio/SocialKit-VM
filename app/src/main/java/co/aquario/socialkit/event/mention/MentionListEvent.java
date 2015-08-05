package co.aquario.socialkit.event.mention;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 8/5/15.
 */
public class MentionListEvent {
    @Expose
    public int id;

    public MentionListEvent(int id) {
        this.id = id;
    }
}

package co.aquario.socialkit.event.mention;

import com.google.gson.annotations.Expose;

/**
 * Created by Mac on 8/5/15.
 */
public class LoadMentionListSuccessEvent {
    @Expose
    public MentionListDataResponse response;

    public LoadMentionListSuccessEvent(MentionListDataResponse response) {
        this.response = response;
    }
}

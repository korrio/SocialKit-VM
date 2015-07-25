package co.aquario.socialkit.event.upload;

import retrofit.mime.TypedFile;

/**
 * Created by Mac on 7/24/15.
 */
public class ClipPostUploadEvent {
    public String text;
    public String fromUserId;
    public String toUserId;
    public TypedFile file;

    public ClipPostUploadEvent(String text, String fromUserId, String toUserId, TypedFile file) {
        this.text = text;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.file = file;
    }
}

package co.aquario.socialkit.model;

/**
 * Created by Mac on 3/12/15.
 */
public class Video extends BaseModel {
    private String postId;
    private String title;
    private String desc;
    private String youtubeId;
    private String text;
    private  String timestamp;
    private String view;
    private  String pUserId;
    private String pName;
    private  String pAvatar;

    public Video(String postId, String title, String desc, String youtubeId, String text, String timestamp, String view, String pUserId, String pName, String pAvatar) {
        this.postId = postId;
        this.title = title;
        this.desc = desc;
        this.youtubeId = youtubeId;
        this.text = text;
        this.timestamp = timestamp;
        this.view = view;
        this.pUserId = pUserId;
        this.pName = pName;
        this.pAvatar = pAvatar;
    }

    public String getPostId() {
        return postId;
    }

    public String getTitle() {
        return title;
    }

    public String getDesc() {
        return desc;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getView() {
        return view;
    }

    public String getpUserId() {
        return pUserId;
    }

    public String getpName() {
        return pName;
    }

    public String getpAvatar() {
        return pAvatar;
    }
}

package co.aquario.socialkit.model;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

@Parcel
public class Video extends BaseModel {
    public String postId;
    public String title;
    public String desc;
    public String youtubeId;
    public String text;
    public  String timestamp;
    public String view;
    public  String pUserId;
    public String pName;
    public  String pAvatar;
    public int nLove;
    public int nComment;
    public int nShare;


    public Video() {

    }
    @ParcelConstructor
    public Video(String postId, String title, String desc, String youtubeId, String text, String timestamp, String view, String pUserId, String pName, String pAvatar, int nLove, int nComment, int nShare) {
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
        this.nLove = nLove;
        this.nComment = nComment;
        this.nShare = nShare;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getView() {
        return view;
    }

    public void setView(String view) {
        this.view = view;
    }

    public String getpUserId() {
        return pUserId;
    }

    public void setpUserId(String pUserId) {
        this.pUserId = pUserId;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getpAvatar() {
        return pAvatar;
    }

    public void setpAvatar(String pAvatar) {
        this.pAvatar = pAvatar;
    }

    public int getnLove() {
        return nLove;
    }

    public void setnLove(int nLove) {
        this.nLove = nLove;
    }

    public int getnComment() {
        return nComment;
    }

    public void setnComment(int nComment) {
        this.nComment = nComment;
    }

    public int getnShare() {
        return nShare;
    }

    public void setnShare(int nShare) {
        this.nShare = nShare;
    }
}

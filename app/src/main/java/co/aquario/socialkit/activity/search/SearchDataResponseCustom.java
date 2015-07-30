package co.aquario.socialkit.activity.search;

import java.util.ArrayList;
import java.util.List;

import co.aquario.socialkit.model.User;

/**
 * Created by Mac on 7/30/15.
 */
public class SearchDataResponseCustom {
    
    private List<StoryEntity> story;
    private List<HashtagEntity> hashtag;
    private ArrayList<User> user;

    public void setStory(List<StoryEntity> story) {
        this.story = story;
    }

    public void setHashtag(List<HashtagEntity> hashtag) {
        this.hashtag = hashtag;
    }

    public void setUser(ArrayList<User> user) {
        this.user = user;
    }

    public List<StoryEntity> getStory() {
        return story;
    }

    public List<HashtagEntity> getHashtag() {
        return hashtag;
    }

    public ArrayList<User> getUser() {
        return user;
    }

    public static class StoryEntity {
        /**
         * tattoo_url : null
         * media_type : Youtube
         * love_count : 0
         * share_count : 0
         * recipient_id : 0
         * clip_id : 0
         * post_id : 20027
         * love : null
         * id : 20027
         * timestamp : 2015-05-09 10:10:51
         * author : {"email_verified":"0","about":"เขียนเกี่ยวกับหน้าของคุณ","avatar":"photos/2014/10/ZkmtT_3488_c7b90b0fc23725f299b47c5224e6ec0d_100x100.jpg","type":"page","password":"d74c35cd16aba06ef8d371aea26e6900","avatar_id":"3488","timestamp":"2014-12-15 12:36:19","id":"180","last_logged":"0","cover":"photos/2014/12/s3GIl_46479_50cd562760a5819b07a9da2239046ef0_cover.jpg","username":"ARAYAfanclub","timezone":"","time":"1414419320","cover_position":"0","email":"ARAYAfanclub","verified":"0","name":"ARAYA A. HARGATE","active":"1","language":"","cover_id":"46479","email_verification_key":""}
         * share : null
         * follow_count : 0
         * time : 1414661324
         * type1 : story
         * youtube : {"id":"aK2-9U2IiLM","title":"Chompoo Araya : เฉิดฉายบนพรมแดงเทศกาลหนังเมืองคานส์","thumbnail":"http://img.youtube.com/vi/aK2-9U2IiLM/0.jpg","description":"9 Entertain 22/05/13<br>www.clubchom.com"}
         * post_type : youtube
         * type2 : none
         * follow : null
         * youtube_description : 9 Entertain 22/05/13<br>www.clubchom.com
         * clip : null
         * comment_count : 0
         * youtube_video_id : aK2-9U2IiLM
         * text : #[ChompooAraya]
         * emoticonized : #[ChompooAraya]
         * youtube_title : Chompoo Araya : เฉิดฉายบนพรมแดงเทศกาลหนังเมืองคานส์
         * link_url :
         * media_id : 0
         * seen : 0
         * soundcloud : null
         * soundcloud_title :
         * country : 1
         * google_map_name :
         * hidden : 0
         * account : {"email_verified":"0","about":"เขียนเกี่ยวกับหน้าของคุณ","avatar":"photos/2014/10/ZkmtT_3488_c7b90b0fc23725f299b47c5224e6ec0d_100x100.jpg","type":"page","password":"d74c35cd16aba06ef8d371aea26e6900","avatar_id":"3488","timestamp":"2014-12-15 12:36:19","id":"180","last_logged":"0","cover":"photos/2014/12/s3GIl_46479_50cd562760a5819b07a9da2239046ef0_cover.jpg","username":"ARAYAfanclub","timezone":"","time":"1414419320","cover_position":"0","email":"ARAYAfanclub","verified":"0","name":"ARAYA A. HARGATE","active":"1","language":"","cover_id":"46479","email_verification_key":""}
         * active : 1
         * link_title :
         * view : 176
         * comment : null
         * media : null
         * soundcloud_uri :
         * timeline_id : 180
         */
        private String tattoo_url;
        private String media_type;
        private int love_count;
        private int share_count;
        private String recipient_id;
        private String clip_id;
        private String post_id;
        private String love;
        private String id;
        private String timestamp;
        private AuthorEntity author;
        private String share;
        private int follow_count;
        private String time;
        private String type1;
        private YoutubeEntity youtube;
        private String post_type;
        private String type2;
        private String follow;
        private String youtube_description;
        private String clip;
        private int comment_count;
        private String youtube_video_id;
        private String text;
        private String emoticonized;
        private String youtube_title;
        private String link_url;
        private String media_id;
        private String seen;
        private String soundcloud;
        private String soundcloud_title;
        private String country;
        private String google_map_name;
        private String hidden;
        private AccountEntity account;
        private String active;
        private String link_title;
        private String view;
        private String comment;
        private String media;
        private String soundcloud_uri;
        private String timeline_id;

        public void setTattoo_url(String tattoo_url) {
            this.tattoo_url = tattoo_url;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public void setLove_count(int love_count) {
            this.love_count = love_count;
        }

        public void setShare_count(int share_count) {
            this.share_count = share_count;
        }

        public void setRecipient_id(String recipient_id) {
            this.recipient_id = recipient_id;
        }

        public void setClip_id(String clip_id) {
            this.clip_id = clip_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }

        public void setLove(String love) {
            this.love = love;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public void setAuthor(AuthorEntity author) {
            this.author = author;
        }

        public void setShare(String share) {
            this.share = share;
        }

        public void setFollow_count(int follow_count) {
            this.follow_count = follow_count;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setType1(String type1) {
            this.type1 = type1;
        }

        public void setYoutube(YoutubeEntity youtube) {
            this.youtube = youtube;
        }

        public void setPost_type(String post_type) {
            this.post_type = post_type;
        }

        public void setType2(String type2) {
            this.type2 = type2;
        }

        public void setFollow(String follow) {
            this.follow = follow;
        }

        public void setYoutube_description(String youtube_description) {
            this.youtube_description = youtube_description;
        }

        public void setClip(String clip) {
            this.clip = clip;
        }

        public void setComment_count(int comment_count) {
            this.comment_count = comment_count;
        }

        public void setYoutube_video_id(String youtube_video_id) {
            this.youtube_video_id = youtube_video_id;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setEmoticonized(String emoticonized) {
            this.emoticonized = emoticonized;
        }

        public void setYoutube_title(String youtube_title) {
            this.youtube_title = youtube_title;
        }

        public void setLink_url(String link_url) {
            this.link_url = link_url;
        }

        public void setMedia_id(String media_id) {
            this.media_id = media_id;
        }

        public void setSeen(String seen) {
            this.seen = seen;
        }

        public void setSoundcloud(String soundcloud) {
            this.soundcloud = soundcloud;
        }

        public void setSoundcloud_title(String soundcloud_title) {
            this.soundcloud_title = soundcloud_title;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public void setGoogle_map_name(String google_map_name) {
            this.google_map_name = google_map_name;
        }

        public void setHidden(String hidden) {
            this.hidden = hidden;
        }

        public void setAccount(AccountEntity account) {
            this.account = account;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public void setLink_title(String link_title) {
            this.link_title = link_title;
        }

        public void setView(String view) {
            this.view = view;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public void setSoundcloud_uri(String soundcloud_uri) {
            this.soundcloud_uri = soundcloud_uri;
        }

        public void setTimeline_id(String timeline_id) {
            this.timeline_id = timeline_id;
        }

        public String getTattoo_url() {
            return tattoo_url;
        }

        public String getMedia_type() {
            return media_type;
        }

        public int getLove_count() {
            return love_count;
        }

        public int getShare_count() {
            return share_count;
        }

        public String getRecipient_id() {
            return recipient_id;
        }

        public String getClip_id() {
            return clip_id;
        }

        public String getPost_id() {
            return post_id;
        }

        public String getLove() {
            return love;
        }

        public String getId() {
            return id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public AuthorEntity getAuthor() {
            return author;
        }

        public String getShare() {
            return share;
        }

        public int getFollow_count() {
            return follow_count;
        }

        public String getTime() {
            return time;
        }

        public String getType1() {
            return type1;
        }

        public YoutubeEntity getYoutube() {
            return youtube;
        }

        public String getPost_type() {
            return post_type;
        }

        public String getType2() {
            return type2;
        }

        public String getFollow() {
            return follow;
        }

        public String getYoutube_description() {
            return youtube_description;
        }

        public String getClip() {
            return clip;
        }

        public int getComment_count() {
            return comment_count;
        }

        public String getYoutube_video_id() {
            return youtube_video_id;
        }

        public String getText() {
            return text;
        }

        public String getEmoticonized() {
            return emoticonized;
        }

        public String getYoutube_title() {
            return youtube_title;
        }

        public String getLink_url() {
            return link_url;
        }

        public String getMedia_id() {
            return media_id;
        }

        public String getSeen() {
            return seen;
        }

        public String getSoundcloud() {
            return soundcloud;
        }

        public String getSoundcloud_title() {
            return soundcloud_title;
        }

        public String getCountry() {
            return country;
        }

        public String getGoogle_map_name() {
            return google_map_name;
        }

        public String getHidden() {
            return hidden;
        }

        public AccountEntity getAccount() {
            return account;
        }

        public String getActive() {
            return active;
        }

        public String getLink_title() {
            return link_title;
        }

        public String getView() {
            return view;
        }

        public String getComment() {
            return comment;
        }

        public String getMedia() {
            return media;
        }

        public String getSoundcloud_uri() {
            return soundcloud_uri;
        }

        public String getTimeline_id() {
            return timeline_id;
        }

        public static class AuthorEntity {
            /**
             * email_verified : 0
             * about : เขียนเกี่ยวกับหน้าของคุณ
             * avatar : photos/2014/10/ZkmtT_3488_c7b90b0fc23725f299b47c5224e6ec0d_100x100.jpg
             * type : page
             * password : d74c35cd16aba06ef8d371aea26e6900
             * avatar_id : 3488
             * timestamp : 2014-12-15 12:36:19
             * id : 180
             * last_logged : 0
             * cover : photos/2014/12/s3GIl_46479_50cd562760a5819b07a9da2239046ef0_cover.jpg
             * username : ARAYAfanclub
             * timezone :
             * time : 1414419320
             * cover_position : 0
             * email : ARAYAfanclub
             * verified : 0
             * name : ARAYA A. HARGATE
             * active : 1
             * language :
             * cover_id : 46479
             * email_verification_key :
             */
            private String email_verified;
            private String about;
            private String avatar;
            private String type;
            private String password;
            private String avatar_id;
            private String timestamp;
            private String id;
            private String last_logged;
            private String cover;
            private String username;
            private String timezone;
            private String time;
            private String cover_position;
            private String email;
            private String verified;
            private String name;
            private String active;
            private String language;
            private String cover_id;
            private String email_verification_key;

            public void setEmail_verified(String email_verified) {
                this.email_verified = email_verified;
            }

            public void setAbout(String about) {
                this.about = about;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public void setAvatar_id(String avatar_id) {
                this.avatar_id = avatar_id;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setLast_logged(String last_logged) {
                this.last_logged = last_logged;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public void setCover_position(String cover_position) {
                this.cover_position = cover_position;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public void setVerified(String verified) {
                this.verified = verified;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setActive(String active) {
                this.active = active;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public void setCover_id(String cover_id) {
                this.cover_id = cover_id;
            }

            public void setEmail_verification_key(String email_verification_key) {
                this.email_verification_key = email_verification_key;
            }

            public String getEmail_verified() {
                return email_verified;
            }

            public String getAbout() {
                return about;
            }

            public String getAvatar() {
                return avatar;
            }

            public String getType() {
                return type;
            }

            public String getPassword() {
                return password;
            }

            public String getAvatar_id() {
                return avatar_id;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public String getId() {
                return id;
            }

            public String getLast_logged() {
                return last_logged;
            }

            public String getCover() {
                return cover;
            }

            public String getUsername() {
                return username;
            }

            public String getTimezone() {
                return timezone;
            }

            public String getTime() {
                return time;
            }

            public String getCover_position() {
                return cover_position;
            }

            public String getEmail() {
                return email;
            }

            public String getVerified() {
                return verified;
            }

            public String getName() {
                return name;
            }

            public String getActive() {
                return active;
            }

            public String getLanguage() {
                return language;
            }

            public String getCover_id() {
                return cover_id;
            }

            public String getEmail_verification_key() {
                return email_verification_key;
            }
        }

        public static class YoutubeEntity {
            /**
             * id : aK2-9U2IiLM
             * title : Chompoo Araya : เฉิดฉายบนพรมแดงเทศกาลหนังเมืองคานส์
             * thumbnail : http://img.youtube.com/vi/aK2-9U2IiLM/0.jpg
             * description : 9 Entertain 22/05/13<br>www.clubchom.com
             */
            private String id;
            private String title;
            private String thumbnail;
            private String description;

            public void setId(String id) {
                this.id = id;
            }

            public void setTitle(String title) {
                this.title = title;
            }

            public void setThumbnail(String thumbnail) {
                this.thumbnail = thumbnail;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getId() {
                return id;
            }

            public String getTitle() {
                return title;
            }

            public String getThumbnail() {
                return thumbnail;
            }

            public String getDescription() {
                return description;
            }
        }

        public static class AccountEntity {
            /**
             * email_verified : 0
             * about : เขียนเกี่ยวกับหน้าของคุณ
             * avatar : photos/2014/10/ZkmtT_3488_c7b90b0fc23725f299b47c5224e6ec0d_100x100.jpg
             * type : page
             * password : d74c35cd16aba06ef8d371aea26e6900
             * avatar_id : 3488
             * timestamp : 2014-12-15 12:36:19
             * id : 180
             * last_logged : 0
             * cover : photos/2014/12/s3GIl_46479_50cd562760a5819b07a9da2239046ef0_cover.jpg
             * username : ARAYAfanclub
             * timezone :
             * time : 1414419320
             * cover_position : 0
             * email : ARAYAfanclub
             * verified : 0
             * name : ARAYA A. HARGATE
             * active : 1
             * language :
             * cover_id : 46479
             * email_verification_key :
             */
            private String email_verified;
            private String about;
            private String avatar;
            private String type;
            private String password;
            private String avatar_id;
            private String timestamp;
            private String id;
            private String last_logged;
            private String cover;
            private String username;
            private String timezone;
            private String time;
            private String cover_position;
            private String email;
            private String verified;
            private String name;
            private String active;
            private String language;
            private String cover_id;
            private String email_verification_key;

            public void setEmail_verified(String email_verified) {
                this.email_verified = email_verified;
            }

            public void setAbout(String about) {
                this.about = about;
            }

            public void setAvatar(String avatar) {
                this.avatar = avatar;
            }

            public void setType(String type) {
                this.type = type;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public void setAvatar_id(String avatar_id) {
                this.avatar_id = avatar_id;
            }

            public void setTimestamp(String timestamp) {
                this.timestamp = timestamp;
            }

            public void setId(String id) {
                this.id = id;
            }

            public void setLast_logged(String last_logged) {
                this.last_logged = last_logged;
            }

            public void setCover(String cover) {
                this.cover = cover;
            }

            public void setUsername(String username) {
                this.username = username;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public void setCover_position(String cover_position) {
                this.cover_position = cover_position;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public void setVerified(String verified) {
                this.verified = verified;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setActive(String active) {
                this.active = active;
            }

            public void setLanguage(String language) {
                this.language = language;
            }

            public void setCover_id(String cover_id) {
                this.cover_id = cover_id;
            }

            public void setEmail_verification_key(String email_verification_key) {
                this.email_verification_key = email_verification_key;
            }

            public String getEmail_verified() {
                return email_verified;
            }

            public String getAbout() {
                return about;
            }

            public String getAvatar() {
                return avatar;
            }

            public String getType() {
                return type;
            }

            public String getPassword() {
                return password;
            }

            public String getAvatar_id() {
                return avatar_id;
            }

            public String getTimestamp() {
                return timestamp;
            }

            public String getId() {
                return id;
            }

            public String getLast_logged() {
                return last_logged;
            }

            public String getCover() {
                return cover;
            }

            public String getUsername() {
                return username;
            }

            public String getTimezone() {
                return timezone;
            }

            public String getTime() {
                return time;
            }

            public String getCover_position() {
                return cover_position;
            }

            public String getEmail() {
                return email;
            }

            public String getVerified() {
                return verified;
            }

            public String getName() {
                return name;
            }

            public String getActive() {
                return active;
            }

            public String getLanguage() {
                return language;
            }

            public String getCover_id() {
                return cover_id;
            }

            public String getEmail_verification_key() {
                return email_verification_key;
            }
        }
    }

    public static class HashtagEntity {
        /**
         * id : 184
         * hash : 8a6fc2a4e9a7b2ffa972147acd85b37c
         * tag : ไฟแดงเสียช่วยโบกเตือน
         * last_trend_time : 1414507579
         * trend_use_num : 1
         */
        private String id;
        private String hash;
        private String tag;
        private String last_trend_time;
        private String trend_use_num;

        public void setId(String id) {
            this.id = id;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public void setLast_trend_time(String last_trend_time) {
            this.last_trend_time = last_trend_time;
        }

        public void setTrend_use_num(String trend_use_num) {
            this.trend_use_num = trend_use_num;
        }

        public String getId() {
            return id;
        }

        public String getHash() {
            return hash;
        }

        public String getTag() {
            return tag;
        }

        public String getLast_trend_time() {
            return last_trend_time;
        }

        public String getTrend_use_num() {
            return trend_use_num;
        }
    }

    public static class UserEntity {
        /**
         * email_verified : 1
         * about :
         * type : user
         * password : 1045552bccae75d1c2cfbbfb6aef539a
         * avatar_id : 0
         * online : false
         * id : 19269
         * timestamp : 2015-07-27 05:45:38
         * cover_position : 0
         * time : 1437973854
         * timezone :
         * username : tartakalot
         * cover : themes/vdomax1.1/images/default-cover.png
         * verified : 0
         * name : ดำแดง เขียวขาว
         * email_verification_key : ec78914484ee9cb08f9a7944d8e0a83a
         * live : http://150.107.31.13:1935/live/tartakalot/playlist.m3u8
         * avatar : themes/vdomax1.1/images/default-male-avatar.png
         * last_logged : 1437975938
         * live_cover : https://www.vdomax.com/clips/imgd.php?src=rtmp/tartakalot.png&width=800&height=600&crop-to-fit
         * email : tartakalotza@hotmail.com
         * active : 1
         * is_live : false
         * language :
         * cover_id : 0
         */
        private String email_verified;
        private String about;
        private String type;
        private String password;
        private String avatar_id;
        private boolean online;
        private String id;
        private String timestamp;
        private String cover_position;
        private String time;
        private String timezone;
        private String username;
        private String cover;
        private String verified;
        private String name;
        private String email_verification_key;
        private String live;
        private String avatar;
        private String last_logged;
        private String live_cover;
        private String email;
        private String active;
        private boolean is_live;
        private String language;
        private String cover_id;

        public void setEmail_verified(String email_verified) {
            this.email_verified = email_verified;
        }

        public void setAbout(String about) {
            this.about = about;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setAvatar_id(String avatar_id) {
            this.avatar_id = avatar_id;
        }

        public void setOnline(boolean online) {
            this.online = online;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public void setCover_position(String cover_position) {
            this.cover_position = cover_position;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public void setVerified(String verified) {
            this.verified = verified;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setEmail_verification_key(String email_verification_key) {
            this.email_verification_key = email_verification_key;
        }

        public void setLive(String live) {
            this.live = live;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public void setLast_logged(String last_logged) {
            this.last_logged = last_logged;
        }

        public void setLive_cover(String live_cover) {
            this.live_cover = live_cover;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public void setActive(String active) {
            this.active = active;
        }

        public void setIs_live(boolean is_live) {
            this.is_live = is_live;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public void setCover_id(String cover_id) {
            this.cover_id = cover_id;
        }

        public String getEmail_verified() {
            return email_verified;
        }

        public String getAbout() {
            return about;
        }

        public String getType() {
            return type;
        }

        public String getPassword() {
            return password;
        }

        public String getAvatar_id() {
            return avatar_id;
        }

        public boolean isOnline() {
            return online;
        }

        public String getId() {
            return id;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public String getCover_position() {
            return cover_position;
        }

        public String getTime() {
            return time;
        }

        public String getTimezone() {
            return timezone;
        }

        public String getUsername() {
            return username;
        }

        public String getCover() {
            return cover;
        }

        public String getVerified() {
            return verified;
        }

        public String getName() {
            return name;
        }

        public String getEmail_verification_key() {
            return email_verification_key;
        }

        public String getLive() {
            return live;
        }

        public String getAvatar() {
            return avatar;
        }

        public String getLast_logged() {
            return last_logged;
        }

        public String getLive_cover() {
            return live_cover;
        }

        public String getEmail() {
            return email;
        }

        public String getActive() {
            return active;
        }

        public boolean isIs_live() {
            return is_live;
        }

        public String getLanguage() {
            return language;
        }

        public String getCover_id() {
            return cover_id;
        }
    }
}

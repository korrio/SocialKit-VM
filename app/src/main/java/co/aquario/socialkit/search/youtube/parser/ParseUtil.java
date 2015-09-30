package co.aquario.socialkit.search.youtube.parser;

/**
 * Created by Mac on 9/30/15.
 */
public class ParseUtil {

    public enum VIDEO_TYPE {
        YOUTUBE,
        DIALYMOTION,
        YOUKU,
    }

    public static VideoParse parse(VIDEO_TYPE type, String hashID) {
        VideoParse parse = null;
        switch (type) {
            case YOUTUBE:
                parse = new YoutubeParser();
                break;

        }

        if (parse != null) {
            parse.parse(hashID);
        }

        return parse;
    }
}

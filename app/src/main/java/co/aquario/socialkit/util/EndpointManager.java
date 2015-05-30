package co.aquario.socialkit.util;

/**
 * Created by Mac on 3/3/15.
 */
public class EndpointManager {

    public static String prefix = "https://www.vdomax.com";

    public static String defaultCover = "https://www.vdomax.com/themes/vdomax1.1/images/default-cover.png";

    public static String defaultMaleAvatar = "https://www.vdomax.com/themes/vdomax1.1/images/default-male-avatar.png";

    public static String getDefaultFemaleAvatar = "https://www.vdomax.com/themes/vdomax1.1/images/default-female-avatar.png";

    public static String getAvatarPath(String path) {
        if (path != null) {
            if (!path.equals(""))
                return prefix + "/" + path;
            else
                return defaultMaleAvatar;
        } else {
            return defaultMaleAvatar;
        }

    }
}

package co.aquario.socialkit.util;

/**
 * Created by Mac on 3/11/15.
 */
public enum RelationType {
    FOLLOWER(1000),
    FOLLOWING(1001),
    FRIEND(1002)
    ;

    public int id;

    RelationType(int id) {
        this.id = id;
    }
}

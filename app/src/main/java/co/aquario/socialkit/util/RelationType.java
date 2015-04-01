package co.aquario.socialkit.util;

/**
 * Created by Mac on 3/11/15.
 */
public enum RelationType {
    FOLLOWER(1000),
    FOLLOWING(1001),
    FRIEND(1002)
    /*,
    BUILDINGS(1),
    FOOD(2),
    NATURE(4),
    PEOPLE(8),
    TECHNOLOGY(16),
    OBJECTS(32)
    */
    ;

    public int id;

    private RelationType(int id) {
        this.id = id;
    }
}

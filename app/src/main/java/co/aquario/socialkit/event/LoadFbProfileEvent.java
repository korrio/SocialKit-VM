package co.aquario.socialkit.event;

import co.aquario.socialkit.model.FbProfile;

public class LoadFbProfileEvent {

    public FbProfile profile;

    public String facebookToken;

    public LoadFbProfileEvent(FbProfile profile,String facebookToken) {
        this.profile = profile;
        this.facebookToken = facebookToken;
    }
}

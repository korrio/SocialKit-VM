package co.aquario.socialkit.event;

/**
 * Created by Mac on 3/3/15.
 */
public class LoadTimelineSuccessEvent {
    private TimelineDataResponse timelineData;

    public LoadTimelineSuccessEvent(TimelineDataResponse timelineData) {
        this.timelineData = timelineData;
    }

    public TimelineDataResponse getTimelineData() {
        return timelineData;
    }
}

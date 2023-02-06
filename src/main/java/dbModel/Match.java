package dbModel;

import apiModel.matchData.Data;
import apiModel.matchData.Metadata;
import apiModel.matchTimeline.Timeline;

public class Match {
    private Metadata metadata;
    private Data data;
    private Timeline timeline;


    public Match(Metadata metadata, Data data, Timeline timeline) {
        this.metadata = metadata;
        this.data = data;
        this.timeline = timeline;
    }
}

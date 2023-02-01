package dbModel;

import apiModel.matchData.MatchData;
import apiModel.matchTimeline.MatchTimeline;

public class Match {
    private MatchData matchData;
    private MatchTimeline matchTimeline;

    public Match(MatchData matchData, MatchTimeline matchTimeline) {
        this.matchData = matchData;
        this.matchTimeline = matchTimeline;
    }
}

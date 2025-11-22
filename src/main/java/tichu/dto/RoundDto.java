package tichu.dto;

import tichu.domain.Round;
import tichu.enums.Rank;

public class RoundDto {
    private Rank calledRank = null;
    private boolean isCallActive = false;

    public RoundDto(Rank calledRank, boolean isCallActive) {
        this.calledRank = calledRank;
        this.isCallActive = isCallActive;
    }

    public static RoundDto from(Round round) {
        return new RoundDto(
                round.getCalledRank(),
                round.isCallActive()
        );
    }

    public Rank getCalledRank() {
        return calledRank;
    }

    public boolean isCallActive() {
        return isCallActive;
    }
}

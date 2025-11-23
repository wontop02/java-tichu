package tichu.dto;

import tichu.domain.Round;
import tichu.enums.Rank;

public class RoundDto {
    private final Rank calledRank;
    private final boolean isCallActive;

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

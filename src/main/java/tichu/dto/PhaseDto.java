package tichu.dto;

import tichu.domain.Combination;
import tichu.domain.Phase;
import tichu.enums.CombinationType;
import tichu.enums.Rank;

public class PhaseDto {
    private final boolean isCallActive;
    private final Rank calledRank;

    private final PlayerDto currentWinner;
    private final CombinationType lastCombinationType;
    private final Rank lastCombinationTopRank;

    private PhaseDto(
            boolean isCallActive,
            Rank calledRank,
            PlayerDto currentWinner,
            CombinationType lastCombinationType,
            Rank lastCombinationTopRank
    ) {
        this.isCallActive = isCallActive;
        this.calledRank = calledRank;
        this.currentWinner = currentWinner;
        this.lastCombinationType = lastCombinationType;
        this.lastCombinationTopRank = lastCombinationTopRank;
    }

    public static PhaseDto from(Phase phase) {
        boolean isCallActive = phase.isCallActive();
        Rank calledRank = phase.getCalledRank();

        PlayerDto currentWinner = null;
        if (phase.getPhaseWinner() != null) {
            currentWinner = PlayerDto.from(phase.getPhaseWinner());
        }
        Combination lastCombination = phase.getLastCombination();
        CombinationType type = null;
        Rank topRank = null;
        
        if (lastCombination != null) {
            type = lastCombination.getCombinationType();
            topRank = phase.getTopRank(); // lastCombination이 있을 때만 의미 존재
        }

        return new PhaseDto(
                isCallActive,
                calledRank,
                currentWinner,
                type,
                topRank
        );
    }

    public boolean isCallActive() {
        return isCallActive;
    }

    public Rank getCalledRank() {
        return calledRank;
    }

    public PlayerDto getCurrentWinner() {
        return currentWinner;
    }

    public CombinationType getLastCombinationType() {
        return lastCombinationType;
    }

    public Rank getLastCombinationTopRank() {
        return lastCombinationTopRank;
    }
}

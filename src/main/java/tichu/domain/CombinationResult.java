package tichu.domain;

import tichu.enums.CombinationType;
import tichu.enums.Rank;

public class CombinationResult {
    private final CombinationType type;
    private final Card topCard;
    private final Rank topRank;

    public CombinationResult(CombinationType type, Card topCard, Rank topRank) {
        this.type = type;
        this.topCard = topCard;
        this.topRank = topRank;
    }

    public CombinationType getType() {
        return type;
    }

    public Card getTopCard() {
        return topCard;
    }

    public Rank getTopRank() {
        return topRank;
    }
}

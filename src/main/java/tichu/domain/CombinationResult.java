package tichu.domain;

public class CombinationResult {
    private final CombinationType type;
    private final Card topCard;

    public CombinationResult(CombinationType type, Card topCard) {
        this.type = type;
        this.topCard = topCard;
    }

    public CombinationType getType() {
        return type;
    }

    public Card getTopCard() {
        return topCard;
    }
}

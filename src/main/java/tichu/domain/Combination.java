package tichu.domain;

import static tichu.enums.CombinationType.BOMB_FOUR_CARD;
import static tichu.enums.CombinationType.BOMB_STRAIGHT_FLUSH;
import static tichu.enums.CombinationType.SINGLE;

import java.util.List;
import tichu.enums.CombinationType;
import tichu.enums.Rank;
import tichu.enums.Special;

// 낸 패 조합
public class Combination {
    private static final String INCOMPARABLE = "서로 다른 조합은 비교할 수 없습니다.";

    private final List<Card> cards;
    private final CombinationResult combinationResult;

    public Combination(List<Card> cards) {
        this.cards = List.copyOf(cards);
        this.combinationResult = CombinationEvaluator.evaluate(cards);
    }

    // 조합 간 비교
    public int compareTo(Combination other) {
        CombinationType thisType = combinationResult.getType();
        CombinationType otherType = other.combinationResult.getType();
        if (this.isBomb() && !other.isBomb()) {
            return 1;
        }
        if (!this.isBomb() && other.isBomb()) {
            return -1;
        }
        if (this.isBomb() && other.isBomb()) {
            return compareToBetweenBombs(other);
        }
        if (thisType != otherType) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        return compareSameType(other);
    }

    private int compareToBetweenBombs(Combination other) {
        CombinationType thisType = combinationResult.getType();
        CombinationType otherType = other.combinationResult.getType();
        if (thisType == BOMB_STRAIGHT_FLUSH && otherType == BOMB_FOUR_CARD) {
            return 1;
        }
        if (thisType == BOMB_FOUR_CARD && otherType == BOMB_STRAIGHT_FLUSH) {
            return -1;
        }
        return compareSameType(other);
    }

    public CombinationType getCombinationType() {
        return combinationResult.getType();
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }

    public Card getTopCard() {
        return combinationResult.getTopCard();
    }

    public boolean isBomb() {
        return (combinationResult.getType() == BOMB_FOUR_CARD)
                || (combinationResult.getType() == BOMB_STRAIGHT_FLUSH);
    }

    public boolean hasMahjong() {
        return cards.stream().anyMatch(Card::isMahjong);
    }

    public boolean hasCallRank(Rank rank) {
        return cards.stream().anyMatch(card -> card.getRank() == rank);
    }

    private int compareSameType(Combination other) {
        if (this.combinationResult.getType() == SINGLE) {
            return compareSingle(other);
        }
        if (isBomb()) {
            return compareBomb(other);
        }
        return compareByTopCard(other);
    }

    private int compareSingle(Combination other) {
        Card myTopCard = this.combinationResult.getTopCard();
        Card otherTopCard = other.combinationResult.getTopCard();
        if (myTopCard.getSpecial() == Special.DRAGON) {
            return 1;
        }
        if (otherTopCard.getSpecial() == Special.DRAGON) {
            return -1;
        }
        if (myTopCard.getSpecial() == Special.PHOENIX) {
            return 1;
        }
        if (otherTopCard.getSpecial() == Special.PHOENIX) {
            return -1;
        }
        return compareByTopCard(other);
    }

    private int compareBomb(Combination other) {
        if (this.cards.size() > other.cards.size()) {
            return 1;
        }
        if (this.cards.size() < other.cards.size()) {
            return -1;
        }
        return compareByTopCard(other);
    }

    private int compareByTopCard(Combination other) {
        Card myTopCard = this.combinationResult.getTopCard();
        Card otherTopCard = other.combinationResult.getTopCard();
        return Integer.compare(myTopCard.getRankPriority(), otherTopCard.getRankPriority());
    }
}

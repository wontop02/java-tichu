package tichu.domain;

import static tichu.domain.CombinationType.BOMB;
import static tichu.domain.CombinationType.SINGLE;

import java.util.List;
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
        if (this.combinationResult.getType() == BOMB
                && other.combinationResult.getType() != BOMB) {
            return 1;
        }
        if (this.combinationResult.getType() != BOMB
                && other.combinationResult.getType() == BOMB) {
            return -1;
        }

        if (this.combinationResult.getType() != other.combinationResult.getType()) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        return compareSameType(other);
    }

    public CombinationType getCombinationType() {
        return combinationResult.getType();
    }

    private int compareSameType(Combination other) {
        if (this.combinationResult.getType() == SINGLE) {
            return compareSingle(other);
        }
        if (this.combinationResult.getType() == BOMB) {
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

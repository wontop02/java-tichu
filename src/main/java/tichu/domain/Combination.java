package tichu.domain;

import static tichu.enums.CombinationType.BOMB_FOUR_CARD;
import static tichu.enums.CombinationType.BOMB_STRAIGHT_FLUSH;
import static tichu.enums.CombinationType.SINGLE;

import java.util.List;
import tichu.enums.CombinationType;
import tichu.enums.Rank;

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

        boolean myBomb = this.isBomb();
        boolean otherBomb = other.isBomb();

        if (myBomb && !otherBomb) {
            return 1;
        }
        if (!myBomb && otherBomb) {
            return -1;
        }
        if (myBomb && otherBomb) {
            return compareBomb(other);
        }
        if (thisType != otherType) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        // 같은 타입이어도 장수 다르면 비교 불가
        if (this.cards.size() != other.cards.size()) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        Rank myRank = this.getTopRank();
        Rank otherRank = other.getTopRank();

        return Integer.compare(myRank.getPriority(), otherRank.getPriority());
    }

    private int compareBomb(Combination other) {
        CombinationType myType = this.combinationResult.getType();
        CombinationType otherType = other.combinationResult.getType();
        // 폭탄 종류 비교
        if (myType == BOMB_STRAIGHT_FLUSH && otherType == BOMB_FOUR_CARD) {
            return 1;
        }
        if (myType == BOMB_FOUR_CARD && otherType == BOMB_STRAIGHT_FLUSH) {
            return -1;
        }
        // 길이 비교
        if (myType == BOMB_STRAIGHT_FLUSH && otherType == BOMB_STRAIGHT_FLUSH) {
            if (this.cards.size() > other.cards.size()) {
                return 1;
            }
            if (this.cards.size() < other.cards.size()) {
                return -1;
            }

            // 길이 같으면 topRank 비교
            return Integer.compare(
                    this.getTopRank().getPriority(),
                    other.getTopRank().getPriority()
            );
        }
        return Integer.compare(
                this.getTopRank().getPriority(),
                other.getTopRank().getPriority()
        );
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

    public Rank getTopRank() {
        return combinationResult.getTopRank();
    }

    public boolean isBomb() {
        return (combinationResult.getType() == BOMB_FOUR_CARD)
                || (combinationResult.getType() == BOMB_STRAIGHT_FLUSH);
    }

    public boolean isDog() {
        return cards.getFirst().isDog();
    }

    public boolean isSinglePhoenix() {
        if (combinationResult.getType() == SINGLE) {
            return cards.getFirst().isPhoenix();
        }
        return false;
    }

    public boolean isDragon() {
        return cards.getFirst().isDragon();
    }

    public boolean isMahjong() {
        return cards.getFirst().isMahjong();
    }

    public boolean hasMahjong() {
        return cards.stream().anyMatch(Card::isMahjong);
    }

    public boolean hasCallRank(Rank rank) {
        return cards.stream().anyMatch(card -> card.getRank() == rank);
    }
}

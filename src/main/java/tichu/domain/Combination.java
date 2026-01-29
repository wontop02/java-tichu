package tichu.domain;

import java.util.List;
import tichu.enums.CombinationType;
import tichu.enums.Rank;

// 낸 패 조합
public class Combination {
    private static final String INCOMPARABLE = "서로 다른 조합은 비교할 수 없습니다.";

    private final List<Card> cards;
    private final CombinationType combinationType;
    private final Card topCard;

    public Combination(List<Card> cards, CombinationType combinationType, Card topCard) {
        this.cards = List.copyOf(cards);
        this.combinationType = combinationType;
        this.topCard = topCard;
    }

    // 조합 간 비교
    public int compareTo(Combination other) {
        if (this.isBomb() && !other.isBomb()) {
            return 1;
        }
        if (!this.isBomb() && other.isBomb()) {
            return -1;
        }
        if (this.isBomb()) {
            return compareBomb(other);
        }
        // 같은 타입이어도 장수 다르면 비교 불가
        if (!isComparable(other)) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        return compareTopRank(other);
    }

    private int compareBomb(Combination other) {
        // 폭탄 종류 비교
        if (combinationType == CombinationType.BOMB_STRAIGHT_FLUSH
                && other.combinationType == CombinationType.BOMB_FOUR_CARD) {
            return 1;
        }
        if (combinationType == CombinationType.BOMB_FOUR_CARD
                && other.combinationType == CombinationType.BOMB_STRAIGHT_FLUSH) {
            return -1;
        }
        // 길이 비교
        if (this.cards.size() > other.cards.size()) {
            return 1;
        }
        if (this.cards.size() < other.cards.size()) {
            return -1;
        }
        return compareTopRank(other);
    }

    private boolean isComparable(Combination other) {
        return (combinationType == other.combinationType)
                && this.cards.size() == other.cards.size();
    }

    private int compareTopRank(Combination other) {
        Rank myTopRank = topCard.getRank();
        Rank otherTopRank = other.topCard.getRank();
        return Integer.compare(myTopRank.getPriority(), otherTopRank.getPriority());
    }

    public CombinationType getCombinationType() {
        return combinationType;
    }

    public int size() {
        return cards.size();
    }

    public List<Card> cards() {
        return List.copyOf(cards);
    }

    public Card getTopCard() {
        return topCard;
    }

    public Rank getTopRank() {
        return topCard.getRank();
    }

    public boolean isBomb() {
        return (combinationType == CombinationType.BOMB_FOUR_CARD)
                || (combinationType == CombinationType.BOMB_STRAIGHT_FLUSH);
    }

    public boolean isDog() {
        return cards.getFirst().isDog();
    }

    public boolean isSinglePhoenix() {
        if (combinationType == CombinationType.SINGLE) {
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

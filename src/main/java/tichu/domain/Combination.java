package tichu.domain;

import static tichu.domain.CombinationType.BOMB;
import static tichu.domain.CombinationType.FULL_HOUSE;
import static tichu.domain.CombinationType.PAIR;
import static tichu.domain.CombinationType.PAIR_SEQUENCE;
import static tichu.domain.CombinationType.SINGLE;
import static tichu.domain.CombinationType.STRAIGHT;
import static tichu.domain.CombinationType.TRIPLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import tichu.enums.Rank;
import tichu.enums.Special;
import tichu.enums.Suit;

// 낸 패 조합
public class Combination {
    private static final String NOT_COMBINATION = "유효하지 않은 조합입니다.";
    private static final String INCOMPARABLE = "서로 다른 조합은 비교할 수 없습니다.";

    private final List<Card> cards;
    private final CombinationResult combinationResult;

    private static class CombinationResult {
        final CombinationType type;
        final Card topCard;

        CombinationResult(CombinationType type, Card topCard) {
            this.type = type;
            this.topCard = topCard;
        }
    }

    public Combination(List<Card> cards) {
        this.cards = cards;
        Collections.sort(cards);
        combinationResult = evaluateCombination(cards);
    }

    // 조합 간 비교
    public int compareTo(Combination other) {
        if (this.combinationResult.type == BOMB
                && other.combinationResult.type != BOMB) {
            return 1;
        }
        if (this.combinationResult.type != BOMB
                && other.combinationResult.type == BOMB) {
            return -1;
        }

        if (this.combinationResult.type != other.combinationResult.type) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        return compareSameType(other);
    }

    private int compareSameType(Combination other) {
        if (this.combinationResult.type == SINGLE) {
            return compareSingle(other);
        }
        if (this.combinationResult.type == BOMB) {
            return compareBomb(other);
        }
        return compareByTopCard(other);
    }

    private int compareSingle(Combination other) {
        Card myTopCard = this.combinationResult.topCard;
        Card otherTopCard = other.combinationResult.topCard;
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
        Card myTopCard = this.combinationResult.topCard;
        Card otherTopCard = other.combinationResult.topCard;
        return Integer.compare(myTopCard.getRankPriority(), otherTopCard.getRankPriority());
    }

    private CombinationResult evaluateCombination(List<Card> cards) {
        if (isSingle(cards)) {
            return new CombinationResult(SINGLE, cards.getLast());
        }

        boolean hasPhoenix = containsPhoenix(cards);

        if (hasPhoenix) {
            CombinationResult withPhoenixResult = evaluateWithPhoenix(cards);
            if (withPhoenixResult != null) {
                return withPhoenixResult;
            }
        }

        CombinationResult result = evaluateNormalCombination(cards);
        if (result == null) {
            throw new IllegalArgumentException(NOT_COMBINATION);
        }
        return result;
    }

    // 봉이 포함되어 있을 경우 조합 판단
    private CombinationResult evaluateWithPhoenix(List<Card> cards) {
        List<Rank> ranks = new ArrayList<>(List.of(Rank.values()));
        Collections.reverse(ranks);
        for (Rank substituteRank : ranks) {
            List<Card> substituteCards = substitutePhoenix(cards, substituteRank);
            CombinationResult result = evaluateNormalCombination(substituteCards);
            if (result == null || result.type == BOMB) {
                continue;
            }
            return result;
        }
        return null;
    }

    private boolean containsPhoenix(List<Card> cards) {
        return cards.stream().anyMatch(Card::isPhoenix);
    }

    private List<Card> substitutePhoenix(List<Card> cards, Rank substituteRank) {
        List<Card> substituteCards = new ArrayList<>();
        for (Card card : cards) {
            if (card.isPhoenix()) {
                // 봉으로 대체한 것임을 표시하기 위해 suit는 null
                substituteCards.add(new Card(substituteRank, Suit.NONE));
                continue;
            }
            substituteCards.add(card);
        }
        Collections.sort(substituteCards);

        return substituteCards;
    }

    private CombinationResult evaluateNormalCombination(List<Card> cards) {
        if (isPair(cards)) {
            return new CombinationResult(PAIR, cards.getLast());
        }
        if (isTriple(cards)) {
            return new CombinationResult(TRIPLE, cards.getLast());
        }
        if (isStraight(cards)) {
            return new CombinationResult(STRAIGHT, cards.getLast());
        }
        if (isFullHouse(cards)) {
            return new CombinationResult(FULL_HOUSE, findTripleTopCard(cards));
        }
        if (isPairSequence(cards)) {
            return new CombinationResult(PAIR_SEQUENCE, cards.getLast());
        }
        if (isBomb(cards)) {
            return new CombinationResult(BOMB, cards.getLast());
        }
        return null;
    }

    private boolean isSingle(List<Card> cards) {
        return cards.size() == 1;
    }

    private boolean isPair(List<Card> cards) {
        if (cards.size() != 2) {
            return false;
        }
        for (Card card : cards) {
            if (card.isDog() || card.isDragon() || card.isMahjong()) {
                return false;
            }
        }
        return cards.get(0).getRank() == cards.get(1).getRank();
    }

    private boolean isTriple(List<Card> cards) {
        if (cards.size() != 3) {
            return false;
        }
        for (Card card : cards) {
            if (card.isDog() || card.isDragon() || card.isMahjong()) {
                return false;
            }
        }
        return cards.get(0).getRank() == cards.get(2).getRank();
    }

    private boolean isStraight(List<Card> cards) {
        if (cards.size() < 5) {
            return false;
        }
        for (Card card : cards) {
            if (card.isDog() || card.isDragon()) {
                return false;
            }
        }
        return IntStream.range(0, cards.size() - 1)
                .allMatch(i ->
                        cards.get(i + 1).getRankPriority() - cards.get(i).getRankPriority() == 1);
    }

    private boolean isFullHouse(List<Card> cards) {
        if (cards.size() != 5) {
            return false;
        }
        for (Card card : cards) {
            if (card.isDog() || card.isDragon() || card.isMahjong()) {
                return false;
            }
        }
        boolean threeFirst = (cards.get(0).getRank() == cards.get(2).getRank())
                && (cards.get(3).getRank() == cards.get(4).getRank());

        boolean threeLast = (cards.get(0).getRank() == cards.get(1).getRank())
                && (cards.get(2).getRank() == cards.get(4).getRank());

        return threeFirst || threeLast;
    }


    private Card findTripleTopCard(List<Card> cards) {
        if (cards.get(0).getRank() == cards.get(2).getRank()) {
            return cards.getFirst();
        }
        return cards.getLast();
    }

    private boolean isPairSequence(List<Card> cards) {
        if (cards.size() % 2 != 0) {
            return false;
        }
        for (Card card : cards) {
            if (card.isDog() || card.isDragon() || card.isMahjong()) {
                return false;
            }
        }
        boolean allPairs = IntStream.iterate(0, i -> i < cards.size(), i -> i + 2)
                .allMatch(i ->
                        cards.get(i).getRank() == cards.get(i + 1).getRank());
        if (!allPairs) {
            return false;
        }
        return IntStream.iterate(0, i -> i < cards.size() - 1, i -> i + 2)
                .allMatch(i ->
                        cards.get(i + 2).getRankPriority()
                                - cards.get(i).getRankPriority() == 1);
    }

    private boolean isBomb(List<Card> cards) {
        if (cards.size() == 4) {
            return cards.get(0).getRank() == cards.get(3).getRank();
        }
        for (Card card : cards) {
            if (card.isSpecial()) {
                return false;
            }
        }
        if (cards.size() >= 5) {
            boolean isStraight = isStraight(cards);
            boolean isSameSuit = IntStream.range(0, cards.size() - 1)
                    .allMatch(i -> cards.get(i).getSuit() == cards.get(i + 1).getSuit());
            return isStraight && isSameSuit;
        }
        return false;
    }
}

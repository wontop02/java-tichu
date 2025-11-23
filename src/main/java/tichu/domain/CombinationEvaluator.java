package tichu.domain;

import static tichu.enums.CombinationType.BOMB_FOUR_CARD;
import static tichu.enums.CombinationType.BOMB_STRAIGHT_FLUSH;
import static tichu.enums.CombinationType.FULL_HOUSE;
import static tichu.enums.CombinationType.PAIR;
import static tichu.enums.CombinationType.PAIR_SEQUENCE;
import static tichu.enums.CombinationType.SINGLE;
import static tichu.enums.CombinationType.STRAIGHT;
import static tichu.enums.CombinationType.TRIPLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import tichu.enums.Rank;
import tichu.enums.Suit;

public class CombinationEvaluator {
    private static final String NOT_COMBINATION = "유효하지 않은 조합입니다.";

    public static CombinationResult evaluate(List<Card> cards) {
        List<Card> sorted = new ArrayList<>(cards);
        Collections.sort(sorted);

        // 싱글 Rank는 어차피 phase에서 다시 비교하기 때문에 무시됨
        if (isSingle(sorted)) {
            return new CombinationResult(SINGLE, sorted.getLast(), sorted.getLast().getRank());
        }

        boolean hasPhoenix = containsPhoenix(sorted);

        if (hasPhoenix) {
            CombinationResult withPhoenixResult = evaluateWithPhoenix(sorted);
            if (withPhoenixResult != null) {
                return withPhoenixResult;
            }
        }

        CombinationResult result = evaluateNormalCombination(sorted);
        if (result == null) {
            throw new IllegalArgumentException(NOT_COMBINATION);
        }
        return result;
    }

    private static boolean containsPhoenix(List<Card> cards) {
        return cards.stream().anyMatch(Card::isPhoenix);
    }

    // 봉이 포함되어 있을 경우 조합 판단
    private static CombinationResult evaluateWithPhoenix(List<Card> cards) {
        List<Rank> ranks = new ArrayList<>(List.of(Rank.values()));
        Collections.reverse(ranks);
        for (Rank substituteRank : ranks) {
            List<Card> substituteCards = substitutePhoenix(cards, substituteRank);
            CombinationResult result = evaluateNormalCombination(substituteCards);
            if (result == null || result.getType() == BOMB_FOUR_CARD || result.getType() == BOMB_STRAIGHT_FLUSH) {
                continue;
            }
            // Rank에만 대체된 Rank 넣어줌
            Rank topRank = substituteCards.getLast().getRank();
            Card topCard = substituteCards.getLast();
            return new CombinationResult(result.getType(), topCard, topRank);
        }
        return null;
    }

    private static List<Card> substitutePhoenix(List<Card> cards, Rank substituteRank) {
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

    private static CombinationResult evaluateNormalCombination(List<Card> cards) {
        if (isPair(cards)) {
            return new CombinationResult(PAIR, cards.getLast(), cards.getLast().getRank());
        }
        if (isTriple(cards)) {
            return new CombinationResult(TRIPLE, cards.getLast(), cards.getLast().getRank());
        }
        if (isFullHouse(cards)) {
            return new CombinationResult(FULL_HOUSE, findTripleTopCard(cards), findTripleTopCard(cards).getRank());
        }
        if (isStraight(cards)) {
            return new CombinationResult(STRAIGHT, cards.getLast(), cards.getLast().getRank());
        }
        if (isPairSequence(cards)) {
            return new CombinationResult(PAIR_SEQUENCE, cards.getLast(), cards.getLast().getRank());
        }
        if (isBombFourCord(cards)) {
            return new CombinationResult(BOMB_FOUR_CARD, cards.getLast(), cards.getLast().getRank());
        }
        if (isBombStraightFlush(cards)) {
            return new CombinationResult(BOMB_STRAIGHT_FLUSH, cards.getLast(), cards.getLast().getRank());
        }
        return null;
    }

    private static boolean isSingle(List<Card> cards) {
        return cards.size() == 1;
    }

    private static boolean isPair(List<Card> cards) {
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

    private static boolean isTriple(List<Card> cards) {
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

    private static boolean isFullHouse(List<Card> cards) {
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

    private static Card findTripleTopCard(List<Card> cards) {
        if (cards.get(0).getRank() == cards.get(2).getRank()) {
            return cards.getFirst();
        }
        return cards.getLast();
    }

    private static boolean isStraight(List<Card> cards) {
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

    private static boolean isPairSequence(List<Card> cards) {
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
        return IntStream.iterate(0, i -> i < cards.size() - 2, i -> i + 2)
                .allMatch(i ->
                        cards.get(i + 2).getRankPriority()
                                - cards.get(i).getRankPriority() == 1);
    }

    private static boolean isBombFourCord(List<Card> cards) {
        if (cards.size() == 4) {
            return cards.get(0).getRank() == cards.get(3).getRank();
        }
        return false;
    }

    private static boolean isBombStraightFlush(List<Card> cards) {
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

package tichu.domain;

import static tichu.domain.CombinationEvaluator.containsPhoenix;
import static tichu.enums.CombinationType.BOMB_FOUR_CARD;
import static tichu.enums.CombinationType.BOMB_STRAIGHT_FLUSH;
import static tichu.enums.CombinationType.FULL_HOUSE;
import static tichu.enums.CombinationType.PAIR;
import static tichu.enums.CombinationType.PAIR_SEQUENCE;
import static tichu.enums.CombinationType.SINGLE;
import static tichu.enums.CombinationType.STRAIGHT;
import static tichu.enums.CombinationType.TRIPLE;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import tichu.enums.CombinationType;
import tichu.enums.Rank;
import tichu.enums.Suit;

public class CombinationFinder {
    public static boolean hasStrongCombinationWithCall(List<Card> cards, Rank calledRank, Combination combination) {
        boolean includesCalledRank = cards.stream().anyMatch(card -> card.getRank() == calledRank);
        if (!includesCalledRank) {
            return false;
        }
        if (combination == null) {
            return true;
        }
        if (hasStrongBomb(cards, calledRank, combination)) {
            return true;
        }
        return hasStrongNormalCombination(cards, calledRank, combination);
    }

    private static boolean hasStrongBomb(List<Card> cards, Rank calledRank, Combination combination) {
        return hasStrongBombFourCard(cards, calledRank, combination)
                || hasStrongBombStraightFlush(cards, calledRank, combination);
    }

    private static boolean hasStrongNormalCombination(List<Card> cards, Rank calledRank, Combination combination) {
        CombinationType type = combination.getCombinationType();
        if (type == SINGLE) {
            return hasStrongSingle(cards, calledRank, combination);
        }
        if (type == PAIR) {
            return hasStrongPair(cards, calledRank, combination);
        }
        if (type == TRIPLE) {
            return hasStrongTriple(cards, calledRank, combination);
        }
        if (type == FULL_HOUSE) {
            return hasStrongFullHouse(cards, calledRank, combination);
        }
        if (type == STRAIGHT) {
            return hasStrongStraight(cards, calledRank, combination);
        }
        if (type == PAIR_SEQUENCE) {
            return hasStrongPairSequence(cards, calledRank, combination);
        }
        return false;
    }

    private static boolean hasStrongSingle(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = Rank.ONE;
        if (!combination.isMahjong()) {
            topRank = combination.getTopRank();
        }
        if (topRank.getPriority() >= calledRank.getPriority()) {
            return false;
        }
        return cards.stream()
                .anyMatch(card -> card.getRank() == calledRank);
    }

    private static boolean hasStrongPair(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        if (combinationTopCard.getRankPriority() >= calledRank.getPriority()) {
            return false;
        }
        if (containsPhoenix(cards)) {
            return cards.stream()
                    .anyMatch(card -> card.getRank() == calledRank);
        }
        return cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count() >= 2;
    }

    private static boolean hasStrongTriple(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        if (combinationTopCard.getRankPriority() >= calledRank.getPriority()) {
            return false;
        }
        if (containsPhoenix(cards)) {
            return cards.stream()
                    .filter(card -> card.getRank() == calledRank)
                    .count() >= 2;
        }
        return cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count() >= 3;
    }

    private static boolean hasStrongFullHouse(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        boolean hasPhoenix = containsPhoenix(cards);
        long callCount = cards.stream()
                .filter(c -> c.getRank() == calledRank)
                .count();

        Map<Rank, Long> otherRanks = cards.stream()
                .filter(c -> (c.getRank() != calledRank) && !c.isSpecial())
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));

        if (hasStrongFullHouseWithCallTriple(callCount, hasPhoenix, otherRanks, calledRank, topRank)) {
            return true;
        }
        return hasStrongFullHouseWithCallPair(callCount, hasPhoenix, otherRanks, topRank);
    }

    private static boolean hasStrongFullHouseWithCallTriple(long callCount, boolean hasPhoenix,
                                                            Map<Rank, Long> otherRanks, Rank calledRank, Rank topRank) {
        boolean callTriple = (callCount >= 3) || (callCount == 2 && hasPhoenix);
        boolean usedPhoenixForTriple = (callCount == 2 && hasPhoenix);
        if (callTriple) {
            for (Map.Entry<Rank, Long> entry : otherRanks.entrySet()) {
                long count = entry.getValue();

                boolean otherPair = (count >= 2) || (count == 1 && hasPhoenix && !usedPhoenixForTriple);
                if (otherPair && (calledRank.getPriority() > topRank.getPriority())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasStrongFullHouseWithCallPair(long callCount, boolean hasPhoenix,
                                                          Map<Rank, Long> otherRanks, Rank topRank) {
        boolean callPair = (callCount >= 2) || (callCount >= 1 && hasPhoenix);
        boolean usedPhoenixForPair = (callCount == 1 && hasPhoenix);
        if (callPair) {
            for (Map.Entry<Rank, Long> entry : otherRanks.entrySet()) {
                long count = entry.getValue();
                Rank TripleRank = entry.getKey();

                boolean otherTriple = (count >= 3) || (count == 2 && hasPhoenix && !usedPhoenixForPair);
                if (otherTriple && (TripleRank.getPriority() > topRank.getPriority())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasStrongStraight(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        int straightLength = combination.getCards().size();

        List<Rank> distinctRanks = new ArrayList<>(cards.stream()
                .filter(card -> !card.isDog() && !card.isDragon() && !card.isPhoenix())
                .map(Card::getRank)
                .distinct()
                .sorted(Comparator.comparing(Rank::getPriority))
                .toList());

        if (distinctRanks.size() < straightLength) {
            return false;
        }
        return isStraight(distinctRanks, straightLength, calledRank, topRank);
    }

    private static boolean hasStrongPairSequence(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        int pairSequenceLength = combination.getCards().size();
        int pairSequenceRankCount = pairSequenceLength / 2;

        Map<Rank, Long> rankCounts = cards.stream()
                .filter(card -> !card.isSpecial()) // 특수 카드 제외
                .map(Card::getRank)
                .collect(Collectors.groupingBy(rank -> rank, Collectors.counting()));

        List<Rank> pairRanks = rankCounts.entrySet().stream()
                .filter(entry -> entry.getValue() >= 2)
                .map(Map.Entry::getKey)
                .sorted(Comparator.comparing(Rank::getPriority))
                .toList();

        if (pairRanks.size() < pairSequenceRankCount) {
            return false;
        }
        return isStraight(pairRanks, pairSequenceRankCount, calledRank, topRank);
    }

    private static boolean isStraight(List<Rank> ranks, int straightLength, Rank calledRank, Rank topRank) {
        for (int i = 0; i <= ranks.size() - straightLength; i++) {
            List<Rank> partRanks = ranks.subList(i, i + straightLength);
            Rank firstRank = partRanks.getFirst();
            Rank lastRank = partRanks.getLast();

            boolean isStraight = (lastRank.getPriority() - firstRank.getPriority() == straightLength - 1);
            boolean includesCalledRank = partRanks.contains(calledRank);
            boolean isStronger = (lastRank.getPriority() > topRank.getPriority());

            if (isStraight && includesCalledRank && isStronger) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStrongBombFourCard(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        CombinationType combinationType = combination.getCombinationType();

        long calledRankCount = cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count();

        if (calledRankCount >= 4) {
            if (combinationType != BOMB_STRAIGHT_FLUSH) {
                if (!combination.isBomb()
                        || calledRank.getPriority() > topRank.getPriority()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasStrongBombStraightFlush(List<Card> cards, Rank calledRank, Combination combination) {
        CombinationType combinationType = combination.getCombinationType();

        List<Card> normalCards = cards.stream()
                .filter(card -> !card.isSpecial())
                .sorted(Comparator.comparing(Card::getRankPriority))
                .toList();

        Map<Suit, List<Card>> suitedCards = normalCards.stream()
                .collect(Collectors.groupingBy(Card::getSuit));

        int requireBombSize = 5;
        if (combinationType == BOMB_STRAIGHT_FLUSH) {
            requireBombSize = combination.getCards().size();
        }

        return findStraightParts(suitedCards, requireBombSize, calledRank, combination);
    }

    private static boolean findStraightParts(Map<Suit, List<Card>> suitedCards, int requireBombSize, Rank calledRank,
                                             Combination combination) {
        for (List<Card> suitCards : suitedCards.values()) {
            if (suitCards.size() < requireBombSize) {
                continue;
            }
            List<List<Card>> straightParts = extractStraightParts(suitCards);
            for (List<Card> partCards : straightParts) {
                if (partCards.size() < requireBombSize) {
                    continue;
                }
                boolean includesCalledRank = partCards.stream()
                        .anyMatch(card -> card.getRank() == calledRank);
                if (!includesCalledRank) {
                    continue;
                }
                if (isStrongBombStraightFlush(partCards, requireBombSize, combination)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static List<List<Card>> extractStraightParts(List<Card> cards) {
        List<List<Card>> straightParts = new ArrayList<>();
        int start = 0;

        for (int i = 1; i <= cards.size(); i++) {
            boolean endStraight = (i == cards.size())
                    || cards.get(i).getRankPriority() - cards.get(i - 1).getRankPriority() != 1;
            if (endStraight) {
                straightParts.add(cards.subList(start, i));
                start = i;
            }
        }
        return straightParts;
    }

    private static boolean isStrongBombStraightFlush(List<Card> cards, int requireBombSize, Combination combination) {
        Rank topRank = combination.getTopRank();
        CombinationType combinationType = combination.getCombinationType();
        if (!combination.isBomb() || combinationType == BOMB_FOUR_CARD) {
            return true;
        }
        if (cards.size() > requireBombSize) {
            return true;
        }
        if (cards.size() == requireBombSize &&
                cards.getLast().getRankPriority() > topRank.getPriority()) {
            return true;
        }
        return false;
    }
}

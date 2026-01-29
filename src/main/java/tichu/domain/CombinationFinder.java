package tichu.domain;

import static tichu.domain.CombinationEvaluator.containsPhoenix;
import static tichu.domain.CombinationEvaluator.substitutePhoenix;

import java.util.ArrayList;
import java.util.Collections;
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
        if (containsPhoenix(cards)) {
            return findStrongCombinationWithPhoenix(cards, calledRank, combination);
        }
        return hasStrongNormalCombination(cards, calledRank, combination);
    }

    private static boolean findStrongCombinationWithPhoenix(List<Card> cards, Rank calledRank,
                                                            Combination combination) {
        List<Rank> ranks = new ArrayList<>(List.of(Rank.values()));
        Collections.reverse(ranks);
        for (Rank substituteRank : ranks) {
            if (substituteRank.isSpecial()) {
                continue;
            }
            List<Card> substituteCards = substitutePhoenix(cards, substituteRank);
            if (hasStrongNormalCombination(substituteCards, calledRank, combination)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStrongBomb(List<Card> cards, Rank calledRank, Combination combination) {
        return hasStrongBombFourCard(cards, calledRank, combination)
                || hasStrongBombStraightFlush(cards, calledRank, combination);
    }

    private static boolean hasStrongBombFourCard(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        CombinationType combinationType = combination.getCombinationType();

        long calledRankCount = cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count();

        if (calledRankCount >= 4) {
            if (combinationType != CombinationType.BOMB_STRAIGHT_FLUSH) {
                return !combination.isBomb()
                        || calledRank.getPriority() > topRank.getPriority();
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
        if (combinationType == CombinationType.BOMB_STRAIGHT_FLUSH) {
            requireBombSize = combination.size();
        }
        return findStraightParts(suitedCards, requireBombSize, calledRank, combination);
    }

    private static boolean findStraightParts(Map<Suit, List<Card>> suitedCards, int requireBombSize, Rank calledRank,
                                             Combination combination) {
        for (List<Card> cards : suitedCards.values()) {
            if (cards.size() < requireBombSize) {
                continue;
            }
            List<List<Card>> straightParts = extractStraightParts(cards);
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
        if (!combination.isBomb() || combinationType == CombinationType.BOMB_FOUR_CARD) {
            return true;
        }
        if (cards.size() > requireBombSize) {
            return true;
        }
        return cards.size() == requireBombSize &&
                cards.getLast().getRankPriority() > topRank.getPriority();
    }

    private static boolean hasStrongNormalCombination(List<Card> cards, Rank calledRank, Combination combination) {
        CombinationType type = combination.getCombinationType();
        if (type == CombinationType.SINGLE) {
            return hasStrongSingle(cards, calledRank, combination);
        }
        if (type == CombinationType.PAIR) {
            return hasStrongPair(cards, calledRank, combination);
        }
        if (type == CombinationType.TRIPLE) {
            return hasStrongTriple(cards, calledRank, combination);
        }
        if (type == CombinationType.FULL_HOUSE) {
            return hasStrongFullHouse(cards, calledRank, combination);
        }
        if (type == CombinationType.STRAIGHT) {
            return hasStrongStraight(cards, calledRank, combination);
        }
        if (type == CombinationType.PAIR_SEQUENCE) {
            return hasStrongPairSequence(cards, calledRank, combination);
        }
        return false;
    }

    private static boolean hasStrongSingle(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = Rank.MAHJONG;
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
        return cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count() >= 2;
    }

    private static boolean hasStrongTriple(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        if (combinationTopCard.getRankPriority() >= calledRank.getPriority()) {
            return false;
        }
        return cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count() >= 3;
    }

    private static boolean hasStrongFullHouse(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        if (topRank.getPriority() >= calledRank.getPriority()) {
            return false;
        }

        long callCount = cards.stream()
                .filter(c -> c.getRank() == calledRank)
                .count();

        return findStrongFullHouse(callCount, cards, calledRank);
    }

    private static boolean findStrongFullHouse(long callCount, List<Card> cards, Rank calledRank) {
        boolean callPair = callCount >= 2;
        boolean callTriple = callCount >= 3;

        Map<Rank, Long> otherRanks = cards.stream()
                .filter(c -> c.getRank() != calledRank)
                .collect(Collectors.groupingBy(Card::getRank, Collectors.counting()));

        boolean otherPair = otherRanks.values().stream()
                .anyMatch(count -> count >= 2);
        boolean otherTriple = otherRanks.values().stream()
                .anyMatch(count -> count >= 3);

        return (callPair && otherTriple) || (callTriple && otherPair);
    }

    private static boolean hasStrongStraight(List<Card> cards, Rank calledRank, Combination combination) {
        Rank topRank = combination.getTopRank();
        int straightLength = combination.size();

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
        int pairSequenceLength = combination.size();
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
}

package tichu.domain;

import static tichu.domain.CombinationEvaluator.containsPhoenix;
import static tichu.domain.CombinationEvaluator.substitutePhoenix;
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
        boolean hasStrongCombination;

        if (containsPhoenix(cards)) {
            List<Rank> ranks = new ArrayList<>(List.of(Rank.values()));
            Collections.reverse(ranks);
            for (Rank substituteRank : ranks) {
                List<Card> substituteCards = substitutePhoenix(cards, substituteRank);
                boolean hasCombination = hasStrongNormalCombination(substituteCards, calledRank, combination);
                if (hasCombination) {
                    return true;
                }
            }
        }
        hasStrongCombination = hasStrongNormalCombination(cards, calledRank, combination);
        if (hasStrongCombination) {
            return true;
        }
        return hasStrongBombCombination(cards, calledRank, combination);
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

    private static boolean hasStrongBombCombination(List<Card> cards, Rank calledRank, Combination combination) {
        CombinationType type = combination.getCombinationType();
        if (type == BOMB_FOUR_CARD) {
            return hasStrongBombFourCard(cards, calledRank, combination);
        }
        if (type == BOMB_STRAIGHT_FLUSH) {
            return hasStrongBombStraightFlush(cards, calledRank, combination);
        }
        return hasStrongBombFourCard(cards, calledRank, combination)
                || hasStrongBombStraightFlush(cards, calledRank, combination);
    }

    private static boolean hasStrongSingle(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        if (combinationTopCard.getRankPriority() >= calledRank.getPriority()) {
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
        Card combinationTopCard = combination.getTopCard();

        boolean hasPairWithCallCard = cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count() >= 2;

        boolean hasTripleWithCallCard = cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count() >= 3;

        List<Card> exceptCallCards = cards.stream()
                .filter(card -> card.getRank() != calledRank)
                .toList();

        boolean hasAnotherPair = false;
        boolean hasAnotherTriple = false;

        for (Card card : exceptCallCards) {
            if (exceptCallCards.stream()
                    .filter(c -> c.getRank() == card.getRank())
                    .count() >= 2) {
                hasAnotherPair = true;
                continue;
            }
            if (card.getRankPriority() > combinationTopCard.getRankPriority()) {
                if (exceptCallCards.stream()
                        .filter(c -> c.getRank() == card.getRank())
                        .count() >= 3) {
                    hasAnotherTriple = true;
                    break;
                }
            }
        }
        return (hasPairWithCallCard && hasAnotherTriple) ||
                (hasTripleWithCallCard && hasAnotherPair
                        && (calledRank.getPriority() > combinationTopCard.getRankPriority()));
    }

    private static boolean hasStrongStraight(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        int straightLength = combination.getCards().size();

        List<Rank> distinctRanks = new ArrayList<>(cards.stream()
                .filter(card -> !card.isDog() && !card.isDragon())
                .map(Card::getRank)
                .distinct()
                .sorted(Comparator.comparing(Rank::getPriority))
                .toList());

        if (distinctRanks.size() < straightLength) {
            return false;
        }

        for (int i = 0; i <= distinctRanks.size() - straightLength; i++) {
            List<Rank> partRanks = distinctRanks.subList(i, i + straightLength);
            Rank firstRank = partRanks.getFirst();
            Rank lastRank = partRanks.getLast();

            boolean isStraight = (lastRank.getPriority() - firstRank.getPriority() == straightLength - 1);
            boolean includesCalledRank = partRanks.contains(calledRank);
            boolean isStronger = (lastRank.getPriority() > combinationTopCard.getRankPriority());

            if (isStraight && includesCalledRank && isStronger) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStrongPairSequence(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        int pairSequenceLength = combination.getCards().size();
        int pairSequenceRankCount = pairSequenceLength / 2;

        Map<Rank, Long> rankCounts = cards.stream()
                .filter(card -> !card.isDog() && !card.isDragon()) // 특수 카드 제외
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

        for (int i = 0; i <= pairRanks.size() - pairSequenceRankCount; i++) {
            List<Rank> partRanks = pairRanks.subList(i, i + pairSequenceRankCount);
            Rank firstRank = partRanks.getFirst();
            Rank lastRank = partRanks.getLast();

            boolean isSequence = (lastRank.getPriority() - firstRank.getPriority() == pairSequenceRankCount - 1);
            boolean includesCalledRank = partRanks.contains(calledRank);
            boolean isStronger = (lastRank.getPriority() > combinationTopCard.getRankPriority());

            if (isSequence && includesCalledRank && isStronger) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasStrongBombFourCard(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
        CombinationType combinationType = combination.getCombinationType();

        long calledRankCount = cards.stream()
                .filter(card -> card.getRank() == calledRank)
                .count();

        if (calledRankCount >= 4) {
            if (combinationType != BOMB_STRAIGHT_FLUSH) {
                if (!combination.isBomb()
                        || calledRank.getPriority() > combinationTopCard.getRankPriority()) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasStrongBombStraightFlush(List<Card> cards, Rank calledRank, Combination combination) {
        Card combinationTopCard = combination.getTopCard();
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

        for (List<Card> suitCards : suitedCards.values()) {
            if (suitCards.size() < requireBombSize) {
                continue;
            }
            int start = 0;
            for (int i = 0; i < suitCards.size(); i++) {
                boolean isLast = false;
                if (i == suitCards.size() - 1) {
                    isLast = true;
                }

                boolean broken = false;
                if (!isLast) {
                    int nextPriority = suitCards.get(i + 1).getRankPriority();
                    int currentPriority = suitCards.get(i).getRankPriority();
                    if (!(nextPriority - currentPriority == 1)) {
                        broken = true;
                    }
                }

                if (!broken && !isLast) {
                    continue;
                }

                int end = i + 1;
                List<Card> partCards = suitCards.subList(start, end);

                if (partCards.size() < requireBombSize) {
                    start = i + 1;
                    continue;
                }
                boolean includesCalledRank = partCards.stream()
                        .anyMatch(card -> card.getRank() == calledRank);
                if (!includesCalledRank) {
                    start = i + 1;
                    continue;
                }
                if (!combination.isBomb() || combinationType == BOMB_FOUR_CARD) {
                    return true;
                }
                if (partCards.size() > requireBombSize) {
                    return true;
                }
                if (partCards.size() == requireBombSize &&
                        partCards.getLast().getRankPriority() > combinationTopCard.getRankPriority()) {
                    return true;
                }
                start = i + 1;
            }
        }
        return false;
    }
}

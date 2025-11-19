package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tichu.enums.Rank;

public class Player {
    private static final String NOT_IN_MY_CARDS = "보유하지 않은 카드가 존재합니다.";
    private final String name;
    private List<Card> myCards = new ArrayList<>();
    private List<Card> acquiredCards = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void addMyCards(List<Card> card) {
        myCards.addAll(card);
        Collections.sort(myCards);
    }

    public void addAcquireCards(List<Card> card) {
        acquiredCards.addAll(card);
    }

    public void validateContainMyCard(Card card) {
        if (!myCards.contains(card)) {
            throw new IllegalArgumentException(NOT_IN_MY_CARDS);
        }
    }

    public void removeMyCards(List<Card> cards) {
        myCards.removeAll(cards);
    }

    public String getName() {
        return name;
    }

    public int getCardCount() {
        return myCards.size();
    }

    public void sortMyCards() {
        Collections.sort(myCards);
    }

    public List<Card> getMyCards() {
        return List.copyOf(myCards);
    }

    public boolean hasMahjong() {
        return myCards.stream().anyMatch(Card::isMahjong);
    }

    public boolean hasStrongThanCombinationWithCall(Combination combination, Rank calledRank) {
        return CombinationFinder.hasStrongCombinationWithCall(myCards, calledRank, combination);
    }
}

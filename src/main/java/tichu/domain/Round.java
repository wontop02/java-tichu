package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Round {
    private int currentRound;
    private List<Card> cards;

    public Round() {
        currentRound = 0;
    }

    public void settingCards(Deck deck) {
        cards = new ArrayList<>(deck.getCards());
        Collections.shuffle(cards);
    }

    public List<Card> deal(int count) {
        List<Card> dealCards = new ArrayList<>(cards.subList(0, count));
        cards.subList(0, count).clear();
        return dealCards;
    }
}

package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Special;
import tichu.enums.Suit;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        makeCards();
    }

    private void makeCards() {
        this.cards.add(new Card(Special.DOG));
        this.cards.add(new Card(Special.MAHJONG));
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                this.cards.add(new Card(rank, suit));
            }
        }
        this.cards.add(new Card(Special.PHOENIX));
        this.cards.add(new Card(Special.DRAGON));
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> deal(int count) {
        List<Card> dealCards = new ArrayList<>(cards.subList(0, count));
        cards.subList(0, count).clear();
        return dealCards;
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}

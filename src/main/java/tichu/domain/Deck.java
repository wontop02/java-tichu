package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Suit;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() {
        makeCards();
    }

    private void makeCards() {
        for (Suit suit : Suit.values()) {
            if (suit == Suit.NONE) {
                continue;
            }
            for (Rank rank : Rank.values()) {
                if (rank.isSpecial()) {
                    continue;
                }
                this.cards.add(new Card(rank, suit));
            }
        }
        this.cards.add(new Card(Rank.DOG, Suit.NONE));
        this.cards.add(new Card(Rank.MAHJONG, Suit.NONE));
        this.cards.add(new Card(Rank.PHOENIX, Suit.NONE));
        this.cards.add(new Card(Rank.DRAGON, Suit.NONE));
    }

    public List<Card> deal(int count) {
        shuffle();
        List<Card> dealCards = new ArrayList<>(cards.subList(0, count));
        cards.subList(0, count).clear();
        return dealCards;
    }

    private void shuffle() {
        Collections.shuffle(cards);
    }

    public List<Card> getCards() {
        return List.copyOf(cards);
    }
}

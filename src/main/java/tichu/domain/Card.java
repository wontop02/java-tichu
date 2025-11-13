package tichu.domain;

import tichu.enums.Rank;
import tichu.enums.Special;
import tichu.enums.Suit;

public class Card {
    private final Suit suit;   // null이 될 수 있음 (special 카드)
    private final Rank rank;   // null이 될 수 있음 (special 카드)
    private final Special special; // 일반카드면 null

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.special = null;
    }

    public Card(Special special) {
        this.suit = null;
        this.rank = null;
        this.special = special;
    }
}

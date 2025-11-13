package tichu.domain;

import tichu.enums.Rank;
import tichu.enums.Special;
import tichu.enums.Suit;

public class Card implements Comparable<Card> {
    private final Rank rank;
    private final Suit suit;
    private final Special special; // 일반카드면 null

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
        this.special = null;
    }

    public Card(Special special) {
        this.suit = null;
        this.rank = null;
        this.special = special;
    }

    @Override
    public int compareTo(Card other) {
        if (this.priority() != other.priority()) {
            return this.priority() - other.priority();
        }
        if (this.special == null) {
            return this.suit.getPriority() - other.suit.getPriority();
        }
        return 0;
    }

    private int priority() {
        if (this.special != null) {
            return this.special.getPriority();
        }
        return this.rank.getPriority();
    }
}

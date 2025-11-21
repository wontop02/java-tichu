package tichu.domain;

import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Card card = (Card) o;
        return rank == card.rank &&
                suit == card.suit &&
                special == card.special;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit, special);
    }

    private int priority() {
        if (this.special != null) {
            return this.special.getPriority();
        }
        return this.rank.getPriority();
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public Special getSpecial() {
        return special;
    }

    public int getRankPriority() {
        if (isSpecial()) {
            return special.getPriority();
        }
        return rank.getPriority();
    }

    public boolean isSpecial() {
        return special != null;
    }

    public boolean isDog() {
        return special == Special.DOG;
    }

    public boolean isMahjong() {
        return special == Special.MAHJONG;
    }

    public boolean isDragon() {
        return special == Special.DRAGON;
    }

    public boolean isPhoenix() {
        return special == Special.PHOENIX;
    }
}

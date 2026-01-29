package tichu.domain;

import java.util.Objects;
import tichu.enums.Rank;
import tichu.enums.Suit;

public class Card implements Comparable<Card> {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    @Override
    public int compareTo(Card other) {
        if (this.rank.getPriority() != other.rank.getPriority()) {
            return this.rank.getPriority() - other.rank.getPriority();
        }
        if (!this.rank.isSpecial()) {
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
                suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public String getSpecialRank() {
        return rank.getRank();
    }

    public int getRankPriority() {
        return rank.getPriority();
    }

    public boolean isSpecial() {
        return rank.isSpecial();
    }

    public boolean isSubstitutePhoenix() {
        return (!rank.isSpecial()) && (suit == Suit.NONE);
    }

    public boolean isDog() {
        return rank == Rank.DOG;
    }

    public boolean isMahjong() {
        return rank == Rank.MAHJONG;
    }

    public boolean isDragon() {
        return rank == Rank.DRAGON;
    }

    public boolean isPhoenix() {
        return rank == Rank.PHOENIX;
    }
}

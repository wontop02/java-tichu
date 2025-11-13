package tichu.enums;

public enum Suit {
    SPADE("s"),
    DIAMOND("d"),
    HEART("h"),
    CLUB("c");

    private final String suit;

    Suit(String suit) {
        this.suit = suit;
    }

    public String getSuit() {
        return suit;
    }
}
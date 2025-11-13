package tichu.enums;

public enum Suit {
    SPADE("s", 1),
    DIAMOND("d", 2),
    HEART("h", 3),
    CLUB("c", 4);

    private final String suit;
    private final int priority;

    Suit(String suit, int priority) {
        this.suit = suit;
        this.priority = priority;
    }

    public String getSuit() {
        return suit;
    }

    public int getPriority() {
        return priority;
    }
}
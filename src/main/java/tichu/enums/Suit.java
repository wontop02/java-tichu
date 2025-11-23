package tichu.enums;

import java.util.Arrays;

public enum Suit {
    SPADE("s", 1),
    DIAMOND("d", 2),
    HEART("h", 3),
    CLUB("c", 4),
    // 봉이 대체된 경우
    NONE("", 5);

    private static final String INVALID_SUIT = "해당 카드 무늬는 존재하지 않습니다.";

    private final String suit;
    private final int priority;

    Suit(String suit, int priority) {
        this.suit = suit;
        this.priority = priority;
    }

    public static Suit valueOfSuit(String suit) {
        return Arrays.stream(values())
                .filter(s -> s.getSuit().equals(suit))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_SUIT));
    }

    public String getSuit() {
        return suit;
    }

    public int getPriority() {
        return priority;
    }
}
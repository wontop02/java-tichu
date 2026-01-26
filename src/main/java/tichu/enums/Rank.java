package tichu.enums;

import java.util.Arrays;

public enum Rank {
    TWO("2", 2, false),
    THREE("3", 3, false),
    FOUR("4", 4, false),
    FIVE("5", 5, false),
    SIX("6", 6, false),
    SEVEN("7", 7, false),
    EIGHT("8", 8, false),
    NINE("9", 9, false),
    TEN("10", 10, false),
    JACK("J", 11, false),
    QUEEN("Q", 12, false),
    KING("K", 13, false),
    ACE("A", 14, false),

    DOG("개", 0, true),
    MAHJONG("1", 1, true),
    PHOENIX("봉", 15, true),
    DRAGON("용", 16, true);

    private static final String INVALID_RANK = "해당 카드 등급은 존재하지 않습니다.";

    private final String rank;
    private final int priority;
    private final boolean isSpecial;

    Rank(String rank, int priority, boolean isSpecial) {
        this.rank = rank;
        this.priority = priority;
        this.isSpecial = isSpecial;
    }

    public static Rank valueOfRank(String rank) {
        return Arrays.stream(values())
                .filter(s -> s.getRank().equals(rank))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_RANK));
    }

    public String getRank() {
        return rank;
    }

    public int getPriority() {
        return priority;
    }

    public boolean isSpecial() {
        return isSpecial;
    }
}
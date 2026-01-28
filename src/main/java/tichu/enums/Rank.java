package tichu.enums;

import java.util.Arrays;

public enum Rank {
    TWO("2", 2, false, 0),
    THREE("3", 3, false, 0),
    FOUR("4", 4, false, 0),
    FIVE("5", 5, false, 5),
    SIX("6", 6, false, 0),
    SEVEN("7", 7, false, 0),
    EIGHT("8", 8, false, 0),
    NINE("9", 9, false, 0),
    TEN("10", 10, false, 10),
    JACK("J", 11, false, 0),
    QUEEN("Q", 12, false, 0),
    KING("K", 13, false, 10),
    ACE("A", 14, false, 0),

    DOG("개", 0, true, 0),
    MAHJONG("1", 1, true, 0),
    PHOENIX("봉", 15, true, -25),
    DRAGON("용", 16, true, 25);

    private static final String INVALID_RANK = "해당 카드 등급은 존재하지 않습니다.";

    private final String rank;
    private final int priority;
    private final boolean isSpecial;
    private final int score;

    Rank(String rank, int priority, boolean isSpecial, int score) {
        this.rank = rank;
        this.priority = priority;
        this.isSpecial = isSpecial;
        this.score = score;
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

    public int getScore() {
        return score;
    }
}
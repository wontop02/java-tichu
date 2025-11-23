package tichu.enums;

import java.util.Arrays;

public enum Rank {
    // 봉 처음 냈을 때, 1 냈을 때 전용
    ONE("1", 1),

    TWO("2", 2),
    THREE("3", 3),
    FOUR("4", 4),
    FIVE("5", 5),
    SIX("6", 6),
    SEVEN("7", 7),
    EIGHT("8", 8),
    NINE("9", 9),
    TEN("10", 10),
    JACK("J", 11),
    QUEEN("Q", 12),
    KING("K", 13),
    ACE("A", 14),

    // 용 전용 Rank
    DRAGON("용", 15);

    private static final String INVALID_RANK = "해당 카드 등급은 존재하지 않습니다.";

    private final String rank;
    private final int priority;

    Rank(String rank, int priority) {
        this.rank = rank;
        this.priority = priority;
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
}
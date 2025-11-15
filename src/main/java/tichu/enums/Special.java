package tichu.enums;

import java.util.Arrays;

public enum Special {
    DOG("개", 0),
    MAHJONG("1", 1),
    PHOENIX("봉", 15),
    DRAGON("용", 16);

    private static final String INVALID_SPECIAL = "해당 특수카드는 존재하지 않습니다.";

    private final String special;
    private final int priority;

    Special(String special, int priority) {
        this.special = special;
        this.priority = priority;
    }

    public String getSpecial() {
        return special;
    }

    public int getPriority() {
        return priority;
    }

    public static Special valueOfSpecial(String special) {
        return Arrays.stream(values())
                .filter(s -> s.getSpecial().equals(special))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(INVALID_SPECIAL));
    }
}

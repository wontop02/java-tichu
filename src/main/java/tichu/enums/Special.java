package tichu.enums;

public enum Special {
    DOG("개", 0),
    MAHJONG("1", 1),
    PHOENIX("봉", 15),
    DRAGON("용", 16);

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
}

package tichu.enums;

public enum Special {
    MAHJONG("1"),
    DOG("개"),
    PHOENIX("봉"),
    DRAGON("용");

    private final String special;

    Special(String special) {
        this.special = special;
    }

    public String getSpecial() {
        return special;
    }
}

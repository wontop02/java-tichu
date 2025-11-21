package tichu.enums;

public enum Place {
    FIRST(1),
    SECOND(2),
    THIRD(3),
    FOURTH(4);

    public final int place;

    Place(int place) {
        this.place = place;
    }

    public int getPlace() {
        return place;
    }
}

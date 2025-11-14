package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Player {
    private final String name;
    private List<Card> myCards = new ArrayList<>();
    private List<Card> acquiredCards = new ArrayList<>();

    public Player(String name) {
        this.name = name;
    }

    public void addMyCards(List<Card> card) {
        myCards.addAll(card);
    }

    public String getName() {
        return name;
    }

    public void sortMyCards() {
        Collections.sort(myCards);
    }

    public List<Card> getMyCards() {
        return List.copyOf(myCards);
    }
}

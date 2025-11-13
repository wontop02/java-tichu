package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TichuGame {
    private final List<Player> direction = new ArrayList<>();

    private final List<Player> red = new ArrayList<>();
    private final List<Player> blue = new ArrayList<>();

    public TichuGame(List<Player> players) {
        decisionDirection(players);
        decisionTeams();
    }

    private void decisionDirection(List<Player> players) {
        direction.addAll(players);
        Collections.shuffle(direction);
    }

    private void decisionTeams() {
        red.add(direction.get(0));
        red.add(direction.get(2));
        blue.add(direction.get(1));
        blue.add(direction.get(3));
    }
}

package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TichuGame {
    private static final String INCORRECT_PLAYER_NUMBER = "참가자 수는 반드시 4명이어야 합니다.";
    private static final String PLAYER_NOT_FOUND = "해당 이름의 플레이어를 찾을 수 없습니다: ";

    private final List<Player> players;
    private final List<Player> direction = new ArrayList<>();
    private final List<Player> red = new ArrayList<>();
    private final List<Player> blue = new ArrayList<>();

    public TichuGame(List<Player> players) {
        validatePlayers(players);
        this.players = players;
        decisionDirection(players);
        decisionTeams();
    }

    public Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(PLAYER_NOT_FOUND + name));
    }

    private void validatePlayers(List<Player> players) {
        if (players.size() != 4) {
            throw new IllegalArgumentException(INCORRECT_PLAYER_NUMBER);
        }
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

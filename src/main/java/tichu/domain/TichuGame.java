package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TichuGame {
    private static final String INCORRECT_PLAYER_NUMBER = "참가자 수는 반드시 4명이어야 합니다.";
    private static final String PLAYER_NOT_FOUND = "잘못된 플레이어 이름이 존재합니다.";

    private final List<Player> players;
    private int roundNumber = 0;

    public TichuGame(List<Player> players) {
        validatePlayers(players);
        this.players = players;
    }

    public Round startRound() {
        roundNumber++;
        List<Player> direction = decisionDirection(players);
        List<Player> red = List.of(direction.get(0), direction.get(2));
        List<Player> blue = List.of(direction.get(1), direction.get(3));

        Round round = new Round(players, direction, red, blue, roundNumber);
        round.settingRound();
        return round;
    }

    public Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException(PLAYER_NOT_FOUND));
    }

    private void validatePlayers(List<Player> players) {
        if (players.size() != 4) {
            throw new IllegalArgumentException(INCORRECT_PLAYER_NUMBER);
        }
    }

    private static List<Player> decisionDirection(List<Player> players) {
        List<Player> direction = new ArrayList<>(players);
        Collections.shuffle(direction);
        return direction;
    }
}

package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tichu.enums.Team;

public class TichuGame {
    private static final String INCORRECT_PLAYER_NUMBER = "참가자 수는 반드시 4명이어야 합니다.";

    private final List<Player> playersWithDirection;
    private int roundNumber = 0;

    public TichuGame(List<String> names) {
        validatePlayerCount(names);
        this.playersWithDirection = decisionDirection(names);
    }

    public Round startRound() {
        roundNumber++;

        return new Round(playersWithDirection);
    }

    private void validatePlayerCount(List<String> names) {
        if (names.size() != 4) {
            throw new IllegalArgumentException(INCORRECT_PLAYER_NUMBER);
        }
    }

    private List<Player> decisionDirection(List<String> names) {
        List<String> playerNames = new ArrayList<>(names);
        Collections.shuffle(playerNames);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String name = playerNames.get(i);
            if (i % 2 != 0) {
                players.add(new Player(name, Team.RED));
                continue;
            }
            players.add(new Player(name, Team.BLUE));
        }
        return players;
    }
}
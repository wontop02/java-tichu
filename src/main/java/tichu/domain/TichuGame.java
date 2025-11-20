package tichu.domain;

import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tichu.enums.Team;

public class TichuGame {
    private static final String INCORRECT_PLAYER_NUMBER = "참가자 수는 반드시 4명이어야 합니다.";

    private final List<Player> playersWithDirection;
    private final Map<Team, Integer> teamScore;
    private int roundNumber = 0;

    public TichuGame(List<String> names) {
        validatePlayerCount(names);
        this.playersWithDirection = decisionDirection(names);
        this.teamScore = new HashMap<>();
        teamScore.put(RED, 0);
        teamScore.put(BLUE, 0);
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
                players.add(new Player(name, RED));
                continue;
            }
            players.add(new Player(name, BLUE));
        }
        return players;
    }

    public void addTeamScore(Map<Team, Integer> roundScore) {
        teamScore.put(RED, teamScore.get(RED) + roundScore.get(RED));
        teamScore.put(BLUE, teamScore.get(BLUE) + roundScore.get(BLUE));
    }

    public boolean isEndTichuGame() {
        if (teamScore.get(RED) >= 1000 || teamScore.get(BLUE) >= 1000) {
            return true;
        }
        return false;
    }

    public Team tichuGameWinner() {
        boolean redReached = teamScore.get(RED) >= 1000;
        boolean blueReached = teamScore.get(BLUE) >= 1000;

        if (redReached && !blueReached) {
            return RED;
        }
        if (!redReached && blueReached) {
            return BLUE;
        }
        // 둘 다 1000 이상인 경우 점수로 비교
        if (teamScore.get(RED) > teamScore.get(BLUE)) {
            return RED;
        }
        if (teamScore.get(BLUE) > teamScore.get(RED)) {
            return BLUE;
        }
        // 무승부
        return null;
    }
}
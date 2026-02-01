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
    private static final int GOAL_SCORE = 1000;
    private static final int MAX_PLAYER_NAME = 5;
    private static final int NUMBER_OF_PLAYER = 4;

    private static final String INCORRECT_PLAYER_NUMBER = "참가자 수는 반드시 4명이어야 합니다.";
    private static final String INVALID_NAME_LENGTH = "참가자 이름은 최대 5글자로 입력 가능합니다.";

    private final List<Player> playersWithDirection;
    private final Map<Team, Integer> teamScore;
    private int roundNumber = 0;

    public TichuGame(List<String> names) {
        validate(names);
        this.playersWithDirection = decisionDirection(names);
        this.teamScore = new HashMap<>();
        teamScore.put(RED, 0);
        teamScore.put(BLUE, 0);
    }

    private void validate(List<String> names) {
        validatePlayerCount(names);
        validatePlayerName(names);
    }

    private void validatePlayerCount(List<String> names) {
        if (names.size() != NUMBER_OF_PLAYER) {
            throw new IllegalArgumentException(INCORRECT_PLAYER_NUMBER);
        }
    }

    private void validatePlayerName(List<String> names) {
        for (String name : names) {
            if (name.length() > MAX_PLAYER_NAME) {
                throw new IllegalArgumentException(INVALID_NAME_LENGTH);
            }
        }
    }

    private List<Player> decisionDirection(List<String> names) {
        List<String> playerNames = new ArrayList<>(names);
        Collections.shuffle(playerNames);
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
            String name = playerNames.get(i);
            if (i % 2 != 0) {
                players.add(new Player(name, BLUE));
                continue;
            }
            players.add(new Player(name, RED));
        }
        return players;
    }

    public Round startRound() {
        roundNumber++;
        return new Round(playersWithDirection);
    }

    public Map<Team, Integer> addTeamScore(Map<Team, Integer> roundScore) {
        teamScore.put(RED, teamScore.get(RED) + roundScore.get(RED));
        teamScore.put(BLUE, teamScore.get(BLUE) + roundScore.get(BLUE));
        return Map.copyOf(teamScore);
    }

    public boolean isEndTichuGame() {
        return teamScore.get(RED) >= GOAL_SCORE || teamScore.get(BLUE) >= GOAL_SCORE;
    }

    public Team tichuGameWinner() {
        boolean redReached = teamScore.get(RED) >= GOAL_SCORE;
        boolean blueReached = teamScore.get(BLUE) >= GOAL_SCORE;

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

    public int getRoundNumber() {
        return roundNumber;
    }

    public List<Player> getPlayersWithDirection() {
        return List.copyOf(playersWithDirection);
    }
}
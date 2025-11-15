package tichu.domain;

import java.util.ArrayList;
import java.util.List;

public class Round {
    private static final String ALREADY_CALLED_TICHU = "이미 티츄를 부른 플레이어가 존재합니다.";
    private static final String PLAYER_NOT_FOUND = "잘못된 플레이어 이름이 존재합니다.";

    private final List<Player> players;

    private Deck deck;
    private List<Player> largeTichu;
    private List<Player> smallTichu;
    private final List<Player> direction;
    private final List<Player> red;
    private final List<Player> blue;
    private int roundNumber;

    public Round(List<Player> players,
                 List<Player> direction,
                 List<Player> red,
                 List<Player> blue,
                 int roundNumber) {
        largeTichu = new ArrayList<>();
        smallTichu = new ArrayList<>();
        this.players = players;
        this.direction = direction;
        this.red = red;
        this.blue = blue;
        this.roundNumber = roundNumber;
    }

    public void settingRound() {
        deck = new Deck();
    }

    public void dealCards8() {
        dealCards(8);
    }

    public void addLargeTichu(List<String> names) {
        for (String name : names) {
            Player player = findPlayerByName(name); // String → Player
            largeTichu.add(player);
        }
    }

    public void dealCards6() {
        dealCards(6);
    }

    public void addSmallTichu(List<String> names) {
        for (String name : names) {
            Player player = findPlayerByName(name);
            validateNotAlreadyCalledTichu(player);
            smallTichu.add(player);
        }
    }

    public void dealCards(int count) {
        for (Player player : players) {
            player.addMyCards(deck.deal(count));
        }
    }

    public void validatePlayerNames(List<String> names) {
        names.forEach(this::findPlayerByName);
    }

    private Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PLAYER_NOT_FOUND));
    }

    private void validateNotAlreadyCalledTichu(Player player) {
        if (largeTichu.contains(player) || smallTichu.contains(player)) {
            throw new IllegalArgumentException(ALREADY_CALLED_TICHU);
        }
    }
}

package tichu.domain;

import java.util.ArrayList;
import java.util.List;

public class Round {
    private static final String ALREADY_CALLED_TICHU = "이미 티츄를 부른 플레이어입니다: ";

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

    public void addLargeTichu(Player player) {
        largeTichu.add(player);
    }

    public void dealCards6() {
        dealCards(6);
    }

    public void addSmallTichu(Player player) {
        if (largeTichu.contains(player) || smallTichu.contains(player)) {
            throw new IllegalArgumentException(ALREADY_CALLED_TICHU + player.getName());
        }
        smallTichu.add(player);
    }

    public void dealCards(int count) {
        for (Player player : players) {
            player.addMyCards(deck.deal(count));
        }
    }
}

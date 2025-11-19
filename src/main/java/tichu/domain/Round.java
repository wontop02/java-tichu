package tichu.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tichu.enums.Place;

public class Round {
    private static final String ALREADY_CALLED_TICHU = "이미 티츄를 부른 플레이어가 존재합니다.";
    private static final String PLAYER_NOT_FOUND = "잘못된 플레이어 이름이 존재합니다.";
    private static final String NOT_FOUND_HAS_MAJHONG = "1 카드를 가진 플레이어가 존재하지 않습니다.";
    private final List<Player> players;
    private final Map<Place, Player> playerPlace = new HashMap<>();
    private Player lastPhaseWinner;

    private static final Place[] ORDER = {
            Place.FIRST,
            Place.SECOND,
            Place.THIRD,
            Place.FOURTH
    };

    private Deck deck;
    private List<Player> largeTichu;
    private List<Player> smallTichu;
    private final List<Player> direction;
    private final List<Player> red;
    private final List<Player> blue;
    private int roundNumber = 0;
    private int phaseNumber = 0;

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

    public List<Player> getDirection() {
        return List.copyOf(direction);
    }

    public void tradeCards(List<List<Card>> received) {
        for (int i = 0; i < 4; i++) {
            direction.get(i).addMyCards(received.get(i));
        }
    }

    public Phase startPhase() {
        phaseNumber++;
        Player startPlayer;
        if (phaseNumber == 1) {
            startPlayer = decideStartPlayer();
            return new Phase(startPlayer, direction);
        }
        startPlayer = lastPhaseWinner;
        return new Phase(startPlayer, direction);
    }

    public void endPhase(Phase phase) {
        phase.giveCardsToWinner();
        lastPhaseWinner = phase.getPhaseWinner();
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

    private Player decideStartPlayer() {
        return players.stream()
                .filter(Player::hasMahjong)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_FOUND_HAS_MAJHONG));
    }

    private void checkRoundPlace(Player player) {
        if (player.getCardCount() != 0) {
            return;
        }
        for (Place place : Place.values()) {
            if (!playerPlace.containsKey(place)) {
                playerPlace.put(place, player);
                break;
            }
        }
    }

    private boolean isEndPlayer(Player player) {
        return playerPlace.containsValue(player);
    }

    public boolean isRoundEnd() {
        if (playerPlace.size() == 3 && !playerPlace.containsKey(Place.FOURTH)) {
            for (Player player : direction) {
                if (!playerPlace.containsValue(player)) {
                    playerPlace.put(Place.FOURTH, player);
                    break;
                }
            }
        }
        return playerPlace.size() == 4;
    }
}

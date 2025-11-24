package tichu.domain;

import static tichu.enums.Place.FIRST;
import static tichu.enums.Place.FOURTH;
import static tichu.enums.Place.SECOND;
import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import tichu.enums.Place;
import tichu.enums.Rank;
import tichu.enums.Team;
import tichu.exception.RoundEndSignal;

public class Round {
    private static final String ALREADY_CALLED_TICHU = "이미 티츄를 부른 플레이어가 존재합니다.";
    private static final String PLAYER_NOT_FOUND = "잘못된 플레이어 이름이 존재합니다.";
    private static final String NOT_FOUND_HAS_MAJHONG = "1 카드를 가진 플레이어가 존재하지 않습니다.";
    private static final String NOT_FOUND_PLACE = "등수를 찾을 수 없습니다.";
    private static final String NOT_FOUND_NEXT_PLAYER = "페이즈 시작이 가능한 플레이어가 없습니다.";
    private static final String DOUBLE_WIN = "\n같은 팀이 1등과 2등을 차지해 라운드를 종료합니다.";

    private final List<Player> players;
    private Rank calledRank = null;
    private boolean isCallActive = false;
    private final Map<Place, Player> playerPlace = new HashMap<>();
    private Player lastPhaseWinner;

    private Deck deck;
    private int phaseNumber = 0;

    public Round(List<Player> playersWithDirection) {
        this.players = playersWithDirection;
        deck = new Deck();
    }

    public void dealCards8() {
        dealCards(8);
    }

    public void dealCards6() {
        dealCards(6);
    }

    private void dealCards(int count) {
        for (Player player : players) {
            player.addMyCards(deck.deal(count));
        }
    }

    public void addLargeTichu(List<String> names) {
        for (String name : names) {
            Player player = findPlayerByName(name);
            player.callLargeTichu();
        }
    }

    public void addSmallTichu(List<String> names) {
        for (String name : names) {
            Player player = findPlayerByName(name);
            validateNotAlreadyCalledTichu(player);
            player.callSmallTichu();
        }
    }

    private void validateNotAlreadyCalledTichu(Player player) {
        if (player.getLargeTichuStatus() || player.getSmallTichuStatus()) {
            throw new IllegalArgumentException(ALREADY_CALLED_TICHU);
        }
    }

    public void tradeCards(List<List<Card>> received) {
        for (int i = 0; i < 4; i++) {
            players.get(i).addMyCards(received.get(i));
        }
    }

    public Phase startPhase() {
        phaseNumber++;
        Player startPlayer;
        if (phaseNumber == 1) {
            startPlayer = decideStartPlayer();
            return new Phase(startPlayer, players);
        }
        startPlayer = lastPhaseWinner;
        if (isEndPlayer(startPlayer)) {
            startPlayer = findNextNotEndPlayer(startPlayer);
        }
        return new Phase(startPlayer, players);
    }

    private Player findNextNotEndPlayer(Player startPlayer) {
        int index = players.indexOf(startPlayer);

        for (int i = 0; i < 4; i++) {
            index = (index + 1) % players.size();
            Player nextPlayer = players.get(index);

            if (!isEndPlayer(nextPlayer)) {
                return nextPlayer;
            }
        }
        throw new IllegalStateException(NOT_FOUND_NEXT_PLAYER);
    }

    private Player decideStartPlayer() {
        return players.stream()
                .filter(Player::hasMahjong)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_FOUND_HAS_MAJHONG));
    }

    public void callRank(Rank rank) {
        this.calledRank = rank;
        this.isCallActive = true;
    }

    public void callEnd() {
        this.calledRank = null;
        this.isCallActive = false;
    }

    public void endPhase(Phase phase) {
        phase.giveCardsToWinner();
        lastPhaseWinner = phase.getPhaseWinner();
    }

    public void endPhase(Phase phase, String name) {
        phase.giveCardsToPlayerWithDragon(name);
        lastPhaseWinner = phase.getPhaseWinner();
    }

    public void endPhaseWithDog(Phase phase) {
        lastPhaseWinner = phase.giveCardsUseDogPlayer();
    }

    public void validatePlayerNames(List<String> names) {
        names.forEach(this::findPlayerByName);
    }

    public boolean checkRoundPlace(Player player) {
        if (player.getCardCount() != 0) {
            return false;
        }
        if (playerPlace.containsValue(player)) {
            return false;
        }
        for (Place place : Place.values()) {
            if (!playerPlace.containsKey(place)) {
                playerPlace.put(place, player);
                if (place == SECOND) {
                    // 같은 팀이 1, 2등이면 강제 종료
                    Player first = playerPlace.get(FIRST);
                    Player second = playerPlace.get(SECOND);

                    if (first.getTeam() == second.getTeam()) {
                        throw new RoundEndSignal(DOUBLE_WIN);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public int getPlace(Player player) {
        return playerPlace.entrySet().stream()
                .filter(entry -> entry.getValue().equals(player))
                .map(entry -> entry.getKey().getPlace())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_FOUND_PLACE));
    }

    public boolean isRoundEnd() {
        if (playerPlace.size() == 2) {
            Player first = playerPlace.get(FIRST);
            Player second = playerPlace.get(SECOND);
            return first.getTeam() == second.getTeam();
        }
        if (playerPlace.size() == 3 && !playerPlace.containsKey(FOURTH)) {
            for (Player player : players) {
                if (!playerPlace.containsValue(player)) {
                    playerPlace.put(FOURTH, player);
                    break;
                }
            }
        }
        return playerPlace.size() == 4;
    }

    public Map<Team, Integer> calculateScore() {
        Map<Team, Integer> teamScore = calculateCardScore();
        Player first = playerPlace.get(FIRST);

        for (Player player : players) {
            if (player.getLargeTichuStatus()) {
                applyTichuScore(teamScore, player, first, 200);
                continue;
            }

            if (player.getSmallTichuStatus()) {
                applyTichuScore(teamScore, player, first, 100);
            }
        }
        for (Player player : players) {
            player.resetStatus();
        }
        return Map.copyOf(teamScore);
    }

    private Map<Team, Integer> calculateCardScore() {
        Map<Team, Integer> cardScore = new HashMap<>();
        cardScore.put(RED, 0);
        cardScore.put(BLUE, 0);

        Player first = playerPlace.get(FIRST);
        Player second = playerPlace.get(SECOND);
        if (first.getTeam() == second.getTeam()) {
            Team team = first.getTeam();
            cardScore.put(team, 200);
            return cardScore;
        }

        Player fourth = playerPlace.get(FOURTH);
        giveFourthCards(cardScore, first, fourth);

        for (Player player : players) {
            if (player == fourth) {
                continue;
            }
            int score = player.calculateAcquireCardScore();
            cardScore.put(player.getTeam(),
                    cardScore.get(player.getTeam()) + score);
        }
        return cardScore;
    }

    private void giveFourthCards(Map<Team, Integer> cardScore, Player first, Player fourth) {
        Team fourthTeam = fourth.getTeam();
        Team otherTeam = RED;
        if (fourthTeam == RED) {
            otherTeam = BLUE;
        }

        // 1등에게 얻은 점수 줌
        first.addAcquireCards(fourth.getAcquiredCards());

        // 다른 팀에게 마지막까지 들고 있던 카드 점수 줌
        int fourthHandScore = fourth.calculateMyCardScore();
        cardScore.put(otherTeam, cardScore.get(otherTeam) + fourthHandScore);
        
        fourth.resetStatus();
    }

    private void applyTichuScore(Map<Team, Integer> teamScore, Player player, Player first, int point) {
        Team team = player.getTeam();
        if (player == first) {
            teamScore.put(team, teamScore.get(team) + point);
            return;
        }
        teamScore.put(team, teamScore.get(team) - point);
    }

    private boolean isEndPlayer(Player player) {
        return playerPlace.containsValue(player);
    }

    private Player findPlayerByName(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PLAYER_NOT_FOUND));
    }

    public List<Player> getPlayers() {
        return List.copyOf(players);
    }

    public Rank getCalledRank() {
        return calledRank;
    }

    public boolean isCallActive() {
        return isCallActive;
    }

    public int getPhaseNumber() {
        return phaseNumber;
    }

    public Map<Place, Player> getPlayerPlace() {
        return Map.copyOf(playerPlace);
    }
}

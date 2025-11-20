package tichu.domain;

import static tichu.enums.CombinationType.SINGLE;

import java.util.ArrayList;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Team;

public class Phase {
    private static final String LOW_OR_SAME_COMBINATION = "이전 조합보다 낮거나 같은 조합을 낼 수 없습니다.";
    private static final String START_PLAYER_CANNOT_PASS = "선 플레이어는 패스할 수 없습니다.";
    private static final String MUST_COMBINATION_INCLUDE_CALL = "콜을 포함한 조합을 내야 합니다.";
    private static final String CANNOT_SELECT_SAME_TEAM = "같은 팀원은 선택할 수 없습니다.";
    private static final String PLAYER_NOT_FOUND = "잘못된 플레이어 이름이 존재합니다.";
    private static final String NOT_BOMB = "포 카드나 스트레이트 플러쉬가 아닙니다.";
    private static final String MUST_STRONG_BOMB = "이전 폭탄보다 강한 폭탄만 낼 수 있습니다.";
    private static final String CANNOT_USE_BOMB = "폭탄은 첫 조합으로 낼 수 없습니다.";
    private static final String TEAM_MEMBER_NOT_FOUND = "팀원을 찾을 수 없습니다.";

    private final List<Player> players;
    private final Player startPlayer;
    private final List<Card> phaseCards = new ArrayList<>();
    private boolean[] passed;

    private int turnIndex;
    private Rank calledRank = null;
    private boolean isCallActive = false;
    private Player phaseWinner;
    private Combination lastCombination;
    private Double singleValue = null;

    public Phase(Player startPlayer, List<Player> players) {
        this.startPlayer = startPlayer;
        this.players = new ArrayList<>(players);
        this.turnIndex = players.indexOf(startPlayer);
        this.passed = new boolean[4];
    }

    public void callRank(Rank rank) {
        calledRank = rank;
        isCallActive = true;
    }

    public void pass(Player player) {
        passed[players.indexOf(player)] = true;
        nextTurn();
    }

    public void useBomb(String name, List<Card> cards) {
        Player player = findPlayer(name);
        Combination bombCombination = new Combination(cards);
        if (lastCombination == null) {
            throw new IllegalArgumentException(CANNOT_USE_BOMB);
        }
        if (!bombCombination.isBomb()) {
            throw new IllegalArgumentException(NOT_BOMB);
        }
        if (bombCombination.compareTo(lastCombination) != 1) {
            throw new IllegalArgumentException(MUST_STRONG_BOMB);
        }
        lastCombination = bombCombination;
        turnIndex = players.indexOf(player);
        phaseWinner = player;
        passed = new boolean[4];

        player.removeMyCards(bombCombination.getCards());
        phaseCards.addAll(bombCombination.getCards());

        nextTurn();
    }

    public void useDog(Player player, Combination combination) {
        lastCombination = null;
        phaseWinner = null;

        Player otherMember = findOtherTeamMember(player);
        turnIndex = players.indexOf(otherMember);

        player.removeMyCards(combination.getCards());
        phaseCards.addAll(combination.getCards());
        passed = new boolean[4];
        if (otherMember.getCardCount() == 0) {
            nextTurn();
        }
    }

    private Player findOtherTeamMember(Player player) {
        Team team = player.getTeam();
        return players.stream()
                .filter(p -> p.getTeam() == team)
                .filter(p -> p != player)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(TEAM_MEMBER_NOT_FOUND));
    }

    public boolean isPhaseEnd() {
        int passCount = 0;
        for (int i = 0; i < passed.length; i++) {
            if (passed[i] && players.get(i) != phaseWinner) {
                passCount++;
            }
        }
        return passCount == 3;
    }

    public Player getCurrentPlayer() {
        return players.get(turnIndex);
    }

    public Player getPhaseWinner() {
        return phaseWinner;
    }

    public boolean winWithDragon() {
        return phaseCards.getLast().isDragon();
    }

    public void giveCardsToWinner() {
        phaseWinner.addAcquireCards(phaseCards);
    }

    public Combination getLastCombination() {
        return lastCombination;
    }

    public void giveCardsToPlayerWithDragon(String name) {
        Player player = findPlayer(name);
        if (player.getTeam() == phaseWinner.getTeam()) {
            throw new IllegalArgumentException(CANNOT_SELECT_SAME_TEAM);
        }
        player.addAcquireCards(phaseCards);
    }

    public void evaluateCombination(Player player, Combination combination) {
        if (isCallActive) {
            includeCallRank(player, combination);
        }
        if (lastCombination == null && combination.isDog()) {
            useDog(player, combination);
            return;
        }
        if (combination.getCombinationType() == SINGLE
                && lastCombination.getCombinationType() == SINGLE) {
            evaluateSingleCombination(combination);
        }
        if (lastCombination != null && combination.compareTo(lastCombination) != 1) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
        lastCombination = combination;
        phaseWinner = player;
        passed = new boolean[4];

        player.removeMyCards(combination.getCards());
        phaseCards.addAll(combination.getCards());

        nextTurn();
    }

    public void validatePass(Player player) {
        validateStartPlayerPass();
        validateHasCallCardPass(player);
    }

    private void validateStartPlayerPass() {
        if (lastCombination == null) {
            throw new IllegalArgumentException(START_PLAYER_CANNOT_PASS);
        }
    }

    private void validateHasCallCardPass(Player player) {
        if (!isCallActive) {
            return;
        }
        if (player.hasStrongThanCombinationWithCall(lastCombination, calledRank)) {
            throw new IllegalArgumentException(MUST_COMBINATION_INCLUDE_CALL);
        }
    }

    private void nextTurn() {
        for (int i = 0; i < 4; i++) {
            turnIndex = (turnIndex + 1) % 4;
            Player nextPlayer = players.get(turnIndex);
            // 카드 없으면 자동 패스 처리
            if (nextPlayer.getCardCount() == 0) {
                passed[turnIndex] = true;
                continue;
            }
            if (!passed[turnIndex]) {
                return;
            }
        }
    }

    private void callEnd() {
        calledRank = null;
        isCallActive = false;
    }

    private Player findPlayer(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PLAYER_NOT_FOUND));
    }

    private void evaluateSingleCombination(Combination combination) {
        if (lastCombination.isDragon()) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
        if (lastCombination.getTopCard().isPhoenix()) {
            if (phaseCards.size() != 1) {
                int phoenixIndex = phaseCards.size() - 1;
                Card beforePhoenix = phaseCards.get(phoenixIndex - 1);
                if (beforePhoenix.getRankPriority()
                        > combination.getTopCard().getRankPriority()) {
                    throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
                }
            }
        }
        if (lastCombination.getTopCard().getRankPriority() > combination.getTopCard().getRankPriority()) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
    }

    private void includeCallRank(Player player, Combination combination) {
        if (!combination.hasCallRank(calledRank)
                && player.hasStrongThanCombinationWithCall(lastCombination, calledRank)) {
            throw new IllegalArgumentException(MUST_COMBINATION_INCLUDE_CALL);
        }
        if (combination.hasCallRank(calledRank)) {
            callEnd();
        }
    }
}

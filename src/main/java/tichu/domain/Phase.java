package tichu.domain;

import java.util.ArrayList;
import java.util.List;
import tichu.enums.Rank;

public class Phase {
    private static final String LOW_OR_SAME_COMBINATION = "이전 조합보다 낮거나 같은 조합을 낼 수 없습니다.";
    private static final String START_PLAYER_CANNOT_PASS = "선 플레이어는 패스할 수 없습니다.";
    private static final String MUST_COMBINATION_INCLUDE_CALL = "콜을 포함한 조합을 내야 합니다.";
    private static final String CANNOT_SELECT_SAME_TEAM = "같은 팀원은 선택할 수 없습니다.";
    private final List<Player> direction;
    private final Player startPlayer;
    private final List<Card> phaseCards = new ArrayList<>();
    private boolean[] passed;

    private int turnIndex;
    private Rank calledRank = null;
    private boolean isCallActive = false;
    private Player phaseWinner;
    private Combination lastCombination;

    public Phase(Player startPlayer, List<Player> direction) {
        this.startPlayer = startPlayer;
        this.direction = new ArrayList<>(direction);
        this.turnIndex = direction.indexOf(startPlayer);
        this.passed = new boolean[4];
    }

    public void callRank(Rank rank) {
        calledRank = rank;
        isCallActive = true;
    }

    public boolean isPlayerPassed(Player player) {
        return passed[direction.indexOf(player)];
    }

    public void pass(Player player) {
        passed[direction.indexOf(player)] = true;
        nextTurn();
    }

    public boolean isPhaseEnd() {
        int passCount = 0;
        for (int i = 0; i < passed.length; i++) {
            if (passed[i] && direction.get(i) != phaseWinner) {
                passCount++;
            }
        }
        return passCount == 3;
    }

    public Player getCurrentPlayer() {
        return direction.get(turnIndex);
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

    public void giveCardsToPlayerWithDragon(Player player) {
        if (player.getTeam() == phaseWinner.getTeam()) {
            throw new IllegalArgumentException(CANNOT_SELECT_SAME_TEAM);
        }
        player.addAcquireCards(phaseCards);
    }

    public void evaluateCombination(Player player, Combination combination) {
        if (lastCombination != null && combination.compareTo(lastCombination) != 1) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
        if (isCallActive) {
            if (!combination.hasCallRank(calledRank)
                    && player.hasStrongThanCombinationWithCall(lastCombination, calledRank)) {
                throw new IllegalArgumentException(MUST_COMBINATION_INCLUDE_CALL);
            }
            if (combination.hasCallRank(calledRank)) {
                callEnd();
            }
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
            if (!passed[turnIndex]) {
                return;
            }
        }
    }

    private void callEnd() {
        calledRank = null;
        isCallActive = false;
    }
}

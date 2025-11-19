package tichu.domain;

import java.util.ArrayList;
import java.util.List;
import tichu.enums.Rank;

public class Phase {
    private static final String LOW_OR_SAME_COMBINATION = "이전 조합보다 낮거나 같은 조합을 낼 수 없습니다.";
    private static final String START_PLAYER_CANNOT_PASS = "선 플레이어는 패스할 수 없습니다.";
    private static final String CALL_CANNOT_PASS = "콜을 포함한 조합을 내야 합니다.";
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

    public void callEnd() {
        calledRank = null;
        isCallActive = false;
    }

    public void nextTurn() {
        for (int i = 0; i < 4; i++) {
            turnIndex = (turnIndex + 1) % 4;
            if (!passed[turnIndex]) {
                return;
            }
        }
    }

    public boolean isPlayerPassed(Player player) {
        return passed[direction.indexOf(player)];
    }

    public void pass(Player player) {
        passed[direction.indexOf(player)] = true;
    }

    public boolean isAllOthersPassed() {
        int passCount = 0;
        for (int i = 0; i < passed.length; i++) {
            if (passed[i] && direction.get(i) != phaseWinner) {
                passCount++;
            }
        }
        return passCount == 3;
    }

    public boolean isPhaseEnd() {
        return isAllOthersPassed();
    }

    public Player getCurrentPlayer() {
        return direction.get(turnIndex);
    }

    public Combination getLastCombination() {
        return lastCombination;
    }

    public Player getPhaseWinner() {
        return phaseWinner;
    }

    public void evaluateCombination(Player player, Combination combination) {
        if (lastCombination != null && combination.compareTo(lastCombination) != 1) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
        lastCombination = combination;
        phaseWinner = player;
        passed = new boolean[4];

        phaseCards.addAll(combination.getCards());
    }

    public void validatePass(Player player) {
        validateStartPlayerPass(player);
        validateHasCallCardPass(player);
    }

    private void validateStartPlayerPass(Player player) {
        if (player == startPlayer) {
            throw new IllegalArgumentException(START_PLAYER_CANNOT_PASS);
        }
    }

    private void validateHasCallCardPass(Player player) {
        if (!isCallActive) {
            return;
        }
        if (lastCombination == null
                || player.hasStrongThanCombinationWithCall(lastCombination, calledRank)) {
            throw new IllegalArgumentException(CALL_CANNOT_PASS);
        }
    }
}

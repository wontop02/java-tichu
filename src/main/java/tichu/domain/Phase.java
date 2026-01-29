package tichu.domain;

import static tichu.enums.CombinationType.SINGLE;

import java.util.ArrayList;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Team;
import tichu.exception.PhaseEndSignal;

public class Phase {
    private static final String LOW_OR_SAME_COMBINATION = "이전 조합보다 낮거나 같은 조합을 낼 수 없습니다.";
    private static final String START_PLAYER_CANNOT_PASS = "선 플레이어는 패스할 수 없습니다.";
    private static final String MUST_COMBINATION_INCLUDE_CALL = "콜을 포함한 조합을 내야 합니다.";
    private static final String CANNOT_SELECT_SAME_TEAM = "같은 팀원은 선택할 수 없습니다.";
    private static final String CANNOT_SELECT_END_PLAYER = "이미 라운드를 종료한 플레이어는 선택할 수 없습니다.";
    private static final String PLAYER_NOT_FOUND = "잘못된 플레이어 이름이 존재합니다.";
    private static final String NOT_BOMB = "포 카드나 스트레이트 플러쉬가 아닙니다.";
    private static final String MUST_STRONG_BOMB = "이전 폭탄보다 강한 폭탄만 낼 수 있습니다.";
    private static final String CANNOT_USE_BOMB = "폭탄은 첫 조합으로 낼 수 없습니다.";
    private static final String TEAM_MEMBER_NOT_FOUND = "팀원을 찾을 수 없습니다.";
    private static final String INCOMPARABLE = "서로 다른 조합은 비교할 수 없습니다.";
    private static final String USE_DOG = "개를 사용했습니다. 같은 팀원에게 선이 넘어갑니다.";

    private final List<Player> players;
    private final Player startPlayer;
    private final List<Card> phaseCards = new ArrayList<>();
    private boolean[] passed;

    private int turnIndex;
    private Player phaseWinner;
    private Combination lastCombination;

    public Phase(Player startPlayer, List<Player> players) {
        this.startPlayer = startPlayer;
        this.players = new ArrayList<>(players);
        this.turnIndex = players.indexOf(startPlayer);
        this.passed = new boolean[4];
    }

    public void evaluateCombination(Player player, Combination combination, Round round) {
        if (round.isCallActive()) {
            includeCallRank(player, combination, round);
        }
        // 기존에 조합이 없는 경우
        if (lastCombination == null) {
            if (combination.isDog()) {
                useDog(player, combination);
                return;
            }
            registerCombination(player, combination);
            return;
        }
        // 새 조합이 폭탄인 경우
        if (combination.isBomb()) {
            if (lastCombination.isBomb()) {
                if (combination.compareTo(lastCombination) != 1) {
                    throw new IllegalArgumentException(MUST_STRONG_BOMB);
                }
            }
            registerCombination(player, combination);
            return;
        }
        // 기존 조합은 폭탄인데 새 조합이 폭탄이 아닌 경우
        if (lastCombination.isBomb() && !combination.isBomb()) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
        // 타입이 다른 경우
        if (combination.getCombinationType() != lastCombination.getCombinationType()) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        // 카드 장수가 다른 경우
        if (combination.getCards().size() != lastCombination.getCards().size()) {
            throw new IllegalArgumentException(INCOMPARABLE);
        }
        if (combination.getCombinationType() == SINGLE
                && lastCombination.getCombinationType() == SINGLE) {
            if (evaluateSingleCombination(combination) != -1) {
                throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
            }
            registerCombination(player, combination);
            return;
        }
        // 일반 조합 비교
        if (combination.compareTo(lastCombination) != 1) {
            throw new IllegalArgumentException(LOW_OR_SAME_COMBINATION);
        }
        registerCombination(player, combination);
    }

    private void includeCallRank(Player player, Combination combination, Round round) {
        Rank calledRank = round.getCalledRank();
        if (!combination.hasCallRank(calledRank)
                && player.hasStrongThanCombinationWithCall(lastCombination, calledRank)) {
            throw new IllegalArgumentException(MUST_COMBINATION_INCLUDE_CALL);
        }
        if (combination.hasCallRank(calledRank)) {
            round.callEnd();
        }
    }

    private int evaluateSingleCombination(Combination combination) {
        if (lastCombination == null) {
            return -1;
        }
        if (lastCombination.isDragon()) {
            return 1;
        }
        if (combination.isDragon()) {
            return -1;
        }
        Card lastTopCard = lastCombination.getTopCard();
        Card newTopCard = combination.getTopCard();
        if (lastTopCard.isPhoenix()) {
            int phoenixIndex = phaseCards.size() - 1;
            boolean hasBeforeCard = phoenixIndex > 0;
            if (hasBeforeCard) {
                return evaluatePhoenixSingleCombination(phoenixIndex, newTopCard);
            }
        }
        if (lastTopCard.getRankPriority() > newTopCard.getRankPriority()) {
            return 1;
        }
        if (lastTopCard.getRankPriority() < newTopCard.getRankPriority()) {
            return -1;
        }
        return 0;
    }

    private int evaluatePhoenixSingleCombination(int phoenixIndex, Card newTopCard) {
        Card beforePhoenix = phaseCards.get(phoenixIndex - 1);
        int beforePhoenixRank = beforePhoenix.getRankPriority();
        int combinationRank = newTopCard.getRankPriority();

        if (beforePhoenixRank > combinationRank) {
            return 1;
        }
        if (beforePhoenixRank < combinationRank) {
            return -1;
        }
        return 0;
    }

    private void registerCombination(Player player, Combination combination) {
        lastCombination = combination;
        phaseWinner = player;
        passed = new boolean[4];

        player.removeMyCards(combination.getCards());
        phaseCards.addAll(combination.getCards());

        nextTurn();
    }

    public Rank getTopRank() {
        if (this.lastCombination == null) {
            return null;
        }
        int phoenixIndex = phaseCards.size() - 1;
        boolean hasBeforeCard = phoenixIndex > 0;
        if (lastCombination.isSinglePhoenix()) {
            // 처음에 싱글로 낸 phoenix는 mahjong과 같은 Rank로 취급
            if (!hasBeforeCard) {
                return Rank.MAHJONG;
            }
            if (phaseCards.get(phoenixIndex - 1).isMahjong()) {
                return Rank.MAHJONG;
            }
            return phaseCards.get(phoenixIndex - 1).getRank();
        }
        if (lastCombination.isMahjong()) {
            return Rank.MAHJONG;
        }
        if (lastCombination.isDragon()) {
            return Rank.DRAGON;
        }
        return lastCombination.getTopRank();
    }

    public void pass(Player player, Round round) {
        validatePass(player, round);
        passed[players.indexOf(player)] = true;
        nextTurn();
    }

    private void validatePass(Player player, Round round) {
        validateStartPlayerPass();
        validateHasCallCardPass(player, round);
    }

    private void validateStartPlayerPass() {
        if (lastCombination == null) {
            throw new IllegalArgumentException(START_PLAYER_CANNOT_PASS);
        }
    }

    private void validateHasCallCardPass(Player player, Round round) {
        if (!round.isCallActive()) {
            return;
        }
        if (player.hasStrongThanCombinationWithCall(lastCombination, round.getCalledRank())) {
            throw new IllegalArgumentException(MUST_COMBINATION_INCLUDE_CALL);
        }
    }

    public void useBomb(String name, List<Card> cards, Round round) {
        Player player = findPlayer(name);
        Combination bombCombination = CombinationEvaluator.evaluate(cards);
        Rank calledRank = round.getCalledRank();
        validateBomb(bombCombination);
        lastCombination = bombCombination;
        if (bombCombination.hasCallRank(calledRank)) {
            round.callEnd();
        }
        turnIndex = players.indexOf(player);
        phaseWinner = player;
        passed = new boolean[4];

        player.removeMyCards(bombCombination.getCards());
        phaseCards.addAll(bombCombination.getCards());

        nextTurn();
    }

    private void validateBomb(Combination bombCombination) {
        if (lastCombination == null) {
            throw new IllegalArgumentException(CANNOT_USE_BOMB);
        }
        if (!bombCombination.isBomb()) {
            throw new IllegalArgumentException(NOT_BOMB);
        }
        if (bombCombination.compareTo(lastCombination) != 1) {
            throw new IllegalArgumentException(MUST_STRONG_BOMB);
        }
    }

    public Player findPlayer(String name) {
        return players.stream()
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(PLAYER_NOT_FOUND));
    }

    public void useDog(Player player, Combination combination) {
        player.removeMyCards(combination.getCards());
        phaseCards.addAll(combination.getCards());
        phaseWinner = player;
        throw new PhaseEndSignal(USE_DOG);
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

    public void giveCardsToWinner() {
        phaseWinner.addAcquireCards(phaseCards);
    }

    public Player giveCardsUseDogPlayer() {
        phaseWinner.addAcquireCards(phaseCards);
        return findOtherTeamMember(phaseWinner);
    }

    private Player findOtherTeamMember(Player player) {
        Team team = player.getTeam();
        return players.stream()
                .filter(p -> p.getTeam() == team)
                .filter(p -> p != player)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(TEAM_MEMBER_NOT_FOUND));
    }

    public void giveCardsToPlayerWithDragon(String name) {
        Player player = findPlayer(name);
        if (player.getTeam() == phaseWinner.getTeam()) {
            throw new IllegalArgumentException(CANNOT_SELECT_SAME_TEAM);
        }
        if (player.getCardCount() == 0) {
            throw new IllegalArgumentException(CANNOT_SELECT_END_PLAYER);
        }
        player.addAcquireCards(phaseCards);
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

    public Player getCurrentPlayer() {
        return players.get(turnIndex);
    }

    public Player getPhaseWinner() {
        return phaseWinner;
    }

    public boolean winWithDragon() {
        return phaseCards.getLast().isDragon();
    }

    public Combination getLastCombination() {
        return lastCombination;
    }
}

package tichu.view;

import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

import java.util.List;
import java.util.Map;
import tichu.dto.PhaseDto;
import tichu.dto.PlayerDto;
import tichu.dto.RoundDto;
import tichu.enums.Team;

public class OutputView {
    private static final String ERROR_PREFIX = "[ERROR] ";
    private static final String ERROR_SUFFIX = " 다시 입력해 주세요.";
    private static final String PRINT_DECIDE_TEAM = "\n팀을 선정하겠습니다.";
    private static final String PRINT_RED_TEAM = "red: ";
    private static final String PRINT_BLUE_TEAM = "blue: ";
    private static final String PRINT_ARROW = " -> ";
    private static final String PRINT_ALERT_DIRECTION = "게임 진행 방향은 '%s'입니다.\n";
    private static final String PRINT_DEAL_CARD = "\n%d장의 카드가 배분되었습니다. 카드 문양은 s,d,h,c로 구분됩니다.\n";
    private static final String PRINT_CARD_TRADE_START = "\n카드 교환을 시작합니다.";
    private static final String PRINT_CARD_TRADE_END = "\n카드 교환이 완료되었습니다.";
    private static final String PRINT_FIRST_PLAYER = "\n%s님이 선으로 시작합니다.\n";
    private static final String PRINT_CURRENT_CARD_COUNT = "[현재 카드 개수]";
    private static final String PRINT_CURRENT_TABLE_CARD = "\n[테이블 카드]";
    private static final String PRINT_NONE_TABLE_CARD = "(테이블 카드 없음)\n";
    private static final String PRINT_CURRENT_CALL = "**콜: %s**\n";
    private static final String PRINT_PHASE_START = "%d 페이즈를 시작합니다.\n";
    private static final String PRINT_ROUND_START = "%d 라운드를 시작합니다.\n";
    private static final String PRINT_PHASE_END = "\n%s님이 이번 페이즈를 승리했습니다.\n";
    private static final String PRINT_CARD_COUNT_ZERO = "%s님이 모든 카드를 소진했습니다. ";
    private static final String PRINT_PLACE = "이번 라운드 %d등은 %s님입니다.\n";
    private static final String PRINT_ROUND_SCORE = "점수 계산이 완료되었습니다.\n[현재 라운드 점수]";
    private static final String PRINT_GAME_SCORE = "[총 게임 점수]";
    private static final String PRINT_WINNER = "%s팀이 승리했습니다! 티츄 게임을 종료합니다.\n";
    private static final String PRINT_DRAW = "무승부로 티츄 게임을 종료합니다.\n";

    private static final String PLAYER_CARD_LIST_FORMAT = "%s: %s\n";
    private static final String LAST_COMBINATION_FORMAT = "%s: %s %s\n";
    private static final String CARD_COUNT_FORMAT = "%s: %d장\n";
    private static final String PLAYER_TURN_FORMAT = "\n<%s> 차례\n";

    private OutputView() {
    }

    public static void printErrorMessage(String message) {
        System.out.println(ERROR_PREFIX + message + ERROR_SUFFIX);
    }

    public static void printMessage(String message) {
        System.out.println(message);
    }

    public static void printTeamAndDirection(
            List<PlayerDto> playerDtos,
            List<PlayerDto> red,
            List<PlayerDto> blue
    ) {
        System.out.println(PRINT_DECIDE_TEAM);
        System.out.print(PRINT_RED_TEAM);
        printPlayerNames(red);
        System.out.print(PRINT_BLUE_TEAM);
        printPlayerNames(blue);

        String direction = "";
        for (int i = 0; i < playerDtos.size(); i++) {
            direction = direction.concat(playerDtos.get(i).getName());
            if (i < playerDtos.size() - 1) {
                direction = direction.concat(PRINT_ARROW);
            }
        }
        direction = direction.concat(PRINT_ARROW);
        direction = direction.concat(playerDtos.get(0).getName());
        System.out.printf(PRINT_ALERT_DIRECTION, direction);
        System.out.println();
    }

    private static void printPlayerNames(List<PlayerDto> team) {
        for (int i = 0; i < team.size(); i++) {
            System.out.print(team.get(i).getName());
            if (i < team.size() - 1) {
                System.out.print(", ");
            }
        }
        System.out.println();
    }

    public static void printDealCards(int dealCount) {
        System.out.printf(PRINT_DEAL_CARD, dealCount);
    }

    public static void printCards(List<String> cards) {
        System.out.println(cards);
    }

    public static void printTradeCards() {
        System.out.println(PRINT_CARD_TRADE_START);
    }

    public static void printTradeEnd() {
        System.out.println(PRINT_CARD_TRADE_END);
    }

    public static void printFirstPlayer(PlayerDto playerDto) {
        System.out.printf(PRINT_FIRST_PLAYER, playerDto.getStatus());
        System.out.println();
    }

    public static void printTurnPlayer(PlayerDto playerDto) {
        System.out.printf(PLAYER_TURN_FORMAT, playerDto.getStatus());
    }

    public static void printAllCardCount(List<PlayerDto> playerDtos) {
        System.out.println(PRINT_CURRENT_CARD_COUNT);
        for (PlayerDto playerDto : playerDtos) {
            System.out.printf(CARD_COUNT_FORMAT, playerDto.getStatus(), playerDto.getCardCount());
        }
    }

    public static void printPlayerCards(PlayerDto playerDto) {
        System.out.printf(PLAYER_TURN_FORMAT, playerDto.getStatus());
        System.out.println(playerDto.getMyCards());
        System.out.println();
    }

    public static void printPlayerAllCards(PlayerDto playerDto) {
        System.out.printf(PLAYER_CARD_LIST_FORMAT, playerDto.getStatus(), playerDto.getMyCards());
    }

    public static void printTableCombination(RoundDto roundDto, PhaseDto phaseDto, List<String> cards) {
        System.out.println(PRINT_CURRENT_TABLE_CARD);
        if (roundDto.isCallActive()) {
            System.out.printf(PRINT_CURRENT_CALL, roundDto.getCalledRank());
        }
        if (phaseDto.getLastCombinationType() == null) {
            System.out.println(PRINT_NONE_TABLE_CARD);
            return;
        }
        System.out.printf(LAST_COMBINATION_FORMAT,
                phaseDto.getCurrentWinner().getStatus(),
                phaseDto.getLastCombinationTopRank(),
                phaseDto.getLastCombinationType());
        printCards(cards);
        System.out.println();
    }

    public static void printTableCombination(RoundDto roundDto) {
        System.out.println(PRINT_CURRENT_TABLE_CARD);
        if (roundDto.isCallActive()) {
            System.out.printf(PRINT_CURRENT_CALL, roundDto.getCalledRank());
        }
        System.out.println(PRINT_NONE_TABLE_CARD);
    }

    public static void printPhaseStart(int currentPhase) {
        System.out.printf(PRINT_PHASE_START, currentPhase);
    }

    public static void printPhaseEnd(PlayerDto playerDto) {
        System.out.printf(PRINT_PHASE_END, playerDto.getStatus());
    }

    public static void printRoundStart(int currentRound) {
        System.out.printf(PRINT_ROUND_START, currentRound);
    }

    public static void printRoundEnd(PlayerDto playerDto, int place) {
        if (place != 4) {
            System.out.printf(PRINT_CARD_COUNT_ZERO, playerDto.getStatus());
        }
        System.out.printf(PRINT_PLACE, place, playerDto.getStatus());
    }

    public static void printRoundScore(Map<Team, Integer> currentRoundScore) {
        System.out.println(PRINT_ROUND_SCORE);
        System.out.println(PRINT_RED_TEAM + currentRoundScore.get(RED));
        System.out.println(PRINT_BLUE_TEAM + currentRoundScore.get(BLUE));
        System.out.println();
    }

    public static void printTeamScore(Map<Team, Integer> currentTeamScore) {
        System.out.println(PRINT_GAME_SCORE);
        System.out.println(PRINT_RED_TEAM + currentTeamScore.get(RED));
        System.out.println(PRINT_BLUE_TEAM + currentTeamScore.get(BLUE));
        System.out.println();
    }

    public static void printWinner(Team team) {
        if (team == null) {
            System.out.println(PRINT_DRAW);
            return;
        }
        System.out.printf(PRINT_WINNER, team);
    }
}

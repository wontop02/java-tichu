package tichu.view;

import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

import java.util.List;
import java.util.Map;
import tichu.dto.PhaseDto;
import tichu.dto.PlayerDto;
import tichu.enums.Team;

public class OutputView {
    private static final String ERROR_PREFIX = "[ERROR] ";
    private static final String ERROR_SUFFIX = " 다시 입력해 주세요.";
    private static final String PRINT_DECIDE_TEAM = "팀을 선정하겠습니다.\n";
    private static final String PRINT_RED_TEAM = "red: ";
    private static final String PRINT_BLUE_TEAM = "blue: ";
    private static final String PRINT_ARROW = " -> ";
    private static final String PRINT_ALERT_DIRECTION = "게임 진행 방향은 '%s'입니다.\n";
    private static final String PRINT_DEAL_CARD = "%d장의 카드가 배분되었습니다. 카드 문양은 s,d,h,c로 구분됩니다.\n";
    private static final String PRINT_CARD_TRADE_START = "카드 교환을 시작합니다.";
    private static final String PRINT_FIRST_PLAYER = "%s님이 선으로 시작합니다.";
    private static final String PRINT_CURRENT_CARD_COUNT = "[현재 카드 개수]";
    private static final String PRINT_CURRENT_TABLE_COUNT = "[테이블 카드]";
    private static final String PRINT_CURRENT_CALL = "**콜: %s**\n";
    private static final String PRINT_PHASE_START = "%d 페이즈를 시작합니다.";
    private static final String PRINT_ROUND_START = "%d 라운드를 시작합니다.";
    private static final String PRINT_PHASE_END = "%s님이 이번 페이즈를 승리했습니다.\n";
    private static final String PRINT_CARD_COUNT_ZERO = "%s님이 모든 카드를 소진했습니다. ";
    private static final String PRINT_PLACE = "이번 라운드 %d등은 %s님입니다.\n";
    private static final String PRINT_SCORE = "점수 계산이 완료되었습니다.\n[현재 점수]";

    private static final String LAST_COMBINATION_FORMAT = "%s: %s %s";
    private static final String CARD_COUNT_FORMAT = "%s: %d장\n";
    private static final String PLAYER_TURN_FORMAT = "<%s> 차례\n";
    private static final String PLAYER_STATUS_FORMAT = "%s(%s%s)";
    private static final String RED_TEAM = "red";
    private static final String BLUE_TEAM = "blue";
    private static final String LARGE_TICHU = "-L";
    private static final String SMALL_TICHU = "-S";
    private static final String NONE_TICHU = "";

    private OutputView() {
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

    public static void printDealCards(List<PlayerDto> playerDtos, int dealCount) {
        System.out.printf(PRINT_DEAL_CARD, dealCount);
        for (PlayerDto playerDto : playerDtos) {
            printCards(playerDto.getMyCards());
        }
        System.out.println();
    }

    public static void printCards(List<String> cards) {
        System.out.println(cards);
    }

    public static void printTradeCards() {
        System.out.println(PRINT_CARD_TRADE_START);
    }

    public static void printErrorMessage(String message) {
        System.out.println(ERROR_PREFIX + message + ERROR_SUFFIX);
    }

    public static void printFirstPlayer(PlayerDto playerDto) {
        System.out.printf(PRINT_FIRST_PLAYER, playerStatus(playerDto));
        System.out.println();
    }

    public static void printTurnPlayer(PlayerDto playerDto) {
        System.out.printf(PLAYER_TURN_FORMAT, playerStatus(playerDto));
    }

    public static void printAllCardCount(List<PlayerDto> playerDtos) {
        System.out.println(PRINT_CURRENT_CARD_COUNT);
        for (PlayerDto playerDto : playerDtos) {
            System.out.printf(CARD_COUNT_FORMAT, playerStatus(playerDto), playerDto.getCardCount());
        }
        System.out.println();
    }

    public static void printTurn(PlayerDto playerDto) {
        System.out.printf(PLAYER_TURN_FORMAT, playerStatus(playerDto));
        System.out.println(playerDto.getMyCards());
    }

    public static void printTableCombination(PhaseDto phaseDto, List<String> cards) {
        System.out.println(PRINT_CURRENT_TABLE_COUNT);
        if (phaseDto.isCallActive()) {
            System.out.printf(PRINT_CURRENT_CALL, phaseDto.getCalledRank());
        }
        System.out.printf(LAST_COMBINATION_FORMAT,
                playerStatus(phaseDto.getCurrentWinner()),
                phaseDto.getLastCombinationTopRank(),
                phaseDto.getLastCombinationType());
        printCards(cards);
        System.out.println();
    }

    public static void printPhaseStart(int currentPhase) {
        System.out.printf(PRINT_PHASE_START, int currentPhase);
    }

    public static void printPhaseEnd(PlayerDto playerDto) {
        System.out.printf(PRINT_PHASE_END, playerStatus(playerDto));
    }

    public static void printRoundStart(int currentRound) {
        System.out.printf(PRINT_ROUND_START, int currentRound);
    }

    public static void printRoundEnd(PlayerDto playerDto, int place) {
        if (place != 4) {
            System.out.printf(PRINT_CARD_COUNT_ZERO, playerStatus(playerDto));
        }
        System.out.printf(PRINT_PLACE, place, playerStatus(playerDto));
        System.out.println();
    }

    public static void printTeamScore(Map<Team, Integer> currentTeamScore) {
        System.out.println(PRINT_SCORE);
        System.out.println(PRINT_RED_TEAM + currentTeamScore.get(RED));
        System.out.println(PRINT_BLUE_TEAM + currentTeamScore.get(BLUE));
        System.out.println();
    }

    private static String playerStatus(PlayerDto playerDto) {
        String team = "";
        if (playerDto.getTeam() == RED) {
            team = RED_TEAM;
        }
        if (playerDto.getTeam() == BLUE) {
            team = BLUE_TEAM;
        }
        String tichuStatus = "";
        if (playerDto.isLargeTichu()) {
            tichuStatus = LARGE_TICHU;
        }
        if (playerDto.isSmallTichu()) {
            tichuStatus = SMALL_TICHU;
        }
        if (!playerDto.isSmallTichu() && !playerDto.isLargeTichu()) {
            tichuStatus = NONE_TICHU;
        }
        return String.format(PLAYER_STATUS_FORMAT, playerDto.getName(), team, tichuStatus);
    }
}

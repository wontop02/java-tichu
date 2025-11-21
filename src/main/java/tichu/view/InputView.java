package tichu.view;

import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

import java.util.Scanner;
import tichu.dto.PlayerDto;

public class InputView {
    private static final String REQUEST_PLAYERS_NAME = "참가자 4명의 이름을 입력해 주세요(쉼표로 구분): ";
    private static final String REQUEST_TICHU_CALLER_NAME = "%s 티츄를 부를 참가자 이름을 입력해 주세요(쉼표로 구분, 없으면 x): ";
    private static final String REQUEST_TRADE_CARD = "%s님에게 줄 카드를 입력해 주세요: ";
    private static final String REQUEST_COMBINATION = "[%s님 차례] 낼 조합을 선택해 주세요(쉼표로 구분, 패스는 p): ";
    private static final String REQUEST_CALL_RANK = "콜을 부를 숫자 또는 알파벳을 입력해 주세요(문양 제외 입력, 없으면 x): ";
    private static final String REQUEST_RECEIVE_PLAYER_NAME = "용으로 획득한 카드를 받을 상대 팀 플레이어 이름을 입력해 주세요: ";
    private static final String REQUEST_BOMB_PLAYER_NAME = "폭탄을 낼 참가자 이름을 입력해 주세요(없으면 x): ";
    private static final String REQUEST_BOMB_COMBINATION = "폭탄을 입력해 주세요(취소는 x): ";

    private static final String PLAYER_STATUS_FORMAT = "%s(%s%s)";
    private static final String RED_TEAM = "red";
    private static final String BLUE_TEAM = "blue";
    private static final String LARGE_TICHU = "-L";
    private static final String SMALL_TICHU = "-S";
    private static final String NONE_TICHU = "";

    private static final Scanner scanner = new Scanner(System.in);

    private InputView() {
    }

    public static String requestPlayerNames() {
        System.out.printf(REQUEST_PLAYERS_NAME);
        return scanner.nextLine();
    }

    public static String requestTichuCallerName(String largeOrSmall) {
        System.out.printf(REQUEST_TICHU_CALLER_NAME, largeOrSmall);
        return scanner.nextLine();
    }

    public static String requestTradeCard(PlayerDto toPlayerDto) {
        System.out.printf(REQUEST_TRADE_CARD, playerStatus(toPlayerDto));
        return scanner.nextLine();
    }

    public static String requestCombination(PlayerDto playerDto) {
        System.out.printf(REQUEST_COMBINATION, playerStatus(playerDto));
        return scanner.nextLine();
    }

    public static String requestBombPlayer() {
        System.out.printf(REQUEST_BOMB_PLAYER_NAME);
        return scanner.nextLine();
    }

    public static String requestBomb() {
        System.out.printf(REQUEST_BOMB_COMBINATION);
        return scanner.nextLine();
    }

    public static String requestCallRank() {
        System.out.printf(REQUEST_CALL_RANK);
        return scanner.nextLine();
    }

    public static String requestReceivePlayerName() {
        System.out.printf(REQUEST_RECEIVE_PLAYER_NAME);
        return scanner.nextLine();
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

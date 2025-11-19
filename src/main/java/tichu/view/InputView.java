package tichu.view;

import java.util.Scanner;

public class InputView {
    private static final String REQUEST_PLAYERS_NAME = "참가자 4명의 이름을 입력해 주세요(쉼표로 구분): ";
    private static final String REQUEST_TICHU_CALLER_NAME = "%s 티츄를 부를 참가자 이름을 입력해 주세요(쉼표로 구분, 없으면 x): ";
    private static final String REQUEST_TRADE_CARD = "%s님에게 줄 카드를 입력해 주세요: ";
    private static final String REQUEST_COMBINATION = "[%s님 차례] 낼 조합을 선택해 주세요(쉼표로 구분, 패스는 p): ";
    private static final String REQUEST_CALL_RANK = "콜을 부를 숫자 또는 알파벳을 입력해 주세요(문양 제외 입력, 없으면 x): ";

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

    public static String requestTradeCard(String toPlayerName) {
        System.out.printf(REQUEST_TRADE_CARD, toPlayerName);
        return scanner.nextLine();
    }

    public static String requestCombination(String playerName) {
        System.out.printf(REQUEST_COMBINATION, playerName);
        return scanner.nextLine();
    }

    public static String requestCallRank() {
        System.out.printf(REQUEST_CALL_RANK);
        return scanner.nextLine();
    }
}

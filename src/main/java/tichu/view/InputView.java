package tichu.view;

import java.util.Scanner;

public class InputView {
    private static final String REQUEST_PLAYERS_NAME = "참가자 4명의 이름을 입력해 주세요(쉼표로 구분): ";
    private static final String REQUEST_TICHU_CALLER_NAME = "%s 티츄를 부를 참가자 이름을 입력해 주세요(쉼표로 구분): ";
    private static final String REQUEST_TRADE_CARD = "%s에게 줄 카드를 입력해 주세요: ";

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
}

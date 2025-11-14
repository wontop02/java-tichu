package tichu.view;

import java.util.Scanner;

public class InputView {
    private static final String REQUEST_PLAYERS_NAME = "참가자 4명의 이름을 입력해 주세요(쉼표로 구분): ";
    private static final String REQUEST_TICHU_CALLER_NAME = "티츄를 부를 참가자 이름을 입력해 주세요(쉼표로 구분): ";
    private static final Scanner scanner = new Scanner(System.in);

    private InputView() {
    }

    public static String requestPlayerNames() {
        System.out.println(REQUEST_PLAYERS_NAME);
        return scanner.nextLine();
    }

    public static String requestTichuCallerName(String largeOrSmall) {
        System.out.println(largeOrSmall + REQUEST_TICHU_CALLER_NAME);
        return scanner.nextLine();
    }
}

package tichu.view;

import java.util.List;

public class OutputView {
    public static final String ERROR_PREFIX = "[ERROR] ";
    public static final String ERROR_SUFFIX = " 다시 입력해 주세요.";

    private OutputView() {
    }

    public static void printCards(List<String> cards) {
        System.out.println(cards);
    }

    public static void printErrorMessage(String message) {
        System.out.println(ERROR_PREFIX + message + ERROR_SUFFIX);
    }
}

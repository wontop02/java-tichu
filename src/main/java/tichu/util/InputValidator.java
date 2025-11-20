package tichu.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class InputValidator {
    private static final String BLANK_INPUT = "빈 문자열 또는 공백은 입력할 수 없습니다.";
    private static final String NOT_ENGLISH_AND_KOREAN_ONLY = "한글(자음 모음 조합)과 영어만 입력이 가능합니다.";
    private static final String DUPLICATED = "중복된 입력은 허용하지 않습니다.";
    private static final String INVALID_CARD_FORMAT = "카드 입력 형식(숫자/알파벳+문양[s,d,h,c])이 올바르지 않습니다.";

    private static final String ENGLISH_AND_KOREAN_ONLY_REGEX = "^[가-힣a-zA-Z]+$";
    private static final String NORMAL_CARD_FORMAT = "^(2|3|4|5|6|7|8|9|10|j|J|q|Q|k|K|a|A)[sdhcSDHC]$";
    private static final String SPECIAL_CARD_FORMAT = "^(1|개|봉|용)$";
    private static final String CALL_RANK_FORMAT = "^(2|3|4|5|6|7|8|9|10|j|J|q|Q|k|K|a|A)$";

    private InputValidator() {
    }

    public static void validatePlayerNames(String input) {
        validateNotBlank(input);
        List<String> inputs = validateNotDuplicate(input);
        inputs.forEach(InputValidator::validateEnglishAndKoreanOnly);
    }

    public static void validateTichuCallerName(String input) {
        validateNotBlank(input);
        List<String> inputs = validateNotDuplicate(input);
        inputs.forEach(InputValidator::validateNotBlank);
    }

    public static void validateReceivePlayerName(String input) {
        validateNotBlank(input);
    }

    public static void validateCardInput(String input) {
        validateNotBlank(input);
        List<String> inputs = validateNotDuplicate(input);
        inputs.forEach(InputValidator::validateCardFormat);
    }

    public static void validateCallRank(String input) {
        validateNotBlank(input);
        if (!input.matches(CALL_RANK_FORMAT)) {
            throw new IllegalArgumentException(INVALID_CARD_FORMAT);
        }
    }

    private static void validateNotBlank(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(BLANK_INPUT);
        }
    }

    private static List<String> validateNotDuplicate(String input) {
        List<String> inputs = Arrays.asList(input.split(",", -1));
        if (new HashSet<>(inputs).size() != inputs.size()) {
            throw new IllegalArgumentException(DUPLICATED);
        }
        return inputs;
    }

    private static void validateEnglishAndKoreanOnly(String input) {
        if (!input.matches(ENGLISH_AND_KOREAN_ONLY_REGEX)) {
            throw new IllegalArgumentException(NOT_ENGLISH_AND_KOREAN_ONLY);
        }
    }

    private static void validateCardFormat(String input) {
        boolean isNormalCard = input.matches(NORMAL_CARD_FORMAT);
        boolean isSpecialCard = input.matches(SPECIAL_CARD_FORMAT);
        if (!isNormalCard && !isSpecialCard) {
            throw new IllegalArgumentException(INVALID_CARD_FORMAT);
        }
    }
}

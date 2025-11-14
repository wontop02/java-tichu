package tichu.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class InputValidator {
    private static final String BLANK_INPUT = "빈 문자열 또는 공백은 입력할 수 없습니다.";
    private static final String NOT_ENGLISH_AND_KOREAN_ONLY = "한글(자음 모음 조합)과 영어만 입력이 가능합니다.";
    private static final String DUPLICATED = "중복된 입력은 허용하지 않습니다.";
    private static final String ENGLISH_AND_KOREAN_ONLY_REGEX = "^[가-힣a-zA-Z]*$";

    private InputValidator() {
    }

    public static List<String> validatePlayerNames(String input) {
        validateNotBlank(input);
        List<String> inputs = validateNotDuplicate(input);
        inputs.forEach(InputValidator::validateEnglishAndKoreanOnly);
        return inputs;
    }

    public static List<String> validateTichuCallerName(String input) {
        validateNotBlank(input);
        return validateNotDuplicate(input);
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
}

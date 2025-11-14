package tichu.util;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class InputValidatorTest {

    @DisplayName("한글(자음 모음 조합), 영어를 제외한 문자가 있으면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"ㅁ", "1", "h i", "!"})
    void 한글과_영어를_제외한_문자가_있으면_예외가_발생한다(String input) {
        assertThatThrownBy(() -> InputValidator.validatePlayerNames(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("한글(자음 모음 조합)과 영어만 입력이 가능합니다.");
    }

    @DisplayName("중복된 입력이 존재하면 예외가 발생한다.")
    @Test
    void 중복된_입력이_존재하면_예외가_발생한다() {
        assertThatThrownBy(() -> InputValidator.validatePlayerNames("철수,영희,철수"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("중복된 입력은 허용하지 않습니다.");
    }
}

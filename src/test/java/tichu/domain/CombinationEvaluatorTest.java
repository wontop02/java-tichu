package tichu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tichu.enums.CombinationType;
import tichu.enums.Rank;
import tichu.util.InputValidator;

public class CombinationEvaluatorTest {
    @DisplayName("일치하는 조합을 찾을 수 없으면 예외가 발생한다.")
    @Test
    void 일치하는_조합을_찾을_수_없으면_예외가_발생한다() {
        String input = "1,2s,3s,4s,6d";
        InputValidator.validateCardInput(input);
        List<Card> cards = CardParser.fromStringToCardList(input);

        assertThatThrownBy(() -> CombinationEvaluator.evaluate(cards))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 조합입니다.");
    }

    @DisplayName("봉이 포함된 카드 리스트에서 스트레이트를 찾는다.")
    @Test
    void 봉이_포함된_카드_리스트에서_스트레이트를_찾는다() {
        String input = "봉,4s,6s,7s,8s";
        InputValidator.validateCardInput(input);
        List<Card> cards = CardParser.fromStringToCardList(input);

        Combination result = CombinationEvaluator.evaluate(cards);

        assertEquals(CombinationType.STRAIGHT, result.getCombinationType());
    }

    @DisplayName("봉이 포함된 카드 리스트에서 풀하우스를 찾는다.")
    @Test
    void 봉이_포함된_카드_리스트에서_풀하우스를_찾는다() {
        String input = "봉,2s,2d,As,Ad";
        InputValidator.validateCardInput(input);
        List<Card> cards = CardParser.fromStringToCardList(input);

        Combination result = CombinationEvaluator.evaluate(cards);

        assertEquals(CombinationType.FULL_HOUSE, result.getCombinationType());
        assertEquals(Rank.ACE, result.getTopRank());
    }

    @DisplayName("스트레이트에서 봉이 가장 큰 숫자를 대체한 경우 대체된 등급을 저장한다.")
    @Test
    void 스트레이트에서_봉이_가장_큰_숫자를_대체한_경우_대체된_등급을_저장한다() {
        String input = "2s,3s,4s,5s,6s,7s,봉";
        InputValidator.validateCardInput(input);
        List<Card> cards = CardParser.fromStringToCardList(input);

        Combination result = CombinationEvaluator.evaluate(cards);

        assertEquals(Rank.EIGHT, result.getTopRank());
    }
}

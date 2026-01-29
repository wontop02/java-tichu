package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tichu.util.InputValidator;

public class CombinationTest {
    @DisplayName("조합을 비교해 더 강한 조합을 찾아낸다.")
    @Test
    void 조합을_비교해_더_강한_조합을_찾아낸다() {
        String input = "2s,2d,3s,3d,4s,4d,5s,5d";
        String input2 = "3s,3d,4s,4d,5s,5d,6s,봉";
        InputValidator.validateCardInput(input);
        InputValidator.validateCardInput(input2);
        List<Card> cards = CardParser.fromStringToCardList(input);
        List<Card> cards2 = CardParser.fromStringToCardList(input2);

        Combination combination = CombinationEvaluator.evaluate(cards);
        Combination combination2 = CombinationEvaluator.evaluate(cards2);

        int result = combination.compareTo(combination2);
        assertEquals(-1, result);
    }
}

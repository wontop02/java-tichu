package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CardParserTest {
    @DisplayName("플레이어가 조합을 순서에 맞지 않게 입력해도 정렬된 상태로 반환한다.")
    @Test
    void 플레이어가_조합을_순서에_맞지_않게_입력해도_정렬된_상태로_반환한다() {
        String input = "5s,8s,3s,4s,7s,6s";
        List<Card> cards = CardParser.fromStringToCardList(input);
        Combination combination = CombinationEvaluator.evaluate(cards);

        List<String> printCards = CardParser.fromCardsToStringList(combination.cards());
        List<String> assertCards = List.of("3s", "4s", "5s", "6s", "7s", "8s");
        assertEquals(assertCards, printCards);
    }
}

package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tichu.enums.Rank;
import tichu.enums.Suit;
import tichu.enums.Team;

class PlayerTest {
    @DisplayName("카드를 순서에 맞게 정렬한다.")
    @Test
    void 카드를_순서에_맞게_정렬한다() {
        Player player = new Player("주영", Team.RED);

        List<Card> sortedCards = List.of(
                new Card(Rank.DOG, Suit.NONE),
                new Card(Rank.MAHJONG, Suit.NONE),
                new Card(Rank.TWO, Suit.SPADE),
                new Card(Rank.SEVEN, Suit.SPADE),
                new Card(Rank.SEVEN, Suit.DIAMOND),
                new Card(Rank.SEVEN, Suit.HEART),
                new Card(Rank.SEVEN, Suit.CLUB),
                new Card(Rank.ACE, Suit.SPADE),
                new Card(Rank.PHOENIX, Suit.NONE),
                new Card(Rank.DRAGON, Suit.NONE)
        );

        List<Card> shuffledCards = new ArrayList<>(sortedCards);
        Collections.shuffle(shuffledCards);

        player.addMyCards(shuffledCards);

        assertEquals(sortedCards, player.getMyCards());
    }

    @DisplayName("카드 점수를 정확히 계산한다.")
    @Test
    void 카드_점수를_정확히_계산한다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(List.of(
                new Card(Rank.FIVE, Suit.SPADE), new Card(Rank.TEN, Suit.SPADE),
                new Card(Rank.KING, Suit.SPADE), new Card(Rank.DRAGON, Suit.NONE), new Card(Rank.PHOENIX, Suit.NONE)
        ));
        assertEquals(25, player.calculateMyCardScore());
    }

    @DisplayName("봉이 포함된 조합을 낸 후 손패에서 카드를 삭제한다.")
    @Test
    void 봉이_포함된_조합을_낸_후_손패에서_카드를_삭제한다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(List.of(
                new Card(Rank.TWO, Suit.SPADE), new Card(Rank.THREE, Suit.SPADE),
                new Card(Rank.FOUR, Suit.SPADE), new Card(Rank.FIVE, Suit.SPADE), new Card(Rank.PHOENIX, Suit.NONE)
        ));
        Combination combination = CombinationEvaluator.evaluate(player.getMyCards());
        player.removeMyCards(combination.cards());
        assertEquals(0, player.getMyCards().size());
    }
}

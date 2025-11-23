package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tichu.enums.Rank.FIVE;
import static tichu.enums.Rank.KING;
import static tichu.enums.Rank.TEN;
import static tichu.enums.Special.DRAGON;
import static tichu.enums.Special.PHOENIX;
import static tichu.enums.Suit.SPADE;
import static tichu.enums.Team.RED;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tichu.enums.Rank;
import tichu.enums.Special;
import tichu.enums.Suit;

class PlayerTest {
    @DisplayName("카드를 순서에 맞게 정렬한다.")
    @Test
    void 카드를_순서에_맞게_정렬한다() {
        Player player = new Player("주영", RED);

        List<Card> sortedCards = List.of(
                new Card(Special.DOG),
                new Card(Special.MAHJONG),
                new Card(Rank.TWO, SPADE),
                new Card(Rank.SEVEN, SPADE),
                new Card(Rank.SEVEN, Suit.DIAMOND),
                new Card(Rank.SEVEN, Suit.HEART),
                new Card(Rank.SEVEN, Suit.CLUB),
                new Card(Rank.ACE, SPADE),
                new Card(Special.PHOENIX),
                new Card(DRAGON)
        );

        List<Card> shuffledCards = new ArrayList<>(sortedCards);
        Collections.shuffle(shuffledCards);

        player.addMyCards(shuffledCards);

        assertEquals(sortedCards, player.getMyCards());
    }

    @DisplayName("카드 점수를 정확히 계산한다")
    @Test
    void 카드_점수를_정확히_계산한다() {
        Player player = new Player("주영", RED);
        player.addMyCards(List.of(
                new Card(FIVE, SPADE), new Card(TEN, SPADE),
                new Card(KING, SPADE), new Card(DRAGON), new Card(PHOENIX)
        ));
        assertEquals(25, player.calculateMyCardScore());
    }
}

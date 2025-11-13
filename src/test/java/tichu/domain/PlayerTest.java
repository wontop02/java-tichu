package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        Player player = new Player("주영");

        List<Card> sortedCards = List.of(
                new Card(Special.DOG),
                new Card(Special.MAHJONG),
                new Card(Rank.TWO, Suit.SPADE),
                new Card(Rank.SEVEN, Suit.SPADE),
                new Card(Rank.SEVEN, Suit.DIAMOND),
                new Card(Rank.SEVEN, Suit.HEART),
                new Card(Rank.SEVEN, Suit.CLUB),
                new Card(Rank.ACE, Suit.SPADE),
                new Card(Special.PHOENIX),
                new Card(Special.DRAGON)
        );

        List<Card> shuffledCards = new ArrayList<>(sortedCards);
        Collections.shuffle(shuffledCards);

        player.addMyCards(shuffledCards);
        player.sortMyCards();

        assertEquals(sortedCards, player.getMyCards());
    }
}

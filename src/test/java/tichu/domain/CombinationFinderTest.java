package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tichu.enums.CombinationType;
import tichu.enums.Rank;
import tichu.enums.Suit;
import tichu.enums.Team;

public class CombinationFinderTest {
    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 싱글을 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_싱글을_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(List.of(new Card(Rank.TWO, Suit.SPADE)));
        Rank callRank = Rank.TWO;
        Combination combination = new Combination(List.of(new Card(Rank.MAHJONG, Suit.NONE)),
                CombinationType.SINGLE, new Card(Rank.MAHJONG, Suit.NONE));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 페어를 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_페어를_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(List.of(new Card(Rank.THREE, Suit.SPADE), new Card(Rank.THREE, Suit.CLUB)));
        Rank callRank = Rank.THREE;
        Combination combination = new Combination(
                List.of(new Card(Rank.TWO, Suit.SPADE), new Card(Rank.TWO, Suit.CLUB)),
                CombinationType.PAIR, new Card(Rank.TWO, Suit.CLUB));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 트리플을 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_트리플을_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(
                List.of(new Card(Rank.THREE, Suit.SPADE), new Card(Rank.THREE, Suit.CLUB),
                        new Card(Rank.THREE, Suit.HEART)));
        Rank callRank = Rank.THREE;
        Combination combination = new Combination(
                List.of(new Card(Rank.TWO, Suit.SPADE), new Card(Rank.TWO, Suit.CLUB), new Card(Rank.TWO, Suit.HEART)),
                CombinationType.TRIPLE, new Card(Rank.TWO, Suit.CLUB));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 풀하우스를 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_풀하우스를_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(
                List.of(new Card(Rank.THREE, Suit.SPADE), new Card(Rank.THREE, Suit.CLUB),
                        new Card(Rank.THREE, Suit.HEART), new Card(Rank.EIGHT, Suit.SPADE),
                        new Card(Rank.EIGHT, Suit.CLUB)));
        Rank callRank = Rank.THREE;
        Combination combination = new Combination(
                List.of(new Card(Rank.TWO, Suit.SPADE), new Card(Rank.TWO, Suit.CLUB), new Card(Rank.TWO, Suit.HEART),
                        new Card(Rank.SEVEN, Suit.SPADE), new Card(Rank.SEVEN, Suit.CLUB)),
                CombinationType.FULL_HOUSE, new Card(Rank.TWO, Suit.CLUB));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 연페어를 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_연페어를_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(
                List.of(new Card(Rank.FOUR, Suit.SPADE), new Card(Rank.FOUR, Suit.CLUB),
                        new Card(Rank.FIVE, Suit.HEART), new Card(Rank.FIVE, Suit.CLUB)));
        Rank callRank = Rank.FIVE;
        Combination combination = new Combination(
                List.of(new Card(Rank.TWO, Suit.CLUB), new Card(Rank.TWO, Suit.SPADE),
                        new Card(Rank.THREE, Suit.CLUB), new Card(Rank.THREE, Suit.SPADE)),
                CombinationType.PAIR_SEQUENCE, new Card(Rank.THREE, Suit.SPADE));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 스트레이트를 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_스트레이트를_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(
                List.of(new Card(Rank.TWO, Suit.SPADE), new Card(Rank.THREE, Suit.CLUB),
                        new Card(Rank.FOUR, Suit.HEART), new Card(Rank.FIVE, Suit.CLUB),
                        new Card(Rank.SIX, Suit.SPADE)));
        Rank callRank = Rank.TWO;
        Combination combination = new Combination(
                List.of(new Card(Rank.MAHJONG, Suit.NONE), new Card(Rank.TWO, Suit.SPADE),
                        new Card(Rank.THREE, Suit.CLUB), new Card(Rank.FOUR, Suit.HEART),
                        new Card(Rank.FIVE, Suit.CLUB)),
                CombinationType.STRAIGHT, new Card(Rank.FIVE, Suit.CLUB));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 포카드를 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_포카드를_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(
                List.of(new Card(Rank.SEVEN, Suit.SPADE), new Card(Rank.SEVEN, Suit.CLUB),
                        new Card(Rank.SEVEN, Suit.HEART), new Card(Rank.SEVEN, Suit.DIAMOND)));
        Rank callRank = Rank.SEVEN;
        Combination combination = new Combination(
                List.of(new Card(Rank.MAHJONG, Suit.NONE), new Card(Rank.TWO, Suit.SPADE),
                        new Card(Rank.THREE, Suit.CLUB), new Card(Rank.FOUR, Suit.HEART),
                        new Card(Rank.FIVE, Suit.CLUB)),
                CombinationType.STRAIGHT, new Card(Rank.FIVE, Suit.CLUB));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }

    @DisplayName("플레이어 카드 리스트에서 콜이 포함된 스트레이트 플러쉬를 찾는다.")
    @Test
    void 플레이어_카드_리스트에서_콜이_포함된_스트레이트_플러쉬를_찾는다() {
        Player player = new Player("주영", Team.RED);
        player.addMyCards(
                List.of(new Card(Rank.TWO, Suit.SPADE), new Card(Rank.THREE, Suit.SPADE),
                        new Card(Rank.FOUR, Suit.SPADE), new Card(Rank.FIVE, Suit.SPADE),
                        new Card(Rank.SIX, Suit.SPADE)));
        Rank callRank = Rank.TWO;
        Combination combination = new Combination(
                List.of(new Card(Rank.MAHJONG, Suit.NONE), new Card(Rank.TWO, Suit.CLUB),
                        new Card(Rank.THREE, Suit.CLUB), new Card(Rank.FOUR, Suit.HEART),
                        new Card(Rank.FIVE, Suit.CLUB)),
                CombinationType.STRAIGHT, new Card(Rank.FIVE, Suit.CLUB));

        assertTrue(CombinationFinder.hasStrongCombinationWithCall(player.getMyCards(), callRank, combination));
    }
}

package tichu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoundTest {
    @DisplayName("카드가 중복 없이 나누어진다.")
    @Test
    void 카드가_중복_없이_나누어진다() {
        Round round = new Round();
        round.settingCards(new Deck());
        List<Player> players = List.of(
                new Player("하나"), new Player("둘"),
                new Player("셋"), new Player("넷"));
        players.forEach(player -> player.addMyCards(round.deal(8)));
        players.forEach(player -> player.addMyCards(round.deal(6)));

        List<Card> cards = new ArrayList<>();
        cards.addAll(players.get(0).getMyCards());
        cards.addAll(players.get(1).getMyCards());
        cards.addAll(players.get(2).getMyCards());
        cards.addAll(players.get(3).getMyCards());

        Set<Card> distinctCards = new HashSet<>(cards);
        assertEquals(56, distinctCards.size());
    }

    @DisplayName("이미 티츄를 불렀는데 또 스몰티츄를 부를 경우 예외가 발생한다")
    @Test
    void 이미_티츄를_불렀는데_또_스몰티츄를_부를_경우_예외가_발생한다() {
        Round round = new Round();
        Player player = new Player("주영");

        round.addSmallTichu(player);

        assertThatThrownBy(() -> round.addSmallTichu(player))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 티츄를 부른 플레이어입니다: " + player.getName());
    }
}

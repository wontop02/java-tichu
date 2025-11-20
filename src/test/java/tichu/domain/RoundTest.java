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
        List<String> names = List.of("영희", "주영", "민지", "철수");
        TichuGame game = new TichuGame(names);
        Round round = game.startRound();
        List<Player> players = round.getPlayers();

        round.dealCards8();
        round.dealCards6();

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
        List<String> names = List.of("영희", "주영", "민지", "철수");
        TichuGame game = new TichuGame(names);
        Round round = game.startRound();

        List<String> tichuPlayer = List.of("주영");
        round.addSmallTichu(tichuPlayer);

        assertThatThrownBy(() -> round.addSmallTichu(tichuPlayer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 티츄를 부른 플레이어가 존재합니다.");
    }

    @DisplayName("입력된 이름의_플레이어가 존재하지 않으면 예외가 발생한다.")
    @Test
    void 입력된_이름의_플레이어가_존재하지_않으면_예외가_발생한다() {
        List<String> names = List.of("영희", "주영", "민지", "철수");
        List<String> name = List.of("짱구");

        TichuGame tichuGame = new TichuGame(names);
        Round round = tichuGame.startRound();

        assertThatThrownBy(() -> round.validatePlayerNames(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 플레이어 이름이 존재합니다.");
    }
}

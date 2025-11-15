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
        List<Player> players = List.of(
                new Player("하나"), new Player("둘"),
                new Player("셋"), new Player("넷"));
        TichuGame game = new TichuGame(players);
        Round round = game.startRound();

        round.settingRound();
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
        List<String> names = List.of("하나");
        List<Player> players = List.of(
                new Player("하나"), new Player("둘"),
                new Player("셋"), new Player("넷"));
        TichuGame game = new TichuGame(players);
        Round round = game.startRound();

        round.addSmallTichu(names);

        assertThatThrownBy(() -> round.addSmallTichu(names))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 티츄를 부른 플레이어가 존재합니다.");
    }

    @DisplayName("입력된 이름의_플레이어가 존재하지 않으면 예외가 발생한다.")
    @Test
    void 입력된_이름의_플레이어가_존재하지_않으면_예외가_발생한다() {
        List<String> names = List.of("영희", "주영");
        List<Player> players = List.of(
                new Player("철수"), new Player("영희"),
                new Player("민지"), new Player("민철"));

        TichuGame tichuGame = new TichuGame(players);
        Round round = tichuGame.startRound();

        assertThatThrownBy(() -> round.validatePlayerNames(names))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잘못된 플레이어 이름이 존재합니다.");
    }
}

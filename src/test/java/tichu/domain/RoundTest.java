package tichu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tichu.enums.Rank;
import tichu.enums.Suit;
import tichu.enums.Team;
import tichu.exception.RoundEndSignal;

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
        round.dealCards6();

        List<String> tichuPlayer = List.of("주영");
        round.addLargeTichu(tichuPlayer);
        round.dealCards8();

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

    @DisplayName("라운드 3위가 결정될 경우 라운드 종료 예외가 발생한다")
    @Test
    void 라운드_3위가_결정될_경우_라운드_종료_예외가_발생한다() {
        Player player1 = new Player("영희", Team.RED);
        Player player2 = new Player("주영", Team.BLUE);
        Player player3 = new Player("민지", Team.RED);

        List<Player> players = List.of(player1, player2, player3);

        Round round = new Round(players);
        round.checkRoundPlace(player1);
        round.checkRoundPlace(player2);
        round.checkRoundPlace(player3);

        assertThatThrownBy(round::isRoundEnd)
                .isInstanceOf(RoundEndSignal.class)
                .hasMessage("\n라운드를 종료합니다.");
    }

    @DisplayName("라운드에서 원투가 나온 경우 라운드 종료 예외가 발생한다")
    @Test
    void 라운드에서_원투가_나온_경우_라운드_종료_예외가_발생한다() {
        Player player1 = new Player("영희", Team.RED);
        Player player2 = new Player("주영", Team.BLUE);
        Player player3 = new Player("민지", Team.RED);

        List<Player> players = List.of(player1, player2, player3);
        player2.addMyCards(List.of(new Card(Rank.TWO, Suit.CLUB)));

        Round round = new Round(players);
        round.checkRoundPlace(player1);
        round.checkRoundPlace(player2);
        round.checkRoundPlace(player3);

        assertThatThrownBy(round::isRoundEnd)
                .isInstanceOf(RoundEndSignal.class)
                .hasMessage("\n같은 팀이 1등과 2등을 차지해 라운드를 종료합니다.");
    }
}

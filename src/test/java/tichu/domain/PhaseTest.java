package tichu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static tichu.enums.Rank.ACE;
import static tichu.enums.Rank.EIGHT;
import static tichu.enums.Rank.KING;
import static tichu.enums.Rank.SEVEN;
import static tichu.enums.Special.PHOENIX;
import static tichu.enums.Suit.CLUB;
import static tichu.enums.Suit.DIAMOND;
import static tichu.enums.Suit.HEART;
import static tichu.enums.Suit.SPADE;
import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class PhaseTest {
    @DisplayName("테이블 싱글 조합 K 위의 봉 위에 A를 낼 수 있다.")
    @Test
    void 테이블_싱글_조합_K_위의_봉_위에_A를_낼_수_있다() {
        List<String> names = List.of("영희", "주영", "민지", "철수");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                players.add(new Player(names.get(i), RED));
                continue;
            }
            players.add(new Player(names.get(i), BLUE));
        }
        Round round = new Round(players);
        Phase phase = new Phase(players.get(0), players);

        players.get(0).addMyCards(List.of(new Card(KING, DIAMOND)));
        players.get(1).addMyCards(List.of(new Card(PHOENIX)));
        players.get(2).addMyCards(List.of(new Card(ACE, DIAMOND)));

        phase.evaluateCombination(players.get(0), new Combination(players.get(0).getMyCards()), round);
        phase.evaluateCombination(players.get(1), new Combination(players.get(1).getMyCards()), round);
        phase.evaluateCombination(players.get(2), new Combination(players.get(2).getMyCards()), round);
        assertEquals(new Card(ACE, DIAMOND), phase.getLastCombination().getTopCard());
    }

    @DisplayName("폭탄 위에 낮은 조합을 내면 예외가 발생한다.")
    @Test
    void 폭탄_위에_낮은_조합을_내면_예외가_발생한다() {
        List<String> names = List.of("영희", "주영", "민지", "철수");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                players.add(new Player(names.get(i), RED));
                continue;
            }
            players.add(new Player(names.get(i), BLUE));
        }
        Round round = new Round(players);
        Phase phase = new Phase(players.get(0), players);

        players.get(0).addMyCards(List.of(new Card(KING, DIAMOND)));
        players.get(1).addMyCards(List.of(
                new Card(SEVEN, DIAMOND), new Card(SEVEN, HEART),
                new Card(SEVEN, CLUB), new Card(SEVEN, SPADE)));
        players.get(2).addMyCards(List.of(new Card(ACE, DIAMOND)));

        phase.evaluateCombination(players.get(0), new Combination(players.get(0).getMyCards()), round);
        phase.evaluateCombination(players.get(1), new Combination(players.get(1).getMyCards()), round);
        assertThatThrownBy(
                () -> phase.evaluateCombination(players.get(2), new Combination(players.get(2).getMyCards()), round))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이전 조합보다 낮거나 같은 조합을 낼 수 없습니다.");
    }

    @DisplayName("폭탄 위에 같은 타입의 높은 조합을 낼 수 있다.")
    @Test
    void 폭탄_위에_같은_타입의_높은_조합을_낼_수_있다() {
        List<String> names = List.of("영희", "주영", "민지", "철수");
        List<Player> players = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            if (i % 2 == 0) {
                players.add(new Player(names.get(i), RED));
                continue;
            }
            players.add(new Player(names.get(i), BLUE));
        }
        Round round = new Round(players);
        Phase phase = new Phase(players.get(0), players);

        players.get(0).addMyCards(List.of(new Card(KING, DIAMOND)));
        players.get(1).addMyCards(List.of(
                new Card(SEVEN, DIAMOND), new Card(SEVEN, HEART),
                new Card(SEVEN, CLUB), new Card(SEVEN, SPADE)));
        players.get(2).addMyCards(List.of(
                new Card(EIGHT, DIAMOND), new Card(EIGHT, HEART),
                new Card(EIGHT, CLUB), new Card(EIGHT, SPADE)));
        Combination player1combination = new Combination(players.get(0).getMyCards());
        Combination player2combination = new Combination(players.get(1).getMyCards());
        Combination player3combination = new Combination(players.get(2).getMyCards());

        phase.evaluateCombination(players.get(0), player1combination, round);
        phase.evaluateCombination(players.get(1), player2combination, round);
        phase.evaluateCombination(players.get(2), player3combination, round);
        assertEquals(player3combination, phase.getLastCombination());
    }
}

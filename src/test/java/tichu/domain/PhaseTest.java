package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tichu.enums.Rank.ACE;
import static tichu.enums.Rank.KING;
import static tichu.enums.Special.PHOENIX;
import static tichu.enums.Suit.DIAMOND;
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
        Phase phase = new Phase(players.get(0), players);

        players.get(0).addMyCards(List.of(new Card(KING, DIAMOND)));
        players.get(1).addMyCards(List.of(new Card(PHOENIX)));
        players.get(2).addMyCards(List.of(new Card(ACE, DIAMOND)));

        phase.evaluateCombination(players.get(0), new Combination(players.get(0).getMyCards()));
        phase.evaluateCombination(players.get(1), new Combination(players.get(1).getMyCards()));
        phase.evaluateCombination(players.get(2), new Combination(players.get(2).getMyCards()));

        assertEquals(new Card(ACE, DIAMOND), phase.getLastCombination().getTopCard());
    }
}

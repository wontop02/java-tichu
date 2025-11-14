package tichu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TichuGameTest {
    @DisplayName("입력된 이름의_플레이어가 존재하지 않으면 예외가 발생한다.")
    @Test
    void 입력된_이름의_플레이어가_존재하지_않으면_예외가_발생한다() {
        Player player = new Player("주영");
        List<Player> players = List.of(
                new Player("철수"), new Player("영희"),
                new Player("민지"), new Player("민철"));

        TichuGame tichuGame = new TichuGame(players);

        assertThatThrownBy(() -> tichuGame.findPlayerByName(player.getName()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이름의 플레이어를 찾을 수 없습니다: " + player.getName());
    }
}

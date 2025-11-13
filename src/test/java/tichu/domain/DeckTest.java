package tichu.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class DeckTest {

    @DisplayName("카드 개수는 총 56장이어야 한다.")
    @Test
    void 카드_개수는_총_56장이어야_한다() {
        Deck deck = new Deck();
        int cardCount = deck.getCards().size();

        assertEquals(56, cardCount);
    }
}

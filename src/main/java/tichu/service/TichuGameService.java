package tichu.service;

import java.util.List;
import tichu.domain.Card;
import tichu.domain.CardParser;
import tichu.domain.Phase;
import tichu.domain.Player;
import tichu.domain.Round;

public class TichuGameService {
    private static final String LARGE = "라지";
    private static final String ALREADY_SELECTED_TRADE_CARD = "이전 참가자에게 준 카드를 선택할 수 없습니다.";

    public void addTichuPlayer(Round round, String largeOrSmall, List<String> names) {
        if (largeOrSmall.equals(LARGE)) {
            round.addLargeTichu(names);
            return;
        }
        round.addSmallTichu(names);
    }

    public void giveCard(String input, List<List<Card>> received, Player fromPlayer, List<Card> toSend,
                         int toIndex) {
        //string을 카드로 변환
        Card card = CardParser.fromString(input);
        if (toSend.contains(card)) {
            throw new IllegalArgumentException(ALREADY_SELECTED_TRADE_CARD);
        }

        fromPlayer.validateContainMyCard(card);
        toSend.add(card);
        received.get(toIndex).add(card);
    }

    public void useBomb(String bomb, Player player, Round round, Phase phase, String name) {
        List<Card> cards = CardParser.fromStringToCardList(bomb);
        for (Card card : cards) {
            player.validateContainMyCard(card);
        }
        phase.useBomb(name, cards, round);
    }
}

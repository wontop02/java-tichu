package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Round {
    private static final String ALREADY_CALLED_TICHU = "이미 티츄를 부른 플레이어입니다: ";
    private int currentRound;
    private List<Card> cards;
    private List<Player> largeTichu;
    private List<Player> smallTichu;

    public Round() {
        currentRound = 0;
        largeTichu = new ArrayList<>();
        smallTichu = new ArrayList<>();
    }

    public void settingCards(Deck deck) {
        cards = new ArrayList<>(deck.getCards());
        Collections.shuffle(cards);
    }

    public List<Card> deal(int count) {
        List<Card> dealCards = new ArrayList<>(cards.subList(0, count));
        cards.subList(0, count).clear();
        return dealCards;
    }

    public void addLargeTichu(Player player) {
        largeTichu.add(player);
    }

    public void addSmallTichu(Player player) {
        if (largeTichu.contains(player) || smallTichu.contains(player)) {
            throw new IllegalArgumentException(ALREADY_CALLED_TICHU + player.getName());
        }
        smallTichu.add(player);
    }
}

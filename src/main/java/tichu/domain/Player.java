package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Team;

public class Player {
    private static final String NOT_IN_MY_CARDS = "보유하지 않은 카드가 존재합니다.";
    private static final String CANNOT_CALLED_SMALL_TICHU = "스몰 티츄는 카드가 14장인 플레이어만 부를 수 있습니다.";
    private final String name;
    private Team team;
    private List<Card> myCards = new ArrayList<>();
    private List<Card> acquiredCards = new ArrayList<>();
    private boolean isLargeTichu;
    private boolean isSmallTichu;

    public Player(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    public void callLargeTichu() {
        isLargeTichu = true;
    }

    public boolean getLargeTichuStatus() {
        return isLargeTichu;
    }

    public void callSmallTichu() {
        if (getCardCount() < 14) {
            throw new IllegalArgumentException(CANNOT_CALLED_SMALL_TICHU);
        }
        isSmallTichu = true;
    }

    public boolean getSmallTichuStatus() {
        return isSmallTichu;
    }

    public boolean isCalledTichu() {
        return isLargeTichu || isSmallTichu;
    }

    public void addMyCards(List<Card> card) {
        myCards.addAll(card);
        Collections.sort(myCards);
    }

    public void addAcquireCards(List<Card> card) {
        acquiredCards.addAll(card);
    }

    public void validateContainMyCard(Card card) {
        if (!myCards.contains(card)) {
            throw new IllegalArgumentException(NOT_IN_MY_CARDS);
        }
    }

    public void removeMyCards(List<Card> cards) {
        myCards.removeAll(cards);
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public int getCardCount() {
        return myCards.size();
    }

    public void sortMyCards() {
        Collections.sort(myCards);
    }

    public List<Card> getMyCards() {
        return List.copyOf(myCards);
    }

    public boolean hasMahjong() {
        return myCards.stream().anyMatch(Card::isMahjong);
    }

    public boolean hasStrongThanCombinationWithCall(Combination combination, Rank calledRank) {
        return CombinationFinder.hasStrongCombinationWithCall(myCards, calledRank, combination);
    }
}

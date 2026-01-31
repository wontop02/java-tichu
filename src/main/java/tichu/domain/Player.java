package tichu.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Suit;
import tichu.enums.Team;

public class Player {
    private static final String NOT_IN_MY_CARDS = "보유하지 않은 카드가 존재합니다.";
    private static final String CANNOT_CALLED_SMALL_TICHU = "스몰 티츄는 카드가 14장인 플레이어만 부를 수 있습니다.";

    private final String name;
    private final Team team;
    private final List<Card> myCards = new ArrayList<>();
    private final List<Card> acquiredCards = new ArrayList<>();
    private boolean isLargeTichu;
    private boolean isSmallTichu;

    public Player(String name, Team team) {
        this.name = name;
        this.team = team;
    }

    public void callLargeTichu() {
        isLargeTichu = true;
    }

    public void callSmallTichu() {
        if (getCardCount() < 14) {
            throw new IllegalArgumentException(CANNOT_CALLED_SMALL_TICHU);
        }
        isSmallTichu = true;
    }

    public void addMyCards(List<Card> card) {
        myCards.addAll(card);
        Collections.sort(myCards);
    }

    public void addAcquireCards(List<Card> card) {
        acquiredCards.addAll(card);
    }

    public void removeMyCards(List<Card> cards) {
        myCards.removeAll(cards);
        cards.stream()
                .filter(Card::isPhoenix)
                .findFirst().ifPresent(p -> myCards.remove(new Card(Rank.PHOENIX, Suit.NONE)));
    }

    public void resetStatus() {
        clearMyCards();
        clearAcquireCards();
        resetTichuStatus();
    }

    private void clearMyCards() {
        myCards.clear();
    }

    private void clearAcquireCards() {
        acquiredCards.clear();
    }

    private void resetTichuStatus() {
        isLargeTichu = false;
        isSmallTichu = false;
    }

    public void validateContainMyCard(Card card) {
        if (!myCards.contains(card)) {
            throw new IllegalArgumentException(NOT_IN_MY_CARDS);
        }
    }

    public boolean hasStrongThanCombinationWithCall(Combination combination, Rank calledRank) {
        return CombinationFinder.hasStrongCombinationWithCall(myCards, calledRank, combination);
    }

    public int calculateAcquireCardScore() {
        return calculateScore(acquiredCards);
    }

    public int calculateMyCardScore() {
        return calculateScore(myCards);
    }

    private int calculateScore(List<Card> cards) {
        int score = 0;
        for (Card card : cards) {
            if (card.getRank() == Rank.FIVE) {
                score += Rank.FIVE.getScore();
            }
            if (card.getRank() == Rank.TEN || card.getRank() == Rank.KING) {
                score += Rank.TEN.getScore();
            }
            if (card.isDragon()) {
                score += Rank.DRAGON.getScore();
            }
            if (card.isPhoenix()) {
                score += Rank.PHOENIX.getScore();
            }
        }
        return score;
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

    public List<Card> getMyCards() {
        return List.copyOf(myCards);
    }

    public List<Card> getAcquiredCards() {
        return List.copyOf(acquiredCards);
    }

    public boolean hasMahjong() {
        return myCards.stream().anyMatch(Card::isMahjong);
    }

    public boolean getLargeTichuStatus() {
        return isLargeTichu;
    }

    public boolean getSmallTichuStatus() {
        return isSmallTichu;
    }

    public boolean isCalledTichu() {
        return isLargeTichu || isSmallTichu;
    }
}

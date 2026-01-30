package tichu.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import tichu.enums.Rank;
import tichu.enums.Suit;

public class CardParser {
    private CardParser() {
    }

    public static List<Card> fromStringToCardList(String input) {
        List<String> inputs = Arrays.asList(input.split(","));
        return inputs.stream()
                .map(CardParser::fromString)
                .toList();
    }

    public static Card fromString(String input) {
        if (input.length() <= 1) {
            Rank rank = Rank.valueOfRank(input);
            return new Card(rank, Suit.NONE);
        }
        String rankInput = input.substring(0, input.length() - 1).toUpperCase();
        String suitInput = input.substring(input.length() - 1).toLowerCase();
        Rank rank = Rank.valueOfRank(rankInput);
        Suit suit = Suit.valueOfSuit(suitInput);
        return new Card(rank, suit);
    }

    public static List<String> fromCardsToStringList(List<Card> cards) {
        List<Card> parsedCards = new ArrayList<>(cards);
        Collections.sort(parsedCards);
        return parsedCards.stream()
                .map(CardParser::fromCard)
                .toList();
    }

    public static String fromCard(Card card) {
        if (card.isSpecial()) {
            return card.getSpecialRank();
        }
        if (card.isSubstitutePhoenix()) {
            return Rank.PHOENIX.getRank();
        }
        Rank rank = card.getRank();
        Suit suit = card.getSuit();
        return rank.getRank() + suit.getSuit();
    }
}

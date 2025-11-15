package tichu.domain;

import java.util.Set;
import tichu.enums.Rank;
import tichu.enums.Special;
import tichu.enums.Suit;

public class CardParser {
    private static final Set<String> SPECIAL =
            Set.of("개", "1", "봉", "용");

    private CardParser() {
    }

    public static Card fromString(String input) {
        if (SPECIAL.contains(input)) {
            return new Card(Special.valueOfSpecial(input));
        }
        String rankInput = input.substring(0, input.length() - 1).toUpperCase();
        String suitInput = input.substring(input.length() - 1).toLowerCase();
        Rank rank = Rank.valueOfRank(rankInput);
        Suit suit = Suit.valueOfSuit(suitInput);
        return new Card(rank, suit);
    }
}

package tichu.dto;

import java.util.List;
import tichu.domain.CardParser;
import tichu.domain.Combination;
import tichu.enums.CombinationType;
import tichu.enums.Rank;

public class CombinationDto {

    private final CombinationType type;
    private final List<String> cards;
    private final Rank topRank;

    private CombinationDto(CombinationType type, List<String> cards, Rank topRank) {
        this.type = type;
        this.cards = cards;
        this.topRank = topRank;
    }

    public static CombinationDto from(Combination combination) {
        return new CombinationDto(
                combination.getCombinationType(),
                CardParser.fromCardsToStringList(combination.getCards()),
                combination.getTopRank()
        );
    }

    public CombinationType getType() {
        return type;
    }

    public List<String> getCards() {
        return cards;
    }

    public Rank getTopRank() {
        return topRank;
    }
}

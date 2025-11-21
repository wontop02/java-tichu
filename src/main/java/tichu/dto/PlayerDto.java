package tichu.dto;

import java.util.List;
import tichu.domain.CardParser;
import tichu.domain.Player;
import tichu.enums.Team;

public class PlayerDto {
    private final String name;
    private final Team team;
    private final List<String> myCards;
    private final boolean smallTichu;
    private final boolean largeTichu;

    private PlayerDto(String name, Team team, List<String> myCards, boolean smallTichu, boolean largeTichu) {
        this.name = name;
        this.team = team;
        this.myCards = myCards;
        this.smallTichu = smallTichu;
        this.largeTichu = largeTichu;
    }

    public static PlayerDto from(Player player) {
        return new PlayerDto(
                player.getName(),
                player.getTeam(),
                CardParser.fromCardsToStringList(player.getMyCards()),
                player.getSmallTichuStatus(),
                player.getLargeTichuStatus()
        );
    }

    public String getName() {
        return name;
    }

    public Team getTeam() {
        return team;
    }

    public List<String> getMyCards() {
        return myCards;
    }


    public int getCardCount() {
        return myCards.size();
    }

    public boolean isSmallTichu() {
        return smallTichu;
    }

    public boolean isLargeTichu() {
        return largeTichu;
    }
}

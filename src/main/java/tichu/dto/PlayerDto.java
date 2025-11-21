package tichu.dto;

import static tichu.enums.Team.BLUE;
import static tichu.enums.Team.RED;

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

    private static final String PLAYER_STATUS_FORMAT = "%s(%s%s)";
    private static final String RED_TEAM = "red";
    private static final String BLUE_TEAM = "blue";
    private static final String LARGE_TICHU = "-L";
    private static final String SMALL_TICHU = "-S";
    private static final String NONE_TICHU = "";

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

    public String getStatus() {
        String myTeam = "";
        if (team == RED) {
            myTeam = RED_TEAM;
        }
        if (team == BLUE) {
            myTeam = BLUE_TEAM;
        }
        String tichuStatus = "";
        if (isLargeTichu()) {
            tichuStatus = LARGE_TICHU;
        }
        if (isSmallTichu()) {
            tichuStatus = SMALL_TICHU;
        }
        if (!isSmallTichu() && !isLargeTichu()) {
            tichuStatus = NONE_TICHU;
        }
        return String.format(PLAYER_STATUS_FORMAT, name, myTeam, tichuStatus);
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

package tichu.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import tichu.domain.Card;
import tichu.domain.CardParser;
import tichu.domain.Combination;
import tichu.domain.CombinationEvaluator;
import tichu.domain.Phase;
import tichu.domain.Player;
import tichu.domain.Round;
import tichu.domain.TichuGame;
import tichu.dto.PhaseDto;
import tichu.dto.PlayerDto;
import tichu.dto.RoundDto;
import tichu.enums.Place;
import tichu.enums.Rank;
import tichu.enums.Team;
import tichu.exception.PhaseEndSignal;
import tichu.exception.PhaseEndSignalWithDog;
import tichu.exception.RoundEndSignal;
import tichu.service.TichuGameService;
import tichu.util.InputValidator;
import tichu.view.InputView;
import tichu.view.OutputView;

public class TichuGameController {
    private static final String LARGE = "라지";
    private static final String SMALL = "스몰";
    private static final String PASS = "p";
    private static final String CANCEL = "x";
    private static final int NUMBER_OF_PLAYER = 4;
    private static final int FIRST_CARD_COUNT = 8;
    private static final int SECOND_CARD_COUNT = 6;
    private static final int MIN_BOMB_CARD_COUNT = 4;

    private final TichuGameService tichuGameService;

    public TichuGameController(TichuGameService tichuGameService) {
        this.tichuGameService = tichuGameService;
    }

    public void start() {
        TichuGame tichuGame = inputPlayers();

        while (!tichuGame.isEndTichuGame()) {
            Round round = tichuGame.startRound();
            beforeRound(tichuGame, round);

            Map<Team, Integer> roundScore = playRound(round);
            OutputView.printRoundScore(roundScore);

            Map<Team, Integer> teamScore = tichuGame.addTeamScore(roundScore);
            OutputView.printTeamScore(teamScore);
        }
        Team winner = tichuGame.tichuGameWinner();
        OutputView.printWinner(winner);
    }

    private void beforeRound(TichuGame tichuGame, Round round) {
        OutputView.printRoundStart(tichuGame.getRoundNumber());

        round.dealCards(FIRST_CARD_COUNT);
        OutputView.printDealCards(FIRST_CARD_COUNT);
        printAllPlayersCards(round);

        inputTichuPlayers(round, LARGE);

        round.dealCards(SECOND_CARD_COUNT);
        OutputView.printDealCards(SECOND_CARD_COUNT);
        printAllPlayersCards(round);

        inputTichuPlayers(round, SMALL);

        OutputView.printTradeCards();
        tradeCards(round);
        OutputView.printTradeEnd();
        printAllPlayersCards(round);
    }

    // 참가자 입력
    public TichuGame inputPlayers() {
        while (true) {
            try {
                String input = InputView.requestPlayerNames();
                InputValidator.validatePlayerNames(input);
                List<String> names = Arrays.asList(input.split(",", -1));

                TichuGame tichuGame = new TichuGame(names);
                printTeamAndDirection(tichuGame);
                return tichuGame;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void printTeamAndDirection(TichuGame tichuGame) {
        List<PlayerDto> playerDtos = tichuGame.getPlayersWithDirection().stream()
                .map(PlayerDto::from)
                .toList();

        List<PlayerDto> red = playerDtos.stream()
                .filter(p -> p.getTeam() == Team.RED)
                .toList();

        List<PlayerDto> blue = playerDtos.stream()
                .filter(p -> p.getTeam() == Team.BLUE)
                .toList();

        OutputView.printTeam(red, blue);
        OutputView.printDirection(playerDtos);
    }

    // 플레이어 카드 출력
    private void printAllPlayersCards(Round round) {
        round.getPlayers().forEach(player -> {
            PlayerDto dto = PlayerDto.from(player);
            OutputView.printPlayerAllCards(dto);
        });
    }

    // 티츄 입력
    private void inputTichuPlayers(Round round, String largeOrSmall) {
        while (true) {
            try {
                String input = InputView.requestTichuCallerName(largeOrSmall);
                if (input.equals(CANCEL)) {
                    return;
                }
                InputValidator.validateTichuCallerName(input);
                List<String> names = Arrays.asList(input.split(",", -1));
                round.validatePlayerNames(names);
                tichuGameService.addTichuPlayer(round, largeOrSmall, names);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void tradeCards(Round round) {
        List<Player> players = round.getPlayers();
        List<List<Card>> received = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
            received.add(new ArrayList<>());
        }
        while (true) {
            try {
                allTradeCards(round, players, received);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void allTradeCards(Round round, List<Player> players, List<List<Card>> received) {
        for (int i = 0; i < NUMBER_OF_PLAYER; i++) {
            Player fromPlayer = players.get(i);
            OutputView.printTurnPlayer(PlayerDto.from(fromPlayer));
            List<Card> toSend = new ArrayList<>();
            // 나를 제외한 참가자 수
            int[] nextDirections = {1, 2, 3};
            giveCards(players, received, i, fromPlayer, toSend, nextDirections);
            fromPlayer.removeMyCards(toSend);
        }
        round.tradeCards(received);
    }

    private void giveCards(List<Player> players, List<List<Card>> received, int i, Player fromPlayer, List<Card> toSend,
                           int[] nextDirections) {
        while (true) {
            try {
                PlayerDto fromPlayerDto = PlayerDto.from(fromPlayer);
                OutputView.printPlayerAllCards(fromPlayerDto);
                for (int nextDirection : nextDirections) {
                    giveCard(players, received, i, fromPlayer, toSend, nextDirection);
                }
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void giveCard(List<Player> players, List<List<Card>> received, int i, Player fromPlayer, List<Card> toSend,
                          int nextDirection) {
        while (true) {
            try {
                int toIndex = (i + nextDirection) % NUMBER_OF_PLAYER;
                Player toPlayer = players.get(toIndex);
                PlayerDto toPlayerDto = PlayerDto.from(toPlayer);

                String input = InputView.requestTradeCard(toPlayerDto);
                InputValidator.validateCardInput(input);

                tichuGameService.giveCard(input, received, fromPlayer, toSend, toIndex);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    public Map<Team, Integer> playRound(Round round) {
        Phase phase = null;
        while (true) {
            try {
                while (!round.isRoundEnd()) {
                    phase = round.startPhase();
                    PlayOneRound(round, phase);
                }
            } catch (PhaseEndSignalWithDog e) {
                endPhaseWithDog(round, phase, e);
            } catch (RoundEndSignal e) {
                return endRound(round, phase, e);
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void endPhaseWithDog(Round round, Phase phase, PhaseEndSignalWithDog e) {
        round.endPhaseWithDog(phase);
        OutputView.printMessage(e.getMessage());
        Player useDogPlayer = phase.getPhaseWinner();
        printRoundEndPlayer(round, useDogPlayer);
    }

    private void PlayOneRound(Round round, Phase phase) {
        if (round.getPhaseNumber() == 1) {
            OutputView.printFirstPlayer(PlayerDto.from(phase.getCurrentPlayer()));
        }
        OutputView.printPhaseStart(round.getPhaseNumber());
        playPhase(round, phase);
    }

    private Map<Team, Integer> endRound(Round round, Phase phase, RoundEndSignal e) {
        if (phase != null) {
            round.endPhase(phase);
        }
        // 원투면 4등이 없을 수도 있음
        if (round.getPlayerPlace().containsKey(Place.FOURTH)) {
            Player fourth = round.getPlayerPlace().get(Place.FOURTH);
            OutputView.printRoundEndPlayer(PlayerDto.from(fourth), Place.FOURTH.getPlace());
        }
        OutputView.printMessage(e.getMessage());
        return round.calculateScore();
    }

    private String inputReceivePlayer(Round round, Phase phase) {
        while (true) {
            try {
                String name = InputView.requestReceivePlayerName();
                InputValidator.validatePlayerName(name);
                round.endPhase(phase, name);
                return name;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void playPhase(Round round, Phase phase) {
        try {
            while (!phase.isPhaseEnd()) {
                if (askSmallTichu(round)) {
                    inputTichuPlayers(round, SMALL);
                }
                if (askBomb(round, phase)) {
                    if (inputBomb(round, phase)) {
                        continue;
                    }
                }
                playTurn(round, phase);
            }
        } catch (PhaseEndSignal e) {
            endPhase(round, phase, e);
        }
    }

    private void endPhase(Round round, Phase phase, PhaseEndSignal e) {
        round.endPhase(phase);
        OutputView.printMessage(e.getMessage());
        if (phase.winWithDragon()) {
            inputReceivePlayer(round, phase);
        }
    }

    private void printTurnStatus(Round round, Phase phase) {
        Combination lastCombination = phase.getLastCombination();
        PhaseDto phaseDto = PhaseDto.from(phase);
        RoundDto roundDto = RoundDto.from(round);
        // 테이블 조합 출력
        printTableCombination(lastCombination, roundDto, phaseDto);
        // 현재 카드 개수 출력
        List<PlayerDto> dtos = round.getPlayers().stream()
                .map(PlayerDto::from)
                .toList();
        OutputView.printAllCardCount(dtos);
        // 현재 플레이어 카드 출력
        PlayerDto playerDto = PlayerDto.from(phase.getCurrentPlayer());
        OutputView.printPlayerCards(playerDto);
    }

    private void printTableCombination(Combination lastCombination, RoundDto roundDto, PhaseDto phaseDto) {
        // 테이블 조합 출력
        if (lastCombination == null) {
            OutputView.printTableCombination(roundDto);
        }
        if (lastCombination != null) {
            List<String> lastCards = CardParser.fromCardsToStringList(lastCombination.cards());
            OutputView.printTableCombination(roundDto, phaseDto, lastCards);
        }
    }

    private void playTurn(Round round, Phase phase) {
        printTurnStatus(round, phase);

        Player player = phase.getCurrentPlayer();
        handleTurnInput(round, phase, player);
    }

    private void inputPassPlayer(Phase phase, Player player, Round round) {
        phase.pass(player, round);
    }

    private void handleTurnInput(Round round, Phase phase, Player player) {
        while (true) {
            try {
                String input = InputView.requestCombination();
                if (input.equals(PASS)) {
                    inputPassPlayer(phase, player, round);
                    return;
                }
                inputCombination(round, phase, player, input);
                printRoundEndPlayer(round, player);
                round.isRoundEnd();
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void inputCombination(Round round, Phase phase, Player player, String input) {
        InputValidator.validateCardInput(input);
        List<Card> cards = CardParser.fromStringToCardList(input);
        for (Card card : cards) {
            player.validateContainMyCard(card);
        }

        Combination combination = CombinationEvaluator.evaluate(cards);
        phase.evaluateCombination(player, combination, round);
        if (combination.hasMahjong()) {
            inputCallRank(round);
        }
    }

    private void inputCallRank(Round round) {
        while (true) {
            try {
                String input = InputView.requestCallRank();
                if (input.equals(CANCEL)) {
                    return;
                }
                InputValidator.validateCallRank(input);
                Rank rank = Rank.valueOfRank(input.toUpperCase());

                round.callRank(rank);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private boolean inputBomb(Round round, Phase phase) {
        while (true) {
            try {
                String name = InputView.requestBombPlayer();
                if (name.equals(CANCEL)) {
                    return false;
                }
                InputValidator.validatePlayerName(name);
                Player player = phase.findPlayer(name);
                inputBombCard(round, phase, name, player);
                printRoundEndPlayer(round, player);
                round.isRoundEnd();
                return true;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void inputBombCard(Round round, Phase phase, String name, Player player) {
        while (true) {
            try {
                OutputView.printPlayerAllCards(PlayerDto.from(player));
                String bomb = InputView.requestBomb();
                if (bomb.equals(CANCEL)) {
                    return;
                }
                InputValidator.validateCardInput(bomb);
                tichuGameService.useBomb(bomb, player, round, phase, name);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void printRoundEndPlayer(Round round, Player player) {
        round.checkRoundPlace(player);
        if (round.getPlayerPlace().containsValue(player)) {
            OutputView.printRoundEndPlayer(PlayerDto.from(player), round.getPlace(player));
        }
    }

    private boolean askSmallTichu(Round round) {
        return round.getPlayers().stream()
                .anyMatch(player ->
                        !player.isCalledTichu() && player.getCardCount() == FIRST_CARD_COUNT + SECOND_CARD_COUNT);
    }

    private boolean askBomb(Round round, Phase phase) {
        return (round.getPlayers().stream()
                .anyMatch(player -> player.getCardCount() >= MIN_BOMB_CARD_COUNT))
                && (phase.getLastCombination() != null);
    }
}

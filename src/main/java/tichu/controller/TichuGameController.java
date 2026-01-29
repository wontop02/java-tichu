package tichu.controller;

import static tichu.enums.Place.FOURTH;

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
import tichu.enums.Rank;
import tichu.enums.Team;
import tichu.exception.PhaseEndSignal;
import tichu.exception.RoundEndSignal;
import tichu.util.InputValidator;
import tichu.view.InputView;
import tichu.view.OutputView;

public class TichuGameController {
    private static final String LARGE = "라지";
    private static final String SMALL = "스몰";
    private static final String ALREADY_SELECTED_TRADE_CARD = "이전 참가자에게 준 카드를 선택할 수 없습니다.";
    private static final String ROUND_END = "\n라운드를 종료합니다.";

    public TichuGameController() {
    }

    public void start() {
        TichuGame tichuGame = inputPlayers();

        while (!tichuGame.isEndTichuGame()) {
            Round round = tichuGame.startRound();
            OutputView.printRoundStart(tichuGame.getRoundNumber());

            round.dealCards8();
            OutputView.printDealCards(8);
            printAllPlayersCards(round);

            inputTichuPlayers(round, LARGE);

            round.dealCards6();
            OutputView.printDealCards(6);
            printAllPlayersCards(round);

            inputTichuPlayers(round, SMALL);

            OutputView.printTradeCards();
            tradeCards(round);
            OutputView.printTradeEnd();
            printAllPlayersCards(round);

            Map<Team, Integer> roundScore = playRound(round);
            OutputView.printRoundScore(roundScore);

            Map<Team, Integer> teamScore = tichuGame.addTeamScore(roundScore);
            OutputView.printTeamScore(teamScore);
        }
        Team winner = tichuGame.tichuGameWinner();
        OutputView.printWinner(winner);
    }

    // 참가자 입력
    public TichuGame inputPlayers() {
        while (true) {
            try {
                String input = InputView.requestPlayerNames();
                InputValidator.validatePlayerNames(input);
                List<String> names = Arrays.asList(input.split(",", -1));

                TichuGame tichuGame = new TichuGame(names);

                List<PlayerDto> playerDtos = tichuGame.getPlayersWithDirection().stream()
                        .map(PlayerDto::from)
                        .toList();

                List<PlayerDto> red = playerDtos.stream()
                        .filter(p -> p.getTeam() == Team.RED)
                        .toList();

                List<PlayerDto> blue = playerDtos.stream()
                        .filter(p -> p.getTeam() == Team.BLUE)
                        .toList();
                OutputView.printTeamAndDirection(playerDtos, red, blue);
                return tichuGame;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    // 플레이어 카드 출력
    private void printAllPlayersCards(Round round) {
        round.getPlayers().forEach(player -> {
            PlayerDto dto = PlayerDto.from(player);
            OutputView.printPlayerAllCards(dto);
        });
    }

    // 티츄 입력
    public void inputTichuPlayers(Round round, String largeOrSmall) {
        while (true) {
            try {
                String input = InputView.requestTichuCallerName(largeOrSmall);
                if (input.equals("x")) {
                    return;
                }
                InputValidator.validateTichuCallerName(input);
                List<String> names = Arrays.asList(input.split(",", -1));
                round.validatePlayerNames(names);
                if (largeOrSmall.equals(LARGE)) {
                    round.addLargeTichu(names);
                    return;
                }
                round.addSmallTichu(names);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    public void tradeCards(Round round) {
        List<Player> players = round.getPlayers();
        List<List<Card>> received = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            received.add(new ArrayList<>());
        }
        while (true) {
            try {
                for (int i = 0; i < 4; i++) {
                    Player fromPlayer = players.get(i);
                    OutputView.printTurnPlayer(PlayerDto.from(fromPlayer));
                    List<Card> toSend = new ArrayList<>();
                    // 나를 제외한 참가자 수
                    int[] nextDirections = {1, 2, 3};
                    giveCards(players, received, i, fromPlayer, toSend, nextDirections);
                    fromPlayer.removeMyCards(toSend);
                }
                round.tradeCards(received);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
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
                int toIndex = (i + nextDirection) % 4;
                Player toPlayer = players.get(toIndex);
                PlayerDto toPlayerDto = PlayerDto.from(toPlayer);

                String input = InputView.requestTradeCard(toPlayerDto);
                InputValidator.validateCardInput(input);

                //string을 카드로 변환
                Card card = CardParser.fromString(input);
                if (toSend.contains(card)) {
                    throw new IllegalArgumentException(ALREADY_SELECTED_TRADE_CARD);
                }

                fromPlayer.validateContainMyCard(card);
                toSend.add(card);
                received.get(toIndex).add(card);
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
                    if (round.getPhaseNumber() == 1) {
                        OutputView.printFirstPlayer(PlayerDto.from(phase.getCurrentPlayer()));
                    }
                    OutputView.printPhaseStart(round.getPhaseNumber());
                    playPhase(round, phase);
                    OutputView.printPhaseEnd(PlayerDto.from(phase.getPhaseWinner()));
                    if (phase.winWithDragon()) {
                        inputReceivePlayer(round, phase);
                        continue;
                    }
                    round.endPhase(phase);
                }
                Player fourth = round.getPlayerPlace().get(FOURTH);
                OutputView.printRoundEnd(PlayerDto.from(fourth), 4);

                return round.calculateScore();
            } catch (PhaseEndSignal e) {
                try {
                    Player dogPlayer = phase.getPhaseWinner();
                    round.endPhaseWithDog(phase);
                    if (dogPlayer.getCardCount() == 0) {
                        round.checkRoundPlace(dogPlayer);
                    }
                    OutputView.printMessage(e.getMessage());
                } catch (RoundEndSignal signal) {
                    OutputView.printMessage(signal.getMessage());
                    return round.calculateScore();
                }
            } catch (RoundEndSignal e) {
                // 원투면 4등이 없을 수도 있음
                if (phase != null) {
                    round.endPhase(phase);
                }
                if (round.getPlayerPlace().containsKey(FOURTH)) {
                    Player fourth = round.getPlayerPlace().get(FOURTH);
                    OutputView.printRoundEnd(PlayerDto.from(fourth), 4);
                }
                OutputView.printMessage(e.getMessage());
                return round.calculateScore();
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
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
    }

    private void printTurnStatus(Round round, Phase phase) {
        Combination lastCombination = phase.getLastCombination();
        PhaseDto phaseDto = PhaseDto.from(phase);
        RoundDto roundDto = RoundDto.from(round);
        // 테이블 조합 출력
        if (lastCombination == null) {
            OutputView.printTableCombination(roundDto);
        }
        if (lastCombination != null) {
            List<String> lastCards = CardParser.fromCardsToStringList(lastCombination.getCards());
            OutputView.printTableCombination(roundDto, phaseDto, lastCards);
        }
        // 현재 카드 개수 출력
        List<PlayerDto> dtos = round.getPlayers().stream()
                .map(PlayerDto::from)
                .toList();
        OutputView.printAllCardCount(dtos);

        // 현재 플레이어 카드 출력
        PlayerDto playerDto = PlayerDto.from(phase.getCurrentPlayer());
        OutputView.printPlayerCards(playerDto);
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
                if (input.equals("p")) {
                    inputPassPlayer(phase, player, round);
                    return;
                }
                inputCombination(round, phase, player, input);

                if (round.checkRoundPlace(player)) {
                    int place = round.getPlace(player);
                    OutputView.printRoundEnd(PlayerDto.from(player), place);
                    if (place == 3) {
                        round.isRoundEnd();
                        throw new RoundEndSignal(ROUND_END);
                    }
                }
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
                if (input.equals("x")) {
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
                if (name.equals("x")) {
                    return false;
                }
                InputValidator.validatePlayerName(name);
                Player player = phase.findPlayer(name);
                inputBombCard(round, phase, name, player);
                if (round.checkRoundPlace(player)) {
                    int place = round.getPlace(player);
                    OutputView.printRoundEnd(PlayerDto.from(player), place);
                    if (place == 3) {
                        round.isRoundEnd();
                        throw new RoundEndSignal(ROUND_END);
                    }
                }
                return true;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private void inputBombCard(Round round, Phase phase, String name, Player player) {
        while (true) {
            try {
                PlayerDto playerDto = PlayerDto.from(player);
                OutputView.printPlayerAllCards(playerDto);
                String bomb = InputView.requestBomb();
                if (bomb.equals("x")) {
                    return;
                }
                InputValidator.validateCardInput(bomb);
                List<Card> cards = CardParser.fromStringToCardList(bomb);
                for (Card card : cards) {
                    player.validateContainMyCard(card);
                }
                phase.useBomb(name, cards, round);
                return;
            } catch (IllegalArgumentException e) {
                OutputView.printErrorMessage(e.getMessage());
            }
        }
    }

    private boolean askSmallTichu(Round round) {
        return round.getPlayers().stream()
                .anyMatch(player ->
                        !player.isCalledTichu() && player.getCardCount() == 14);
    }

    private boolean askBomb(Round round, Phase phase) {
        return (round.getPlayers().stream()
                .anyMatch(player -> player.getCardCount() >= 4))
                && (phase.getLastCombination() != null);
    }
}

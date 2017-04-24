package com.backbase.kalah.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.backbase.kalah.exception.BoardNotFoundException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.model.Player;
import com.backbase.kalah.repository.ResourceStore;
import com.backbase.kalah.util.Constants;

/**
 * Service class for Kalah operations
 * Created by tojagrut
 */
@Service
public class KalahService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KalahService.class);
    /**
     * Creates new instance of Board
     * 
     * @return Board
     */
    public Board createBoard() {
        Board board = new Board();
        ResourceStore.saveBoard(board);
        return board;
    }

    /**
     * Retrieve already created board using id
     * 
     * @param id id
     * @return Board
     * @throws Exception exception if board does not exist
     */
    public Board retrieveBoard(String id) throws BoardNotFoundException {
        return getBoard(id);
    }

    /**
     * Move the stones according to pit selected by a player
     * 
     * @param pit pit id
     * @return Board
     */
    public Board play(String id, int pit) throws BoardNotFoundException {
        Board board = getBoard(id);
        if (!Optional.ofNullable(board.getWinner()).isPresent()) {
            play(board, pit);
        }
        return board;
    }

    private Board getBoard(String id) throws BoardNotFoundException {
        Board board = ResourceStore.getBoard(id);
        if (board == null) {
            LOGGER.error("Board does not exist with id {} ", id);
            throw new BoardNotFoundException("Invalid board id - " + id);
        }
        return board;
    }

    private void play(Board board, int pit) {
        Player currentPlayer = board.getNextTurn().equalsIgnoreCase(board.getNorth().getName()) ? board.getNorth() : board.getSouth();
        Player opponentPlayer = currentPlayer == board.getNorth() ? board.getSouth() : board.getNorth();
        int stonesToSow = Optional.ofNullable(currentPlayer.getPits().stream()
                .filter(eachPit -> pit == eachPit.getId()).findFirst().orElse(null)).map(Pit::getStones).orElse(0);

        // combine all pits of current player, its own kalah and pits of opponent
        // Kalah of opponent player is not included
        List<Pit> allPits = getAllPits(currentPlayer, opponentPlayer);

        processSow(pit, stonesToSow, allPits);
        Pit lastPit = getPit(pit, allPits, stonesToSow);

        processCapture(currentPlayer, opponentPlayer, lastPit);
        processAllEmptyPits(currentPlayer, opponentPlayer);
        processBoardStatus(board, currentPlayer, opponentPlayer, lastPit);
    }

    private void processSow(int pit, int stonesToSow, List<Pit> allPits) {
        // move stone one by one to the next pit including current player's kalah and opponent player's pits
        IntStream.rangeClosed(1, stonesToSow).forEach(stone -> getPit(pit, allPits, stone).addStones(1));
        allPits.get(pit - 1).setStones(0);
    }

    private void processBoardStatus(Board board, Player currentPlayer, Player opponentPlayer, Pit lastPit) {
        if (isWinner(currentPlayer)) {
            LOGGER.info("Player {} has won the game.", currentPlayer.getName());
            board.setWinner(currentPlayer.getName());
            board.setNextTurn(null);
        }
        else if (isGameTied(currentPlayer, opponentPlayer)) {
            LOGGER.info("Game is tied..");
            board.setWinner("IT'S A TIE!!");
            board.setNextTurn(null);
        }
        else {
            // set name of the player for next turn
            // if last pit is kalah, set the name of the current player again
            board.setNextTurn(isLastPitKalah(currentPlayer, lastPit) ? currentPlayer.getName() : opponentPlayer.getName());
        }
    }

    private void processAllEmptyPits(Player currentPlayer, Player opponentPlayer) {
        if (!isStoneAvailable(currentPlayer)) {
            collectStones(opponentPlayer);
        }
        else if (!isStoneAvailable(opponentPlayer)) {
            collectStones(currentPlayer);
        }
    }

    private void processCapture(Player currentPlayer, Player opponentPlayer, Pit lastPit) {
        if (isCaptureRequired(currentPlayer, lastPit)) {
            LOGGER.info("Capturing opponent's stones from opposite pit...");
            Pit opponentPitToCapture = opponentPlayer.getPits().get(Constants.MAX_PITS - lastPit.getId());
            // add stones of last pit and opponent's pit in kalah
            currentPlayer.getKalah().addStones(lastPit.getStones() + opponentPitToCapture.getStones());
            lastPit.setStones(0);
            opponentPitToCapture.setStones(0);
        }
    }

    private boolean isWinner(Player currentPlayer) {
        return currentPlayer.getKalah().getStones() >= Constants.MIN_STONES_TO_WIN;
    }

    private void collectStones(Player player) {
        // collect stones from each of the pit and add them in kalah
        LOGGER.info("No stones available with current player, collecting stones in opponent kalah");
        player.getPits().forEach(pit -> {
            player.getKalah().addStones(pit.getStones());
            pit.setStones(0);
        });
    }

    private boolean isStoneAvailable(Player player) {
        return player.getPits().stream().anyMatch(Pit::isStoneAvailable);
    }

    private boolean isGameTied(Player currentPlayer, Player opponentPlayer) {
        // check if the no. of stones in kalah of current player and opponent player is 36
        return currentPlayer.getKalah().getStones() == Constants.MIN_STONES_TO_WIN - 1
                && currentPlayer.getKalah().getStones() == opponentPlayer.getKalah().getStones();
    }

    private boolean isLastPitKalah(Player currentPlayer, Pit lastPit) {
        return lastPit == currentPlayer.getKalah();
    }

    private boolean isCaptureRequired(Player currentPlayer, Pit lastPit) {
        // check if last pit after sowing is of current player and contains only 1 stone
        return currentPlayer.getPits().contains(lastPit) && lastPit.getStones() == 1;
    }

    private Pit getPit(int pit, List<Pit> allPits, int stone) {
        int nextPitIndex = pit + stone - 1;
        // see if the next pit index is great than available no. of pits
        if (nextPitIndex >= allPits.size()) {
            // start from the first pit again
            nextPitIndex -= allPits.size();
        }
        return allPits.get(nextPitIndex);
    }

    private List<Pit> getAllPits(Player currentPlayer, Player opponentPlayer) {
        List<Pit> allPits = new ArrayList<>();
        allPits.addAll(currentPlayer.getPits());
        allPits.add(currentPlayer.getKalah());
        allPits.addAll(opponentPlayer.getPits());
        return allPits;
    }
}

package com.backbase.kalah.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import com.backbase.kalah.exception.BoardNotFoundException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.repository.ResourceStore;

/**
 * Test class for {@link KalahService}
 * Created by tojagrut
 */
@RunWith(MockitoJUnitRunner.class)
public class KalahServiceTest {

    @InjectMocks
    KalahService kalahService;

    @Test
    public void testCreateBoard() throws Exception {
        assertNotNull("Board is not created", kalahService.createBoard());
    }

    @Test
    public void testRetrieveBoard() throws BoardNotFoundException {
        Board board = new Board();
        ResourceStore.saveBoard(board);
        assertEquals("Invalid board id", board.getBoardId(), kalahService.retrieveBoard(board.getBoardId()).getBoardId());
    }

    @Test(expected = BoardNotFoundException.class)
    public void testRetrieveBoardWithInvalidId() throws BoardNotFoundException {
        Board board = new Board();
        ResourceStore.saveBoard(board);
        kalahService.retrieveBoard("abc1234").getBoardId();
    }

    @Test
    public void testPlay() throws BoardNotFoundException {
        Board board = new Board();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 2);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid player name for next turn", "South", result.getNextTurn());
    }

    @Test
    public void testPlayWithCapture() throws BoardNotFoundException {
        Board board = createBoardForCapture();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 1);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid no. of stones in kalah", 7, result.getNorth().getKalah().getStones());
        assertEquals("Invalid no. of stones in current player's selected pit", 0, result.getNorth().getPits().get(0).getStones());
        assertEquals("Invalid no. of stones in current player's empty pit", 0, result.getNorth().getPits().get(1).getStones());
        assertEquals("Invalid no. of stones in opponent's pit after capture", 0, result.getSouth().getPits().get(4).getStones());
    }

    @Test
    public void testPlayWithCollectAllStones() throws BoardNotFoundException {
        Board board = createBoardForCollectAllStones();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 6);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid no. of stones in kalah", 1, result.getNorth().getKalah().getStones());
        assertEquals("Invalid no. of stones in current player's selected pit", 0, result.getNorth().getPits().get(5).getStones());
        assertEquals("Invalid no. of stones in opponent's kalah", 36, result.getSouth().getKalah().getStones());
        assertEquals("Invalid total no. of stones in opponent's pits", 0, result.getSouth().getPits().stream().mapToInt(Pit::getStones).sum());
    }

    @Test
    public void testPlayWithTurnAgain() throws BoardNotFoundException {
        Board board = createBoardForTurnAgain();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 6);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid no. of stones in kalah", 1, result.getNorth().getKalah().getStones());
        assertEquals("Invalid no. of stones in current player's selected pit", 0, result.getNorth().getPits().get(5).getStones());
        assertEquals("Invalid player name for next turn", "North", result.getNextTurn());
    }

    @Test
    public void testPlayWithWinner() throws BoardNotFoundException {
        Board board = createBoardForWinner();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 6);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid no. of stones in kalah", 37, result.getNorth().getKalah().getStones());
        assertEquals("Invalid no. of stones in current player's selected pit", 0, result.getNorth().getPits().get(5).getStones());
        assertNull("Invalid player name for next turn", result.getNextTurn());
        assertEquals("Invalid winner", "North", result.getWinner());
    }

    @Test
    public void testPlayWithTie() throws BoardNotFoundException {
        Board board = createBoardForTie();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 6);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid no. of stones in kalah", 36, result.getNorth().getKalah().getStones());
        assertEquals("Invalid no. of stones in opponent's kalah", 36, result.getSouth().getKalah().getStones());
        assertEquals("Invalid no. of stones in current player's selected pit", 0, result.getNorth().getPits().get(5).getStones());
        assertNull("Invalid player name for next turn", result.getNextTurn());
        assertEquals("Invalid winner", "IT'S A TIE!!", result.getWinner());
    }

    @Test
    public void testPlayWithMoreThanSixStones() throws BoardNotFoundException {
        Board board = createBoardForMoreThanSixStones();
        ResourceStore.saveBoard(board);
        Board result = kalahService.play(board.getBoardId(), 6);
        assertEquals("Invalid board id", board.getBoardId(), result.getBoardId());
        assertEquals("Invalid no. of stones in kalah", 1, result.getNorth().getKalah().getStones());
        assertEquals("Invalid no. of stones in opponent's kalah", 0, result.getSouth().getKalah().getStones());
        assertEquals("Invalid no. of stones in current player's selected pit", 0, result.getNorth().getPits().get(5).getStones());
        assertEquals("Invalid no. of stones in last pit after sow", 7, result.getNorth().getPits().get(2).getStones());
    }

    private Board createBoardForCapture() {
        Board board = new Board();
        // keep 1 stone in first pit
        board.getNorth().getPits().get(0).setStones(1);
        // remove all stones from second pit
        board.getNorth().getPits().get(1).setStones(0);
        return board;
    }

    private Board createBoardForCollectAllStones() {
        Board board = new Board();
        // Remove all stones from all pits
        board.getNorth().getPits().forEach(pit -> pit.setStones(0));
        // keep 1 stone in last pit
        board.getNorth().getPits().get(5).setStones(1);
        return board;
    }

    private Board createBoardForTurnAgain() {
        Board board = new Board();
        // keep 1 stone in last pit
        board.getNorth().getPits().get(5).setStones(1);
        return board;
    }

    private Board createBoardForWinner() {
        Board board = new Board();
        // keep 1 stone in last pit
        board.getNorth().getPits().get(5).setStones(1);
        // set kalah to 36 stones
        board.getNorth().getKalah().setStones(36);
        return board;
    }

    private Board createBoardForTie() {
        Board board = new Board();
        // keep 1 stone in last pit
        board.getNorth().getPits().get(5).setStones(1);
        // set kalah to 36 stones
        board.getNorth().getKalah().setStones(35);
        // set kalah for opponent to 36 stones
        board.getSouth().getKalah().setStones(36);
        return board;
    }

    private Board createBoardForMoreThanSixStones() {
        Board board = new Board();
        // keep 10 stones in last pit
        board.getNorth().getPits().get(5).setStones(10);
        return board;
    }
}
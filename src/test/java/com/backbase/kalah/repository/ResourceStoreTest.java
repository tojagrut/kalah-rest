package com.backbase.kalah.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.backbase.kalah.model.Board;

/**
 * Test class for {@link ResourceStore}
 * Created by tojagrut
 */
public class ResourceStoreTest {

    @Test
    public void testSaveAndGetBoard() throws Exception {
        Board board = new Board();
        ResourceStore.saveBoard(board);
        assertEquals("Invalid board id", board.getBoardId(), ResourceStore.getBoard(board.getBoardId()).getBoardId());
    }
}
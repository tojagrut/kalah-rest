package com.backbase.kalah.repository;

import java.util.HashMap;
import java.util.Map;

import com.backbase.kalah.model.Board;

/**
 * Class which maintains all initliazed boards of kalah
 * Created by tojagrut
 */
public final class ResourceStore {

    private static Map<String, Board> boards = new HashMap<>();

    private ResourceStore() {
        // preventing instantiation of this class
    }
    /**
     * Save board to resource store
     * @param board board
     */
    public static void saveBoard(Board board) {
        boards.put(board.getBoardId(), board);
    }

    /**
     * Retrieve board using id from resource store
     * @param id id
     * @return Board
     */
    public static Board getBoard(String id) {
        return boards.get(id);
    }
}

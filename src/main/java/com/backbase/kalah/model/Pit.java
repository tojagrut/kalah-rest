package com.backbase.kalah.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

/**
 * Model class for Pit
 * Created by tojagrut
 */
@Data
public class Pit {
    private int id;
    private int stones;

    public Pit() {
        // default constructor
    }
    /**
     * Initialize pit
     * @param id id
     * @param stones stones
     */
    public Pit(int id, int stones) {
        this.id = id;
        this.stones = stones;
    }

    /**
     * Add given no. of stones
     * @param stones stones
     */
    public void addStones(int stones) {
        this.stones += stones;
    }

    /**
     * Checks if stones are available
     * @return true if available
     */
    @JsonIgnore
    public boolean isStoneAvailable() {
        return stones > 0;
    }
}

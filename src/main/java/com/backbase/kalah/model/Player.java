package com.backbase.kalah.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import com.backbase.kalah.util.Constants;

import lombok.Data;

/**
 * Model class for Player
 * Created by tojagrut
 */
@Data
public class Player {
    private String name;
    private List<Pit> pits;
    private Pit kalah;

    public Player(String name) {
        this.name = name;
        this.pits = initializePits();
        this.kalah = new Pit(0,0);
    }

    private List<Pit> initializePits() {
        List<Pit> pits = new ArrayList<>();
        IntStream.rangeClosed(1, Constants.MAX_PITS).forEach(index -> pits.add(new Pit(index, Constants.MAX_STONES)));
        return pits;
    }
}

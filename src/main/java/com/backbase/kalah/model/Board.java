package com.backbase.kalah.model;

import java.util.UUID;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

/**
 * Model class for Board
 * Created by tojagrut
 */
@JsonPropertyOrder({"id", "nextTurn"})
@Data
public class Board extends ResourceSupport {

    @JsonProperty("id")
    private String boardId;

    private Player north;
    private Player south;
    private String winner;
    private String nextTurn;

    public Board() {
        this.boardId = UUID.randomUUID().toString();
        this.north = new Player("North");
        this.south = new Player("South");
        this.winner = null;
        this.nextTurn = north.getName();
    }
}

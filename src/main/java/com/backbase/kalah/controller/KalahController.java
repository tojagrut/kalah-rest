package com.backbase.kalah.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.backbase.kalah.exception.BoardNotFoundException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.service.KalahService;
import com.backbase.kalah.validator.PitValidator;

/**
 * Controller for Kalah
 * Created by tojagrut
 */
@RestController
@RequestMapping("/kalah")
public class KalahController {

    private static final Logger LOGGER = LoggerFactory.getLogger(KalahController.class);

    @Autowired
    private KalahService kalahService;

    /**
     * Binds validator for Pit
     * @param webDataBinder webDataBinder
     */
    @InitBinder
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.setValidator(new PitValidator());
    }

    /**
     * Initializes a new board
     * @return Board
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Board initialize() throws Exception {
        Board board = kalahService.createBoard();
        createLinks(board);
        return board;
    }

    private void createLinks(Board board) throws Exception {
        board.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(KalahController.class).play(board.getBoardId(), null)).withRel("play"));
        board.add(ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(KalahController.class).retrieve(board.getBoardId())).withSelfRel());
    }

    /**
     * Retrieves current status of Board
     * @param id id of the board
     * @return Board
     * @throws Exception When board not found
     */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Board retrieve(@PathVariable String id) throws Exception {
        Board board = kalahService.retrieveBoard(id);
        return board;
    }

    /**
     * Processes the player's move
     * @param id id of the board
     * @param pit player's selected pit
     * @return Board
     * @throws Exception When board not found
     */
    @PutMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Board play(@PathVariable String id, @Valid @RequestBody Pit pit) throws Exception {
        Board board = kalahService.play(id, pit.getId());
        return board;
    }

    /**
     * Handles BoardNotFoundException
     * @param exception exception
     */
    @ExceptionHandler(BoardNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleBoardNotFoundException(BoardNotFoundException exception) {
        LOGGER.error("Exception occurred {}", exception);
    }
}

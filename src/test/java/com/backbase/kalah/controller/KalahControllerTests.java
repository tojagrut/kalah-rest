/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.backbase.kalah.controller;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.backbase.kalah.exception.BoardNotFoundException;
import com.backbase.kalah.model.Board;
import com.backbase.kalah.model.Pit;
import com.backbase.kalah.service.KalahService;

/**
 * Test class for {@link KalahController}
 */
@RunWith(MockitoJUnitRunner.class)
public class KalahControllerTests {

    @InjectMocks
    private KalahController kalahController;

    @Mock
    private KalahService kalahService;

    private MockHttpServletRequest request;

    private Board board;

    @Before
    public void setup() {
        board = new Board();
        request = new MockHttpServletRequest();
        ServletRequestAttributes requestAttributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(requestAttributes);

        when(kalahService.createBoard()).thenReturn(board);
        when(kalahService.retrieveBoard(any(String.class))).thenReturn(board);
        when(kalahService.play(any(String.class), any(int.class))).thenReturn(board);
    }

    @Test
    public void testInitialize() throws Exception {
        Board result = kalahController.initialize();
        assertNotNull("Invalid board", result);
    }

    @Test
    public void testRetrieve() throws Exception {
        Board result = kalahController.retrieve(board.getBoardId());
        assertNotNull("Invalid board", result);
    }

    @Test(expected = BoardNotFoundException.class)
    public void testRetrieveWithException() throws Exception {
        when(kalahService.retrieveBoard("abc1234")).thenThrow(new BoardNotFoundException("board not found"));
        kalahController.retrieve("abc1234");
    }

    @Test
    public void testPlay() throws Exception {
        Board result = kalahController.play(board.getBoardId(), new Pit(1,1));
        assertNotNull("Invalid board", result);
    }
}

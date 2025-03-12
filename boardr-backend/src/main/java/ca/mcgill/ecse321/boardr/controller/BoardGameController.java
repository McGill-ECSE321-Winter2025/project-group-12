package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameDTO;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.service.BoardGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller class for managing Board Game in the Boardr application.
 * Provides endpoints for creating, deleting and viewing all board game instances.
 * This class interacts with the BoardGameService to perform operations related to events.
 * 
 * REST APIs:
 * - POST /boardgames: Create a new board game 
 * - DELETE /boardgames/{Id}: Delete a board game by its ID
 * - GET /boardgames: Retrieve all available board game 
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-11
 * 
 */

@RestController
@RequestMapping("/boardgames")
public class BoardGameController {

    @Autowired
    private BoardGameService boardGameService;

    @GetMapping
    public List<BoardGameDTO> getAllBoardGames() {
        return boardGameService.getAllBoardGames().stream()
                .map(game -> new BoardGameDTO(game.getGameId(), game.getName(), game.getDescription()))
                .collect(Collectors.toList());
    }

    @PostMapping
    public BoardGameDTO createBoardGame(@RequestBody BoardGameDTO dto) {
        BoardGame boardGame = boardGameService.createBoardGame(dto.getName(), dto.getDescription());
        return new BoardGameDTO(boardGame.getGameId(), boardGame.getName(), boardGame.getDescription());
    }

    // 2. Remove a game 
    @DeleteMapping("/{id}")
    public void removeBoardGame(@PathVariable int id) {
        boardGameService.removeBoardGame(id);
    }
}

package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameDTO;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.service.BoardGameInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST controller class for managing Board Game Instances in the Boardr application.
 * Provides endpoints for creating, deleting, borrowing and view all board game instances.
 * This class interacts with the BoardGameInstanceService to perform operations related to events.
 * 
 * REST APIs:
 * - POST /boardgameinstances: Create a new board game instance
 * - DELETE /boardgameinstances/{Id}: Delete a board game instance by its ID
 * - GET /boardgameinstances: Retrieve all available board game instances
 * - POST /boardgameinstances/{id}/borrow: borrow a board game instance
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-11
 * 
 */

@RestController
@RequestMapping("/boardgameinstances")
public class BoardGameInstanceController {

    @Autowired
    private BoardGameInstanceService boardGameInstanceService;

    @GetMapping
    public List<BoardGameInstanceDTO> getAllBoardGameInstances() {
        return boardGameInstanceService.getAllBoardGameInstances().stream()
                .map(instance -> new BoardGameInstanceDTO(
                        instance.getindividualGameId(),
                        instance.getCondition(),
                        instance.isAvailable(),
                        instance.getGameOwner().getId(),
                        instance.getBoardGame().getGameId()))
                .collect(Collectors.toList());
    }

    // 1. Add a game instance
    @PostMapping
    public BoardGameInstanceDTO addBoardGameInstance(@RequestBody BoardGameInstanceDTO dto) {
        BoardGameInstance instance = boardGameInstanceService.createBoardGameInstance(dto.getBoardGameId(), dto.getGameOwnerId(), dto.getCondition());
        return new BoardGameInstanceDTO(instance.getindividualGameId(), instance.getCondition(), instance.isAvailable(), instance.getGameOwner().getId(), instance.getBoardGame().getGameId());
    }

    // 2. Remove a game instance
    @DeleteMapping("/{id}")
    public void removeBoardGameInstance(@PathVariable int id) {
        boardGameInstanceService.removeBoardGameInstance(id);
    }

    // 3. Borrow a game instance
    @PostMapping("/{id}/borrow")
    public void borrowBoardGameInstance(@PathVariable int id) {
        boardGameInstanceService.borrowBoardGameInstance(id);
    }
}

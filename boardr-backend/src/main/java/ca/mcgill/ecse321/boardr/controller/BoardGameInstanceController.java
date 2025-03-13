package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.*;
import ca.mcgill.ecse321.boardr.dto.BoardGame.*;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.service.BoardGameInstanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
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
public class BoardGameInstanceController {

    @Autowired
    private BoardGameInstanceService boardGameInstanceService;

    // 1. Get all board game instances
    @GetMapping("/boardgameinstances")
    public List<BoardGameInstanceResponseDTO> getAllBoardGameInstances() {
        return boardGameInstanceService.getAllBoardGameInstances();
    }

    // 2. Create a board game instance
    @PostMapping("/boardgameinstances")
    @ResponseStatus(HttpStatus.CREATED)
    public BoardGameInstanceResponseDTO addBoardGameInstance(@RequestBody BoardGameInstanceCreationDTO dto) {
        return boardGameInstanceService.createBoardGameInstance(dto);
    }

    // 3. Remove a board game instance
    @DeleteMapping("/boardgameinstances/{id}")
    public void removeBoardGameInstance(@PathVariable int id) {
        boardGameInstanceService.removeBoardGameInstance(id);
    }
}
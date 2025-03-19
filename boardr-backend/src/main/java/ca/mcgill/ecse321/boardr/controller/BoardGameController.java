package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.BoardGame.*;
import ca.mcgill.ecse321.boardr.service.BoardGameService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import java.util.List;

/**
 * REST controller class for managing Board Game in the Boardr application.
 * Provides endpoints for creating, deleting and viewing all board game instances.
 * This class interacts with the BoardGameService to perform operations related to events.
 * 
 * REST APIs:
 * - GET /boardgames: Retrieve all available board game 
 * - GET /boardgames/ {id}: Retrieve all available board game 
 * - POST /boardgames: Create a new board game 
 * - DELETE /boardgames/{id}: Delete a board game by its ID
 * 
 * @author Jun Ho
 * @version 1.0
 * @since 2025-03-11
 * 
 */

 @RestController
 public class BoardGameController {
 
     @Autowired
     private BoardGameService boardGameService;
 
     // 1.Get all board games
     @GetMapping("/boardgames")
     public ResponseEntity<?> getAllBoardGames() {
         return ResponseEntity.ok().body(boardGameService.getAllBoardGames());
     }
 
     // 2.Get a specific board game by ID
     @GetMapping("/boardgames/{id}")
     public BoardGameResponseDTO getBoardGameById(@PathVariable int id) {
         return boardGameService.getBoardGameById(id);
     }
 
     // 3.Create a board game
     @PostMapping("/boardgames")
     @ResponseStatus(HttpStatus.CREATED)
     public BoardGameResponseDTO createBoardGame(@RequestBody BoardGameCreationDTO dto) {
         return boardGameService.createBoardGame(dto);
     }
 
     // 4.Remove a board game with specified ID
     @DeleteMapping("/boardgames/{id}")
     public ResponseEntity<?> removeBoardGame(@PathVariable int id) {
         boardGameService.removeBoardGame(id);
         return ResponseEntity.ok().body("Board game has been deleted successfully");
     }
 }
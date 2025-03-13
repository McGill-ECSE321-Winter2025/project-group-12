package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.dto.BoardGame.*;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service class for managing board games in the Boardr application.
 * Provides methods for creating, deleting, updating and retrieving board games.
 * This class interacts with the BoardGameRepository
 * to perform operations related to board games.
 * @author Jun Ho Oh
 * @version 1.0
 * @since 2025-03-11
 */

 @Service
 @Validated
 public class BoardGameService {
     @Autowired
     private BoardGameRepository boardGameRepository;
 
     // 1. Retrieve all board games
     public List<BoardGameResponseDTO> getAllBoardGames() {
         return StreamSupport.stream(boardGameRepository.findAll().spliterator(), false)
                 .map(BoardGameResponseDTO::new)
                 .collect(Collectors.toList());
     }
 
     // 2. Retrieve a single board game by ID
     public BoardGameResponseDTO getBoardGameById(int id) {
         BoardGame boardGame = boardGameRepository.findById(id)
                 .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Board Game not found"));
         return new BoardGameResponseDTO(boardGame);
     }
 
     // 3. Create a new board game
     @Transactional
     public BoardGameResponseDTO createBoardGame(@Valid BoardGameCreationDTO dto) {
         BoardGame boardGame = boardGameRepository.save(new BoardGame(dto.getName(), dto.getDescription()));
         return new BoardGameResponseDTO(boardGame);
     }
 
     // 4. Remove a board game
     @Transactional
     public void removeBoardGame(int id) {
         if (!boardGameRepository.existsById(id)) {
             throw new BoardrException(HttpStatus.NOT_FOUND, "Board Game not found");
         }
         boardGameRepository.deleteById(id);
     }
 }
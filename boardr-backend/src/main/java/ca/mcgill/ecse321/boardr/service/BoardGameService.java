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

    // 1.Retrieve all board games - Response DTO
    public List<BoardGameResponseDTO> getAllBoardGames() {
        return StreamSupport.stream(boardGameRepository.findAll().spliterator(), false)
                .map(BoardGameResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 2.Retrieve a single board game by ID - Response DTO
    public BoardGameResponseDTO getBoardGameById(int id) {
        BoardGame boardGame = boardGameRepository.findById(id)
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Board Game not found"));
        return new BoardGameResponseDTO(boardGame);
    }

    // 3.Create a new board game - Creation DTO then response DTO for output
    @Transactional
    public BoardGameResponseDTO createBoardGame(@Valid BoardGameCreationDTO dto) {
        // Additional manual validations
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BoardrException(HttpStatus.BAD_REQUEST, "Board game name cannot be empty");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new BoardrException(HttpStatus.BAD_REQUEST, "Board game description cannot be empty");
        }

        String trimmedName = dto.getName().trim();
        Optional<BoardGame> optionalBoardGame = boardGameRepository.findByName(trimmedName);
        if (optionalBoardGame.isPresent()) {
            // Throwing an error causes the frontend to display a failed popup.
            throw new BoardrException(HttpStatus.BAD_REQUEST, "A board game with the same name already exists");
        }
        
        BoardGame boardGame = new BoardGame(trimmedName, dto.getDescription().trim());
        boardGame = boardGameRepository.save(boardGame);
        return new BoardGameResponseDTO(boardGame);
    }

    // 4.Remove a board game
    @Transactional
    public void removeBoardGame(int id) {
        if (!boardGameRepository.existsById(id)) {
            throw new BoardrException(HttpStatus.NOT_FOUND, "Board Game not found");
        }
        boardGameRepository.deleteById(id);
    }
}
package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class BoardGameService {
    @Autowired
    private BoardGameRepository boardGameRepository;


    public List<BoardGame> getAllBoardGames() {
        return StreamSupport.stream(boardGameRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    public Optional<BoardGame> getBoardGameById(int id) {
        return boardGameRepository.findById(id);
    }

    public BoardGame createBoardGame(String name, String description) {
        return boardGameRepository.save(new BoardGame(name, description));
    }

    public BoardGame updateBoardGame(int id, String description) {
        Optional<BoardGame> boardGame = boardGameRepository.findById(id);
        if (boardGame.isPresent()) {
            BoardGame updatedGame = boardGame.get();
            updatedGame.setDescription(description);
            return boardGameRepository.save(updatedGame);
        }
        throw new IllegalArgumentException("Board Game not found");
    }

    public void deleteBoardGame(int id) {
        boardGameRepository.deleteById(id);
    }

    
}

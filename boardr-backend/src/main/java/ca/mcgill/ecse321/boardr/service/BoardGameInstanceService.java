package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.model.GameOwner;
import ca.mcgill.ecse321.boardr.repository.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repository.BoardGameRepository;
import ca.mcgill.ecse321.boardr.repository.GameOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BoardGameInstanceService {

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BoardGameRepository boardGameRepository;

    @Autowired
    private GameOwnerRepository gameOwnerRepository;

    public List<BoardGameInstance> getAllBoardGameInstances() {
        return boardGameInstanceRepository.findAll();
    }

    public Optional<BoardGameInstance> getBoardGameInstanceById(int id) {
        return boardGameInstanceRepository.findById(id);
    }

    public BoardGameInstance createBoardGameInstance(int boardGameId, int gameOwnerId, String condition) {
        BoardGame boardGame = boardGameRepository.findById(boardGameId)
                .orElseThrow(() -> new IllegalArgumentException("Board Game not found"));

        GameOwner gameOwner = gameOwnerRepository.findById(gameOwnerId)
                .orElseThrow(() -> new IllegalArgumentException("Game Owner not found"));

        return boardGameInstanceRepository.save(new BoardGameInstance(boardGame, gameOwner, condition));
    }

    public void deleteBoardGameInstance(int id) {
        boardGameInstanceRepository.deleteById(id);
    }


    
}

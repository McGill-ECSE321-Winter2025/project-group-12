package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class BoardGameService {
    @Autowired
    private BoardGameRepository boardGameRepository;

    public BoardGame findBoardGameInstanceByIndividualGameId(int id) {
        return boardGameRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no boardGame with ID " + id + "."));
    }

}
//TODO
//find all use cases relating to this classService, then implement those methods
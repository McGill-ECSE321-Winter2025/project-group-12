package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.BoardGame.BoardGameDTO;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.service.BoardGameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

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
}

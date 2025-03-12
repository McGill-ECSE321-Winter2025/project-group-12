package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.service.BoardGameService;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

public class BoardGameController {
    
    private final BoardGameService boardGameService;

    @Autowired
    public BoardGameController(BoardGameService boardGameService) {
        this.boardGameService = boardGameService;
    }

    @PostMapping
    @RequiredUser
    public ResponseEntity<BoardGameResponse> createBoardGame (
        @RequestBody CreateBoardGame request
    ) {
        boardGameService.createBoardGame(request);
        return ResponseEntity.ok().build();
    }

}

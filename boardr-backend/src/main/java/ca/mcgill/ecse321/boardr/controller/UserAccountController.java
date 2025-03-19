package ca.mcgill.ecse321.boardr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountResponseDTO;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestResponseDTO;
import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.*;

import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @PostMapping
    public ResponseEntity<UserAccountResponseDTO> createUser(@RequestBody UserAccountCreationDTO userDTO) {
        UserAccount createdUser = userAccountService.createUser(userDTO);
        return ResponseEntity.ok(new UserAccountResponseDTO(createdUser));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountResponseDTO> getUserById(@PathVariable int id) {
        UserAccount user = userAccountService.getUserById(id);
        return ResponseEntity.ok(new UserAccountResponseDTO(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserAccountResponseDTO> getUserByEmail(@PathVariable String email) {
        UserAccount user = userAccountService.getUserByEmail(email);
        return ResponseEntity.ok(new UserAccountResponseDTO(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody UserAccountResponseDTO userDto) {
        userAccountService.updateUser(id, userDto.getName(), userDto.getEmail(), userDto.getPassword());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UserAccountResponseDTO>> getAllUsers() {
        List<UserAccount> allUsers = userAccountService.getAllUsers();
        return ResponseEntity.ok(allUsers.stream()
                .map(UserAccountResponseDTO::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{gameOwnerId}/owned-games")
    public ResponseEntity<List<BoardGameInstanceResponseDTO>> getOwnedGames(@PathVariable int gameOwnerId) {
        List<BoardGameInstance> ownedGames = userAccountService.getOwnedGames(gameOwnerId);
        return ResponseEntity.ok(ownedGames.stream()
                .map(BoardGameInstanceResponseDTO::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{userId}/borrowed-games")
    public ResponseEntity<List<BoardGameInstanceResponseDTO>> getBorrowedGames(@PathVariable int userId) {
        List<BoardGameInstance> borrowedGames = userAccountService.getBorrowedGames(userId);
        return ResponseEntity.ok(borrowedGames.stream()
                .map(BoardGameInstanceResponseDTO::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{gameOwnerId}/lending-history")
    public ResponseEntity<List<BorrowRequestResponseDTO>> getLendingHistory(@PathVariable int gameOwnerId) {
        List<BorrowRequest> lendingHistory = userAccountService.getLendingHistoryByGameOwnerId(gameOwnerId);
        return ResponseEntity.ok(lendingHistory.stream()
                .map(BorrowRequestResponseDTO::new)
                .collect(Collectors.toList()));
    }
}
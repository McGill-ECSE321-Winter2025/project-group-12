package ca.mcgill.ecse321.boardr.service;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BorrowRequestRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.springframework.http.HttpStatus;


@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @Autowired
    private BorrowRequestRepository borrowRequestRepository;



    @Transactional
    public UserAccount createUser(@Valid UserAccountCreationDTO userAccountToCreate) {
        Optional<UserAccount> existingUserWithSameEmail =
                userAccountRepository.findByEmail(userAccountToCreate.getEmail());

        // Check for duplicate email
        if (existingUserWithSameEmail.isPresent()) {
            throw new BoardrException(HttpStatus.CONFLICT, "Email is already in use.");
        }

        // Create and save user
        UserAccount user = new UserAccount(
            userAccountToCreate.getName(),
            userAccountToCreate.getEmail(),
            userAccountToCreate.getPassword()
        );
        return userAccountRepository.save(user);
    }

    @Transactional
    public UserAccount getUserById(int id) {
        return userAccountRepository.findById(id)
            .orElseThrow(() ->
                new BoardrException(HttpStatus.NOT_FOUND, "No userAccount has ID " + id)
            );
    }

    @Transactional
    public UserAccount getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email)
            .orElseThrow(() ->
                new BoardrException(HttpStatus.NOT_FOUND, "No userAccount has email " + email)
            );
    }

    @Transactional
    public void deleteUser(int id) {
        userAccountRepository.deleteById(id);
    }

    @Transactional
    public void updateUser(int id, String name, String email, String password) {
        UserAccount user = userAccountRepository.findById(id)
            .orElseThrow(() ->
                new BoardrException(HttpStatus.NOT_FOUND, "No user found with ID " + id)
            );

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new BoardrException(HttpStatus.BAD_REQUEST, "Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new BoardrException(HttpStatus.BAD_REQUEST, "Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new BoardrException(HttpStatus.BAD_REQUEST, "Password cannot be empty");
        }

        // Check if the new email is taken by a different user
        UserAccount userWithSameEmail = userAccountRepository.findByEmail(email).orElse(null);
        if (userWithSameEmail != null && userWithSameEmail.getUserAccountId() != id) {
            throw new BoardrException(HttpStatus.CONFLICT, "Email is already in use.");
        }

        // Update and save
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userAccountRepository.save(user);
    }


    @Transactional
    public List<UserAccount> getAllUsers() {
        return StreamSupport.stream(userAccountRepository.findAll().spliterator(), false)
            .collect(Collectors.toList());
    }

    @Transactional
    public List<BoardGameInstance> getOwnedGames(int gameOwnerId){

        return boardGameInstanceRepository.findAllByGameOwnerId(gameOwnerId);

    }

    @Transactional
    public List<BoardGameInstance> getBorrowedGames (int borrowerId){
    
    return borrowRequestRepository.findAcceptedBoardGameInstancesByBorrower(borrowerId);
    }


    @Transactional
    public List<BorrowRequest> getLendingHistoryByGameOwnerId(int gameOwnerId) {
        return borrowRequestRepository.findAllBorrowRequestsByGameOwner(gameOwnerId);
    }
}

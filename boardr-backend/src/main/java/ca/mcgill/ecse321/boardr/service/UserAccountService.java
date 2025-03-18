package ca.mcgill.ecse321.boardr.service;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@Service
public class UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Transactional
    public UserAccount createUser(@Valid UserAccountCreationDTO userAccountToCreate) {
        Optional<UserAccount> existingUserWithSameEmail =
        userAccountRepository.findByEmail(userAccountToCreate.getEmail());
    
    if (existingUserWithSameEmail.isPresent()) {
        throw new IllegalArgumentException("Email is already in use.");
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
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No userAccount has ID %d", id)));
    }

    @Transactional
    public UserAccount getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No userAccount has email %s", email)));
    }

    @Transactional
    public void deleteUser(int id) {
        userAccountRepository.deleteById(id);
    }

    @Transactional
    public void updateUser(int id, String name, String email, String password) {
        UserAccount user = userAccountRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("No user found with ID %d", id)));

        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        UserAccount userWithSameEmail = userAccountRepository.findByEmail(email).orElse(null);
        if (userWithSameEmail != null && userWithSameEmail.getUserAccountId() != id) {
            throw new IllegalArgumentException("Email is already in use.");
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


}

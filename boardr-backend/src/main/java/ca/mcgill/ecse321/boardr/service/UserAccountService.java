package ca.mcgill.ecse321.boardr.service;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class UserAccountService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    public UserAccount createUser(String name, String email, String password) {
        UserAccount user = new UserAccount(name, email, password);
        return userAccountRepository.save(user);
    }

    public UserAccount getUserById(int id) {
        return userAccountRepository.findById(id).orElse(null);
    }

    public UserAccount getUserByEmail(String email) {
        return userAccountRepository.findByEmail(email).orElse(null);
    }

    public void deleteUser(int id) {
        userAccountRepository.deleteById(id);
    }

    public UserAccount updateUser(int id, String name, String email) {
        UserAccount user = userAccountRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        user.setName(name);
        user.setEmail(email);
        // Does saving again make sense?
        return userAccountRepository.save(user);
    }

   
}
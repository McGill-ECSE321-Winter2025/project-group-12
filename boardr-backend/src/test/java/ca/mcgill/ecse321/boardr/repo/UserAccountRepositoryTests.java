package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.sql.Date;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.UserAccount;

@SpringBootTest
public class UserAccountRepositoryTests {

    @Autowired
    private UserAccountRepository repo;

    @AfterEach
    public void clearDatabase() {
        repo.deleteAll();
    }
    
    @Test
    public void testCreateandReadUserAccount() {
        
        // Arrange
        UserAccount userAccount = new UserAccount("testUser", "testuser@mail.mcgill.ca", "password");
        userAccount = repo.save(userAccount);

        // Act
        UserAccount retrievedUserAccount = repo.findByUserAccountId(userAccount.getUserAccountId());

        // Assert
        assertNotNull(retrievedUserAccount);
        assertEquals(userAccount.getUserAccountId(), retrievedUserAccount.getUserAccountId());
        assertEquals(userAccount.getCreationDate(), retrievedUserAccount.getCreationDate());
        assertEquals(userAccount.getEmail(), retrievedUserAccount.getEmail());
        assertEquals(userAccount.getName(), retrievedUserAccount.getName());
        assertEquals(userAccount.getPassword(), retrievedUserAccount.getPassword());
    
    }            
    
}

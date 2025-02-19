package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

        // assertEquals(userAccount.getUserRole(), retrievedUserAccount.getUserRole());
    
    }            
    
    @Test
    public void testDeleteUserAccount() {
        
        // Arrange
        UserAccount userAccount = new UserAccount("testUser", "testuser@mail.mcgill.ca", "password");
        userAccount = repo.save(userAccount);

        // Act
        repo.delete(userAccount);
        boolean exists = repo.existsById(userAccount.getUserAccountId());

        // Assert
        assertFalse(exists);
    }

}

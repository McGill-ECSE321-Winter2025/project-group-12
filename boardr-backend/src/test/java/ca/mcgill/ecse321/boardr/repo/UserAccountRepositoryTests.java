package ca.mcgill.ecse321.boardr.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import ca.mcgill.ecse321.boardr.model.UserAccount;

/**
 * Integration Tests for the {@link UserAccountRepository}.
 * Ensures {@link UserAccount} entities are correctly persisted, retrieved, and deleted.
 * 
 * Test Scenario: Verifies the creation, retrieval, and deletion of {@link UserAccount} entities.
 * 
 * Setup:
 * - Uses @SpringBootTest to load the full Spring context for integration testing.
 * - Uses @Autowired to inject the repository instance for the {@link UserAccount}.
 * - Utilizes @AfterEach annotation to clear the repository after each test to maintain a fresh state.
 * 
 * Test Cases:
 * 1. testCreateandReadUserAccount
 * 2. testDeleteUserAccount
 * 
 * Dependencies:
 * - Gradle
 * - Jakarta Persistence
 * - Spring Boot
 * 
 * Author: Yoon, Junho
 * Version: 1.0
 */

@SpringBootTest
public class UserAccountRepositoryTests {

    @Autowired
    private UserAccountRepository repo;

    /**
     * Clears the repository after each test to ensure a fresh state.
     */

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
    
    /**
     * Tests the creation and retrieval of a {@link UserAccount} entity.
     *
     * Steps:
     * 1. Create a {@link UserAccount} with a username, email, and password.
     * 2. Save the {@link UserAccount} to the repository.
     * 3. Retrieve the {@link UserAccount} by its ID.
     * 4. Verify that the retrieved user account matches the original in all attributes.
     *
     * Assertions:
     * - All attributes of the retrieved user account should match the original user account.
     */

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

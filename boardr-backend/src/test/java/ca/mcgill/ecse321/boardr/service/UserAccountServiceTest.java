    package ca.mcgill.ecse321.boardr.service;

    import ca.mcgill.ecse321.boardr.model.UserAccount;
    import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
    import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
    import org.junit.jupiter.api.AfterEach;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;

    import java.util.Optional;

    import static org.junit.jupiter.api.Assertions.*;

    /**
     * Unit tests for UserAccountService.
     *
     * Methods tested:
     * createUser,
     * getUserById,
     * getUserByEmail,
     * deleteUser,
     * updateUser
     *

     * @author Eric Deng
     * @version 1.0
     * @since 2025-03-16
     */

    @SpringBootTest
    public class UserAccountServiceTest {

        @Autowired
        private UserAccountService userAccountService;

        @Autowired
        private UserAccountRepository userAccountRepository;

        @BeforeEach
        @AfterEach
        public void clearDatabase() {
            userAccountRepository.deleteAll();
        }

        @Test
        public void testCreateUser() {
            String testName = "John Doe";
            String testEmail = "john.doe@example.com";
            String testPassword = "securePassword";

            UserAccountCreationDTO userAccountCreationDTO = new UserAccountCreationDTO();
            userAccountCreationDTO.setName(testName);
            userAccountCreationDTO.setEmail(testEmail);
            userAccountCreationDTO.setPassword(testPassword);

            UserAccount createdUser = userAccountService.createUser(userAccountCreationDTO);

            assertNotNull(createdUser);
            assertEquals(testName, createdUser.getName());
            assertEquals(testEmail, createdUser.getEmail());
            assertEquals(testPassword, createdUser.getPassword());
        }

        @Test
        public void testGetUserById() {
            String testName = "Jane Doe";
            String testEmail = "jane.doe@example.com";
            String testPassword = "anotherPassword";

            UserAccountCreationDTO userAccountCreationDTO = new UserAccountCreationDTO();
            userAccountCreationDTO.setName(testName);
            userAccountCreationDTO.setEmail(testEmail);
            userAccountCreationDTO.setPassword(testPassword);

            UserAccount createdUser = userAccountService.createUser(userAccountCreationDTO);

            UserAccount fetchedUser = userAccountService.getUserById(createdUser.getUserAccountId());

            assertNotNull(fetchedUser);
            assertEquals(testName, fetchedUser.getName());
            assertEquals(testEmail, fetchedUser.getEmail());
        }

        @Test
        public void testGetUserByEmail() {
            String testName = "Alice";
            String testEmail = "alice@example.com";
            String testPassword = "alicePassword";

            UserAccountCreationDTO userAccountCreationDTO = new UserAccountCreationDTO();
            userAccountCreationDTO.setName(testName);
            userAccountCreationDTO.setEmail(testEmail);
            userAccountCreationDTO.setPassword(testPassword);

            userAccountService.createUser(userAccountCreationDTO);

            UserAccount fetchedUser = userAccountService.getUserByEmail(testEmail);

            assertNotNull(fetchedUser);
            assertEquals(testName, fetchedUser.getName());
            assertEquals(testEmail, fetchedUser.getEmail());
        }

        @Test
        public void testDeleteUser() {
            String testName = "Bob";
            String testEmail = "bob@example.com";
            String testPassword = "bobPassword";

            UserAccountCreationDTO userAccountCreationDTO = new UserAccountCreationDTO();
            userAccountCreationDTO.setName(testName);
            userAccountCreationDTO.setEmail(testEmail);
            userAccountCreationDTO.setPassword(testPassword);

            UserAccount createdUser = userAccountService.createUser(userAccountCreationDTO);

            userAccountService.deleteUser(createdUser.getUserAccountId());

            Optional<UserAccount> deletedUser = userAccountRepository.findById(createdUser.getUserAccountId());
            assertFalse(deletedUser.isPresent());
        }

        @Test
        public void testUpdateUser() {
            String testName = "Charlie";
            String testEmail = "charlie@example.com";
            String testPassword = "charliePassword";

            UserAccountCreationDTO userAccountCreationDTO = new UserAccountCreationDTO();
            userAccountCreationDTO.setName(testName);
            userAccountCreationDTO.setEmail(testEmail);
            userAccountCreationDTO.setPassword(testPassword);

            UserAccount createdUser = userAccountService.createUser(userAccountCreationDTO);

            String updatedName = "Charlie Brown";
            String updatedEmail = "charlie.brown@example.com";
            String updatedPassword = "newCharliePassword";

            userAccountService.updateUser(createdUser.getUserAccountId(), updatedName, updatedEmail, updatedPassword);

            UserAccount updatedUser = userAccountService.getUserById(createdUser.getUserAccountId());

            assertEquals(updatedName, updatedUser.getName());
            assertEquals(updatedEmail, updatedUser.getEmail());
            assertEquals(updatedPassword, updatedUser.getPassword());
        }
    }
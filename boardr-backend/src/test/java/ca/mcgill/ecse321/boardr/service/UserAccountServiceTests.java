package ca.mcgill.ecse321.boardr.service;

import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserAccountServiceTests {

    @Mock
    private UserAccountRepository userAccountRepository;

    @InjectMocks
    private UserAccountService userAccountService;

    private UserAccount testUser;

    @BeforeEach
    void setup() {
        testUser = new UserAccount("Kyujin", "kyujin.chu@mail.mcgill.ca", "germane");
    }

    @Test
    void testCreateUser() {
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(testUser);

        UserAccount newUser = userAccountService.createUser("Kyujin", "kyujin.chu@mail.mcgill.ca", "germane");

        assertNotNull(newUser);
        assertEquals("Kyujin", newUser.getName());
        assertEquals("kyujin.chu@mail.mcgill.ca", newUser.getEmail());
        assertEquals("germane", newUser.getPassword());
        // Cant really test the date
        assertNotNull(newUser.getCreationDate());
        assertNotNull(newUser.getUserRole());
        verify(userAccountRepository, times(1)).save(any(UserAccount.class));
    }

    @Test
    void testGetUserById() {
        when(userAccountRepository.findById(1)).thenReturn(Optional.of(testUser));

        UserAccount currentUser = userAccountService.getUserById(1);

        assertNotNull(currentUser);
        assertEquals("Kyujin", currentUser.getName());
        assertEquals("kyujin.chu@mail.mcgill.ca", currentUser.getEmail());
        assertEquals("germane", currentUser.getPassword());
        // Cant really test the date
        assertNotNull(currentUser.getCreationDate());
        assertNotNull(currentUser.getUserRole());
        verify(userAccountRepository, times(1)).findById(1);
    }

    @Test
    void testGetUserByEmail() {
        when(userAccountRepository.findByEmail("kyujin.chu@mail.mcgill.ca")).thenReturn(Optional.of(testUser)); 


        UserAccount currentUser = userAccountService.getUserByEmail("kyujin.chu@mail.mcgill.ca");

        assertNotNull(currentUser);
        assertEquals("Kyujin", currentUser.getName());
        assertEquals("kyujin.chu@mail.mcgill.ca", currentUser.getEmail());
        assertEquals("germane", currentUser.getPassword());

        // Cant really test the dates
        assertNotNull(currentUser.getCreationDate());
        assertNotNull(currentUser.getUserRole());
        verify(userAccountRepository, times(1)).findByEmail("kyujin.chu@mail.mcgill.ca");
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userAccountRepository).deleteById(1);

        userAccountService.deleteUser(1);

        verify(userAccountRepository, times(1)).deleteById(1);
    }

}

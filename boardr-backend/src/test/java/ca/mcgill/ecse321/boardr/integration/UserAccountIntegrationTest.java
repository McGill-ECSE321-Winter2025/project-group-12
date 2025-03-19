package ca.mcgill.ecse321.boardr.integration;

import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountResponseDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrExceptionHandler;
import ca.mcgill.ecse321.boardr.model.*;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import ca.mcgill.ecse321.boardr.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardr.service.UserAccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import ca.mcgill.ecse321.boardr.dto.BoardGameInstance.BoardGameInstanceResponseDTO;
import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestResponseDTO;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.AdditionalMatchers.not;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;

/**
 * Integration tests for the UserAccount API endpoints.
 * Verifies that UserAccountController interacts correctly with UserAccountService.
 */
@SpringBootTest(classes = ca.mcgill.ecse321.boardr.BoardrApplication.class,
                webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(BoardrExceptionHandler.class)
public class UserAccountIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserAccountRepository userAccountRepository;

    @MockBean
    private BoardGameInstanceRepository boardGameInstanceRepository;

    @MockBean
    private BorrowRequestRepository borrowRequestRepository;

    @MockBean
    private UserAccountService userAccountService;

    private UserAccount testUser;
    private BoardGame testBoardGame;
    private BoardGameInstance testBoardGameInstance;
    private BorrowRequest testBorrowRequest;

    private static final int TEST_USER_ID = 1;
    private static final String TEST_NAME = "Test User";
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PASSWORD = "password123";
    private static final Date TEST_CREATION_DATE = Date.valueOf(LocalDate.now());

    @BeforeEach
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Setup test user
        testUser = new UserAccount(TEST_NAME, TEST_EMAIL, TEST_PASSWORD);
        setField(testUser, "userAccountId", TEST_USER_ID);

        // Setup test board game and instance
        testBoardGame = new BoardGame();
        setField(testBoardGame, "name", "Test Game");
        setField(testBoardGame, "description", "A test board game");

        GameOwner gameOwner = testUser.getUserRole().stream()
                .filter(role -> role instanceof GameOwner)
                .map(role -> (GameOwner) role)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("GameOwner role not found"));

        testBoardGameInstance = new BoardGameInstance(testBoardGame, gameOwner, "Good");

        // Setup test borrow request
        testBorrowRequest = new BorrowRequest(testBoardGameInstance, testUser, Date.valueOf("2025-03-10"), Date.valueOf("2025-03-20"));
        setField(testBorrowRequest, "status", BorrowRequest.RequestStatus.Accepted);

        // Mock service behaviors
        when(userAccountService.createUser(any(UserAccountCreationDTO.class))).thenReturn(testUser);
        when(userAccountService.getUserById(TEST_USER_ID)).thenReturn(testUser);
        when(userAccountService.getUserById(not(eq(TEST_USER_ID)))).thenThrow(new BoardrException(HttpStatus.NOT_FOUND, "No userAccount has ID"));
        when(userAccountService.getUserByEmail(TEST_EMAIL)).thenReturn(testUser);
        when(userAccountService.getUserByEmail(not(eq(TEST_EMAIL)))).thenThrow(new BoardrException(HttpStatus.NOT_FOUND, "No userAccount has email"));
        doNothing().when(userAccountService).deleteUser(TEST_USER_ID);
        doAnswer(invocation -> null).when(userAccountService).updateUser(eq(TEST_USER_ID), anyString(), anyString(), anyString());
        when(userAccountService.getAllUsers()).thenReturn(Collections.singletonList(testUser));
        when(userAccountService.getOwnedGames(TEST_USER_ID)).thenReturn(List.of(testBoardGameInstance));
        when(userAccountService.getBorrowedGames(TEST_USER_ID)).thenReturn(List.of(testBoardGameInstance));
        when(userAccountService.getLendingHistoryByGameOwnerId(TEST_USER_ID)).thenReturn(List.of(testBorrowRequest));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    public void testCreateUserSuccess() throws Exception {
        UserAccountCreationDTO dto = new UserAccountCreationDTO();
        dto.setName(TEST_NAME);
        dto.setEmail(TEST_EMAIL);
        dto.setPassword(TEST_PASSWORD);
        String requestJson = objectMapper.writeValueAsString(dto);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserAccountResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountResponseDTO.class);
        assertEquals(TEST_USER_ID, response.getUserAccountId());
        assertEquals(TEST_NAME, response.getName());
        assertEquals(TEST_EMAIL, response.getEmail());
    }

    @Test
    public void testCreateUserDuplicateEmail() throws Exception {
        UserAccountCreationDTO dto = new UserAccountCreationDTO();
        dto.setName("New User");
        dto.setEmail(TEST_EMAIL);
        dto.setPassword("newpass");
        String requestJson = objectMapper.writeValueAsString(dto);

        when(userAccountService.createUser(any(UserAccountCreationDTO.class)))
                .thenThrow(new BoardrException(HttpStatus.CONFLICT, "Email is already in use."));

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("Email is already in use."));
    }

    @Test
    public void testGetUserByIdSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", TEST_USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserAccountResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountResponseDTO.class);
        assertEquals(TEST_USER_ID, response.getUserAccountId());
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/{id}", 999))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("No userAccount has ID"));
    }

    @Test
    public void testGetUserByEmailSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/email/{email}", TEST_EMAIL))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserAccountResponseDTO response = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountResponseDTO.class);
        assertEquals(TEST_EMAIL, response.getEmail());
    }

    @Test
    public void testGetUserByEmailNotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/users/email/{email}", "unknown@example.com"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("No userAccount has email"));
    }

    @Test
    public void testDeleteUserSuccess() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", TEST_USER_ID))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUpdateUserSuccess() throws Exception {
        UserAccountResponseDTO dto = new UserAccountResponseDTO();
        dto.setName("Updated Name");
        dto.setEmail("updated@example.com");
        dto.setPassword("newpassword");
        String requestJson = objectMapper.writeValueAsString(dto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    public void testUpdateUserEmptyName() throws Exception {
        UserAccountResponseDTO dto = new UserAccountResponseDTO();
        dto.setName("");
        dto.setEmail("updated@example.com");
        dto.setPassword("newpassword");
        String requestJson = objectMapper.writeValueAsString(dto);

        doThrow(new BoardrException(HttpStatus.BAD_REQUEST, "Name cannot be empty"))
                .when(userAccountService).updateUser(eq(TEST_USER_ID), eq(""), anyString(), anyString());

        mockMvc.perform(MockMvcRequestBuilders.put("/users/{id}", TEST_USER_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$.errors[0]").value("Name cannot be empty"));
    }

    @Test
    public void testGetAllUsersSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        UserAccountResponseDTO[] users = objectMapper.readValue(result.getResponse().getContentAsString(), UserAccountResponseDTO[].class);
        assertEquals(1, users.length);
        assertEquals(TEST_USER_ID, users[0].getUserAccountId());
    }

    @Test
    public void testGetOwnedGamesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{gameOwnerId}/owned-games", TEST_USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BoardGameInstanceResponseDTO[] games = objectMapper.readValue(result.getResponse().getContentAsString(), BoardGameInstanceResponseDTO[].class);
        assertEquals(1, games.length);
        assertEquals("Test Game", games[0].getBoardGameName());
    }

    @Test
    public void testGetBorrowedGamesSuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}/borrowed-games", TEST_USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BoardGameInstanceResponseDTO[] games = objectMapper.readValue(result.getResponse().getContentAsString(), BoardGameInstanceResponseDTO[].class);
        assertEquals(1, games.length);
        assertEquals("Test Game", games[0].getBoardGameName());
    }

    @Test
    public void testGetLendingHistorySuccess() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users/{gameOwnerId}/lending-history", TEST_USER_ID))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        BorrowRequestResponseDTO[] history = objectMapper.readValue(result.getResponse().getContentAsString(), BorrowRequestResponseDTO[].class);
        assertEquals(1, history.length);
        assertEquals(TEST_USER_ID, history[0].getUserAccountId());
    }
}
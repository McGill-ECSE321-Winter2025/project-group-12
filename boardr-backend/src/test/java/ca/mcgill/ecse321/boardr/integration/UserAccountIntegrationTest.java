// package ca.mcgill.ecse321.boardr.integration;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertIterableEquals;
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertTrue;

// import java.time.LocalDate;
// import java.util.List;
// import java.util.Set;

// import org.junit.jupiter.api.MethodOrderer;
// import org.junit.jupiter.api.Order;
// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.TestInstance;
// import org.junit.jupiter.api.TestInstance.Lifecycle;
// import org.junit.jupiter.api.TestMethodOrder;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.http.HttpEntity;
// import org.springframework.http.HttpMethod;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountCreationDTO;
// import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountResponseDTO;
// import ca.mcgill.ecse321.boardr.model.UserAccount;

// @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
// @TestInstance(Lifecycle.PER_CLASS)
// public class UserAccountIntegrationTest {

//     @Autowired
//     private TestRestTemplate client;

//     private int createdUserId;

//     private static final String VALID_NAME = "John Doe";
//     private static final String VALID_EMAIL = "john.doe@example.com";
//     private static final String VALID_PASSWORD = "securePass123";
//     private static final Set<String> EXPECTED_ROLES = Set.of("PLAYER", "GAMEOWNER");

//     @Test
//     @Order(0)
//     public void testCreateValidUser() {
//         // Arrange
//         UserAccountCreationDTO body = new UserAccountCreationDTO();
//         body.setName(VALID_NAME);
//         body.setEmail(VALID_EMAIL);
//         body.setPassword(VALID_PASSWORD);

//         // Act
//         ResponseEntity<UserAccountResponseDTO> response = client.postForEntity("/users", body, UserAccountResponseDTO.class);

//         // Assert
//         assertEquals(HttpStatus.OK, response.getStatusCode()); // Controller returns 200 OK
//         assertNotNull(response.getBody());
//         assertTrue(response.getBody().getUserAccountId() > 0, "The ID should be a positive int");
//         this.createdUserId = response.getBody().getUserAccountId();
//         assertEquals(VALID_NAME, response.getBody().getName());
//         assertEquals(VALID_EMAIL, response.getBody().getEmail());
//         assertEquals(VALID_PASSWORD, response.getBody().getPassword());
//         assertEquals(LocalDate.now(), response.getBody().getCreationDate().toLocalDate());
//         assertIterableEquals(EXPECTED_ROLES, response.getBody().getRoles(), "User should have PLAYER and GAMEOWNER roles");
//     }

//     @Test
//     @Order(1)
//     public void testCreateUserWithDuplicateEmail() {
//         // Arrange
//         UserAccountCreationDTO body = new UserAccountCreationDTO();
//         body.setName("Jane Smith");
//         body.setEmail(VALID_EMAIL); // Same email as above
//         body.setPassword("anotherPass456");

//         // Act
//         ResponseEntity<String> response = client.postForEntity("/users", body, String.class); // Expecting error as plain text

//         // Assert
//         assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//         assertNotNull(response.getBody());
//         assertTrue(response.getBody().contains("Email is already in use"), "Should indicate duplicate email error");
//     }

//     @Test
//     @Order(2)
//     public void testFindUserByValidId() {
//         // Arrange
//         String url = String.format("/users/%d", this.createdUserId);

//         // Act
//         ResponseEntity<UserAccountResponseDTO> response = client.getForEntity(url, UserAccountResponseDTO.class);

//         // Assert
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertNotNull(response.getBody());
//         assertEquals(this.createdUserId, response.getBody().getUserAccountId());
//         assertEquals(VALID_NAME, response.getBody().getName());
//         assertEquals(VALID_EMAIL, response.getBody().getEmail());
//         assertEquals(VALID_PASSWORD, response.getBody().getPassword());
//         assertEquals(LocalDate.now(), response.getBody().getCreationDate().toLocalDate());
//         assertIterableEquals(EXPECTED_ROLES, response.getBody().getRoles());
//     }

//     @Test
//     @Order(3)
//     public void testUpdateUser() {
//         // Arrange
//         String url = String.format("/users/%d", this.createdUserId);
//         UserAccountResponseDTO body = new UserAccountResponseDTO();
//         body.setName("John Updated");
//         body.setEmail("john.updated@example.com");
//         body.setPassword("newPass789");

//         // Act
//         ResponseEntity<Void> response = client.exchange(url, HttpMethod.PUT, new HttpEntity<>(body), Void.class);

//         // Assert
//         assertEquals(HttpStatus.OK, response.getStatusCode()); // Controller returns void, so expect 200 OK

//         // Verify update by fetching the user
//         ResponseEntity<UserAccountResponseDTO> getResponse = client.getForEntity(url, UserAccountResponseDTO.class);
//         assertEquals(HttpStatus.OK, getResponse.getStatusCode());
//         assertNotNull(getResponse.getBody());
//         assertEquals(this.createdUserId, getResponse.getBody().getUserAccountId());
//         assertEquals("John Updated", getResponse.getBody().getName());
//         assertEquals("john.updated@example.com", getResponse.getBody().getEmail());
//         assertEquals("newPass789", getResponse.getBody().getPassword());
//         assertIterableEquals(EXPECTED_ROLES, getResponse.getBody().getRoles());
//     }

//     @Test
//     @Order(4)
//     public void testGetAllUsers() {
//         // Act
//         ResponseEntity<UserAccountResponseDTO[]> response = client.getForEntity("/users", UserAccountResponseDTO[].class);

//         // Assert
//         assertEquals(HttpStatus.OK, response.getStatusCode());
//         assertNotNull(response.getBody());
//         List<UserAccountResponseDTO> users = List.of(response.getBody());
//         assertTrue(users.size() >= 1, "Should have at least one user");
//         UserAccountResponseDTO updatedUser = users.stream()
//                 .filter(u -> u.getUserAccountId() == this.createdUserId)
//                 .findFirst()
//                 .orElse(null);
//         assertNotNull(updatedUser);
//         assertEquals("John Updated", updatedUser.getName());
//         assertEquals("john.updated@example.com", updatedUser.getEmail());
//         assertEquals("newPass789", updatedUser.getPassword());
//         assertIterableEquals(EXPECTED_ROLES, updatedUser.getRoles());
//     }

//     @Test
//     @Order(5)
//     public void testDeleteUser() {
//         // Arrange
//         String url = String.format("/users/%d", this.createdUserId);

//         // Act
//         ResponseEntity<Void> response = client.exchange(url, HttpMethod.DELETE, null, Void.class);

//         // Assert
//         assertEquals(HttpStatus.OK, response.getStatusCode()); // Controller returns void, so expect 200 OK

//         // Verify deletion
//         ResponseEntity<String> getResponse = client.getForEntity(url, String.class);
//         assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
//         assertTrue(getResponse.getBody().contains("No user found with ID"), "Should indicate user not found");
//     }
// }

package ca.mcgill.ecse321.boardr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import ca.mcgill.ecse321.boardr.dto.UserAccount.UserAccountDTO;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.service.UserAccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    @Autowired
    UserAccountService userAccountService;

    // Constructor injection
    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * **Create a new user**
     *
     * **URL:** `POST /api/users`
     * 
     * **Request Body (JSON)**:
     * ```json
     * {
     *   "name": "Kyujin",
     *   "email": "Kyujin@example.com",
     *   "password": "Kyujin123"
     * }
     * ```
     *
     * **Response:**
     * - `200 OK` with JSON of the created user:
     * ```json
     * {
     *   "name": "Kyujin",
     *   "email": "Kyujin@example.com"
     * }
     * ```
     * - `400 Bad Request` if input is invalid
     */
    @PostMapping
    public ResponseEntity<UserAccountDTO> createUser(@RequestBody UserAccountDTO userDTO) {
        UserAccount createdUser = userAccountService.createUser(
                userDTO.getName(),
                userDTO.getEmail(),
                userDTO.getPassword()
        );
        return ResponseEntity.ok(new UserAccountDTO(createdUser));
    }

    /**
     * **Get a user by ID**
     *
     * **URL:** `GET /api/users/{id}`
     * 
     * **Path Parameter:**
     * - `id` (integer) - The user ID.
     *
     * **Response:**
     * - `200 OK` with JSON of the user:
     * ```json
     * {
     *   "name": "Kyujin",
     *   "email": "Kyujin@example.com"
     * }
     * ```
     * - `404 Not Found` if user does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getUserById(@PathVariable int id) {
        UserAccount user = userAccountService.getUserById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserAccountDTO(user));
    }

    /**
     * **Get a user by email**
     *
     * **URL:** `GET /api/users/by-email?email={email}`
     *
     * **Query Parameter:**
     * - `email` (string) - The user’s email address.
     *
     * **Response:**
     * - `200 OK` with JSON of the user:
     * ```json
     * {
     *   "name": "Kyujin",
     *   "email": "Kyujin@example.com"
     * }
     * ```
     * - `404 Not Found` if user does not exist.
     */
    @GetMapping("/by-email")
    public ResponseEntity<UserAccountDTO> getUserByEmail(@RequestParam String email) {
        UserAccount user = userAccountService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserAccountDTO(user));
    }

    /**
     * **Delete a user by ID**
     *
     * **URL:** `DELETE /api/users/{id}`
     * 
     * **Path Parameter:**
     * - `id` (integer) - The user ID.
     *
     * **Response:**
     * - `204 No Content` if deletion is successful.
     * - `404 Not Found` if user does not exist.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountDTO> updateUser(
            @PathVariable int id, 
            @RequestBody UserAccountDTO userDto) {

        // Call service to update
        UserAccount updated = userAccountService.updateUser(
                id, 
                userDto.getName(), 
                userDto.getEmail()
        );

        // Return 404 if user doesn't exist
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        // Convert updated entity to DTO
        return ResponseEntity.ok(new UserAccountDTO(updated));
    }
}


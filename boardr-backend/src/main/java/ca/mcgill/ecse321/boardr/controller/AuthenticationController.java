package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.Authorization.AuthRequestDTO;
import ca.mcgill.ecse321.boardr.dto.Authorization.AuthResponseDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationRequestDTO;
import ca.mcgill.ecse321.boardr.authentication.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthRequestDTO credentials) {
        AuthResponseDTO response = authenticationService.login(credentials);
        System.out.println("Login response: token=" + response.getToken() +
                ", email=" + response.getEmail() +
                ", name=" + response.getName());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequestDTO userInfo) {
        try {
            authenticationService.register(userInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body("User account created successfully!");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body((e.getMessage()));
        }

    }


}

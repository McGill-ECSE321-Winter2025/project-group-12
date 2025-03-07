package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.Authorization.AuthRequestDTO;
import ca.mcgill.ecse321.boardr.dto.Authorization.AuthResponseDTO;
import ca.mcgill.ecse321.boardr.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/login")
    public AuthResponseDTO login(@RequestBody AuthRequestDTO credentials) {
        return authenticationService.login(credentials);
    }
}

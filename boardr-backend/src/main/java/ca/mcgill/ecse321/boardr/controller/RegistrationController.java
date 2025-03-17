package ca.mcgill.ecse321.boardr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationResponseDTO;
import ca.mcgill.ecse321.boardr.service.RegistrationService;

/**
 * REST controller class for managing registrations in the Boardr application.
 * Provides an endpoint for registering for an event.
 * This class interacts with the RegistrationService to perform operations related to registrations.
 * REST APIs:
 * 
 * POST /registrations: Register for an event
 * @author David Vo
 * @version 2.0
 * @since 2025-03-12
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // Use Case 7: Register for an Event
    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> registerForEvent(@RequestBody RegistrationCreationDTO registrationDTO) {
        RegistrationResponseDTO registration = registrationService.registerForEventDTO(registrationDTO);
        return ResponseEntity.ok(registration);
    }
}
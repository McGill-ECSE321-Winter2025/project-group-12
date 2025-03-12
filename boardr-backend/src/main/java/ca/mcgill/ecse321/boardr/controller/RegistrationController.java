package ca.mcgill.ecse321.boardr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationDTO;
import ca.mcgill.ecse321.boardr.model.Registration;
import ca.mcgill.ecse321.boardr.service.RegistrationService;

@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // Use Case 7: Register for an Event
    @PostMapping
    public ResponseEntity<Registration> registerForEvent(@RequestBody RegistrationDTO registrationDTO) {
        Registration registration = registrationService.registerForEvent(registrationDTO.getEventId(), registrationDTO.getUserId());
        return ResponseEntity.ok(registration);
    }
}
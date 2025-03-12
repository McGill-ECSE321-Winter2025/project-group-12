package ca.mcgill.ecse321.boardr.controller;

import ca.mcgill.ecse321.boardr.dto.RegistrationDTO;
import ca.mcgill.ecse321.boardr.model.Registration;
import ca.mcgill.ecse321.boardr.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
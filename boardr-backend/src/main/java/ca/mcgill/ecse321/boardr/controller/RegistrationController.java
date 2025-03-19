package ca.mcgill.ecse321.boardr.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ca.mcgill.ecse321.boardr.dto.ErrorDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationResponseDTO;
import ca.mcgill.ecse321.boardr.service.RegistrationService;

/**
 * REST controller class for managing registrations in the Boardr application.
 * Provides endpoints for creating, reading, updating, deleting, and canceling registrations.
 * 
 * REST APIs:
 * - POST /registrations: Register for an event
 * - GET /registrations: Retrieve all registrations
 * - GET /registrations/{userId}/{eventId}: Retrieve a specific registration
 * - PUT /registrations/{userId}/{eventId}: Update registration date
 * - DELETE /registrations/{userId}/{eventId}: Delete a registration
 * - POST /registrations/{userId}/{eventId}/cancel: Cancel a registration
 * 
 * @author David Vo
 * @version 2.1
 * @since 2025-03-18
 */
@RestController
@RequestMapping("/registrations")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // Create a registration
    @PostMapping
    public ResponseEntity<RegistrationResponseDTO> createRegistration(@RequestBody RegistrationCreationDTO registrationDTO) {
        RegistrationResponseDTO registration = registrationService.createRegistration(registrationDTO);
        return ResponseEntity.ok(registration);
    }

    // Read all registrations
    @GetMapping
    public ResponseEntity<List<RegistrationResponseDTO>> getAllRegistrations() {
        List<RegistrationResponseDTO> registrations = registrationService.getAllRegistrations();
        return ResponseEntity.ok(registrations);
    }

    // Read a specific registration
    @GetMapping("/{userId}/{eventId}")
    public ResponseEntity<RegistrationResponseDTO> getRegistration(
            @PathVariable int userId,
            @PathVariable int eventId) {
        RegistrationResponseDTO registration = registrationService.getRegistration(userId, eventId);
        return ResponseEntity.ok(registration);
    }

    // Update a registration
    @PutMapping("/{userId}/{eventId}")
    public ResponseEntity<RegistrationResponseDTO> updateRegistration(
            @PathVariable int userId,
            @PathVariable int eventId,
            @RequestParam("registrationDate") Date newRegistrationDate) {
        RegistrationResponseDTO updatedRegistration = registrationService.updateRegistration(userId, eventId, newRegistrationDate);
        return ResponseEntity.ok(updatedRegistration);
    }

    // Delete a registration
    @DeleteMapping("/{userId}/{eventId}")
    public ResponseEntity<Void> deleteRegistration(
            @PathVariable int userId,
            @PathVariable int eventId) {
        registrationService.deleteRegistration(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    // Cancel a registration
    @PostMapping("/{userId}/{eventId}/cancel")
    public ResponseEntity<Void> cancelRegistration(
            @PathVariable int userId,
            @PathVariable int eventId) {
        registrationService.cancelRegistration(userId, eventId);
        return ResponseEntity.noContent().build();
    }

    // Exception handler for IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorDTO errorDTO = new ErrorDTO(ex.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    // Exception handler for general exceptions
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorDTO> handleGeneralException(Exception ex) {
        ErrorDTO errorDTO = new ErrorDTO("An unexpected error occurred: " + ex.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
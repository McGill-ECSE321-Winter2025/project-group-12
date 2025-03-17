package ca.mcgill.ecse321.boardr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationCreationDTO;
import ca.mcgill.ecse321.boardr.dto.Registration.RegistrationResponseDTO;
import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.Registration;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;

/**
 * Service class for managing registrations in the Boardr application.
 * Provides methods for registering for events.
 * This class interacts with the RegistrationRepository, EventRepository, and UserAccountRepository
 * to perform operations related to registrations.
 * @author David Vo
 * @version 2.0
 * @since 2025-03-12
 */
@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    // Use Case 7: Register for an Event (internal method)
    public Registration registerForEvent(int eventId, int userId) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        UserAccount user = userOpt.get();

        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();

        Registration.RegistrationKey key = new Registration.RegistrationKey(user, event);
        if (registrationRepository.findById(key).isPresent()) {
            throw new IllegalArgumentException("User is already registered for this event.");
        }

        List<Registration> registrations = event.getRegistrations();
        int currentRegistrations = (registrations != null) ? registrations.size() : 0;
        if (currentRegistrations >= event.getmaxParticipants()) {
            throw new IllegalArgumentException("Event is fully booked.");
        }

        Registration registration = new Registration(key);
        return registrationRepository.save(registration);
    }

    // Helper method to register for an Event from a DTO
    public RegistrationResponseDTO registerForEventDTO(RegistrationCreationDTO registrationDTO) {
        Registration registration = registerForEvent(registrationDTO.getEventId(), registrationDTO.getUserId());
        return new RegistrationResponseDTO(registration);
    }
}
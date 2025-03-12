package ca.mcgill.ecse321.boardr.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ca.mcgill.ecse321.boardr.model.Event;
import ca.mcgill.ecse321.boardr.model.Registration;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.repo.EventRepository;
import ca.mcgill.ecse321.boardr.repo.RegistrationRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    // Use Case 7: Register for an Event
    public Registration registerForEvent(int eventId, int userId) {
        // Pre-condition: User must be logged in (assumed handled by authentication)
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        UserAccount user = userOpt.get();

        // Pre-condition: Event must exist and have available slots
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();

        if (event.getRegistrations().size() >= event.getmaxParticipants()) {
            throw new IllegalArgumentException("Event is fully booked.");
        }

        // Create and save the registration
        Registration.RegistrationKey key = new Registration.RegistrationKey(user, event);
        Registration registration = new Registration(key);
        return registrationRepository.save(registration);
    }
}
package ca.mcgill.ecse321.boardr.service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
 * Provides methods for creating, reading, updating, deleting, and canceling registrations.
 * This class interacts with the RegistrationRepository, EventRepository, and UserAccountRepository.
 * @author David Vo
 * @version 2.1
 * @since 2025-03-18
 */
@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserAccountRepository userAccountRepository;

    // Create a registration with enhanced validation
    public RegistrationResponseDTO createRegistration(RegistrationCreationDTO registrationDTO) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(registrationDTO.getUserId());
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        UserAccount user = userOpt.get();

        Optional<Event> eventOpt = eventRepository.findById(registrationDTO.getEventId());
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Event event = eventOpt.get();

        // Validation checks
        Registration.RegistrationKey key = new Registration.RegistrationKey(user, event);
        if (registrationRepository.findById(key).isPresent()) {
            throw new IllegalArgumentException("User is already registered for this event.");
        }
        if (user.getUserAccountId() == event.getOrganizer().getUserAccountId()) {
            throw new IllegalArgumentException("Organizer cannot register for their own event.");
        }
        int currentRegistrations = event.getRegistrations() != null ? event.getRegistrations().size() : 0;
        if (currentRegistrations >= event.getmaxParticipants()) {
            throw new IllegalArgumentException("Event is fully booked.");
        }
        LocalDate today = LocalDate.now();
        LocalDate eventDate = LocalDate.of(event.getEventDate() / 10000, (event.getEventDate() % 10000) / 100, event.getEventDate() % 100);
        if (eventDate.isBefore(today)) {
            throw new IllegalArgumentException("Cannot register for a past event.");
        }

        Registration registration = new Registration(key);
        Registration savedRegistration = registrationRepository.save(registration);
        return new RegistrationResponseDTO(savedRegistration);
    }

    // Read all registrations
    public List<RegistrationResponseDTO> getAllRegistrations() {
        Iterable<Registration> registrations = registrationRepository.findAll();
        return StreamSupport.stream(registrations.spliterator(), false)
                .map(RegistrationResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Read a specific registration
    public RegistrationResponseDTO getRegistration(int userId, int eventId) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Registration.RegistrationKey key = new Registration.RegistrationKey(userOpt.get(), eventOpt.get());
        Optional<Registration> registrationOpt = registrationRepository.findById(key);
        if (!registrationOpt.isPresent()) {
            throw new IllegalArgumentException("Registration not found.");
        }
        return new RegistrationResponseDTO(registrationOpt.get());
    }

    // Update registration date
    public RegistrationResponseDTO updateRegistration(int userId, int eventId, Date newRegistrationDate) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Registration.RegistrationKey key = new Registration.RegistrationKey(userOpt.get(), eventOpt.get());
        Optional<Registration> registrationOpt = registrationRepository.findById(key);
        if (!registrationOpt.isPresent()) {
            throw new IllegalArgumentException("Registration not found.");
        }
        Registration registration = registrationOpt.get();
        Event event = eventOpt.get(); // Define event here from eventOpt

        LocalDate today = LocalDate.now();
        LocalDate eventDate = LocalDate.of(event.getEventDate() / 10000, (event.getEventDate() % 10000) / 100, event.getEventDate() % 100);
        if (eventDate.isBefore(today)) {
            throw new IllegalArgumentException("Cannot update registration for a past event.");
        }
        if (newRegistrationDate.after(Date.valueOf(eventDate))) {
            throw new IllegalArgumentException("Registration date cannot be after the event date.");
        }

        setRegistrationDate(registration, newRegistrationDate); // Use setter method
        Registration updatedRegistration = registrationRepository.save(registration);
        return new RegistrationResponseDTO(updatedRegistration);
    }

    // Delete a registration
    public void deleteRegistration(int userId, int eventId) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Registration.RegistrationKey key = new Registration.RegistrationKey(userOpt.get(), eventOpt.get());
        Optional<Registration> registrationOpt = registrationRepository.findById(key);
        if (!registrationOpt.isPresent()) {
            throw new IllegalArgumentException("Registration not found.");
        }
        registrationRepository.delete(registrationOpt.get());
    }

    // Cancel a registration (alias for delete with additional validation)
    public void cancelRegistration(int userId, int eventId) {
        Optional<UserAccount> userOpt = userAccountRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new IllegalArgumentException("User not found.");
        }
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (!eventOpt.isPresent()) {
            throw new IllegalArgumentException("Event not found.");
        }
        Registration.RegistrationKey key = new Registration.RegistrationKey(userOpt.get(), eventOpt.get());
        Optional<Registration> registrationOpt = registrationRepository.findById(key);
        if (!registrationOpt.isPresent()) {
            throw new IllegalArgumentException("Registration not found.");
        }
        LocalDate today = LocalDate.now();
        LocalDate eventDate = LocalDate.of(eventOpt.get().getEventDate() / 10000, (eventOpt.get().getEventDate() % 10000) / 100, eventOpt.get().getEventDate() % 100);
        if (eventDate.isBefore(today)) {
            throw new IllegalArgumentException("Cannot cancel registration for a past event.");
        }
        registrationRepository.delete(registrationOpt.get());
    }

    // Setter for registration date (since the model lacks one)
    private void setRegistrationDate(Registration registration, Date date) {
        try {
            java.lang.reflect.Field field = Registration.class.getDeclaredField("registrationDate");
            field.setAccessible(true);
            field.set(registration, date);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set registration date", e);
        }
    }
    
}
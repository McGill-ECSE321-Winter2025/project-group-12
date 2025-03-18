package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a registration for an event, uniquely identified by a user and event combination.
 * 
 * @author Eric, Junho, Jione, David Zhou, David Vo
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
public class Registration {
    @EmbeddedId /* Registrations are unique to users and events, so no generated id required */
    private RegistrationKey registrationKey;
    private Date registrationDate;

    
    protected Registration() {}

    public Registration(RegistrationKey registrationKey) {
        this.registrationKey = registrationKey;
        this.registrationDate = Date.valueOf(LocalDate.now());
    }

    public RegistrationKey getRegistrationKey(){
        return this.registrationKey;
    }

    public Date getRegistrationDate(){
        return this.registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    
    @Embeddable 
    public static class RegistrationKey implements Serializable {
        @ManyToOne
        @JoinColumn(name = "user_account_id")
        private UserAccount registrant;

        @ManyToOne
        @JoinColumn(name = "event_id")
        private Event event;

        public RegistrationKey() {
        }

        public RegistrationKey(UserAccount registrant, Event event) {
            this.registrant = registrant;
            this.event = event;
        }

        public UserAccount getRegistrant() {
            return registrant;
        }

        public Event getEvent() {
            return event;
        }

        @Override /* Registration key instances should be fungible if they are owned by the same user and for the same event */
        public boolean equals(Object obj) {
            if (!(obj instanceof RegistrationKey)) {
                return false;
            }
            RegistrationKey that = (RegistrationKey) obj;
            return this.registrant.getUserAccountId() == that.registrant.getUserAccountId()
                    && this.event.getEventId() == that.event.getEventId();
        }

        @Override /* Allows different instances of the same registration key fungible */
        public int hashCode() {
            return Objects.hash(this.registrant.getUserAccountId(), this.event.getEventId());
        }
    }
}

package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.*;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;


@Entity
public class Registration {
    @EmbeddedId
    private RegistrationKey registrationKey;
    private Date registrationDate;

    
    protected Registration() {}

    public Registration(RegistrationKey registrationKey) {
        this.registrationKey = registrationKey;
        this.registrationDate = new Date(System.currentTimeMillis());
    }

    public RegistrationKey getRegistrationKey(){
        return this.registrationKey;
    }

    public Date getRegistrationDate(){
        return this.registrationDate;
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

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof RegistrationKey)) {
                return false;
            }
            RegistrationKey that = (RegistrationKey) obj;
            return this.registrant.getUserAccountId() == that.registrant.getUserAccountId()
                    && this.event.getEventId() == that.event.getEventId();
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.registrant.getUserAccountId(), this.event.getEventId());
        }
    }
}

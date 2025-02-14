package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;


@Entity
public class Registration {
    @Id
    @GeneratedValue
    private int registrationId;

    private int registrationDate;

    @OneToOne
    @JoinColumn(name = "event_id")
    private Event event;
    @OneToOne
    @JoinColumn(name = "user_id")
    private UserAccount user;

    
    protected Registration() {}

    public Registration(int registrationDate, Event event, UserAccount user) {
        this.registrationDate = registrationDate;
        this.event = event;
        this.user = user;
    }

    int getRegistrationId(){
        return this.registrationId;
    }

    int getRegistrationDate(){
        return this.registrationDate;
    }

    Event getEvent(){
        return this.event;
    }
    
    UserAccount getUser(){
        return this.user;
    }

}

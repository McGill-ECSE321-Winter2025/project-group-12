package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;


@Entity
public class Registration {
    @Id
    @GeneratedValue
    private int registrationId;
    private Date registrationDate;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_account_id")
    private UserAccount userAccount;

    
    protected Registration() {}

    public Registration(Event event, UserAccount userAccount) {
        this.event = event;
        this.userAccount = userAccount;
        this.registrationDate = new Date(System.currentTimeMillis());
    }

    public int getRegistrationId(){
        return this.registrationId;
    }

    public Date getRegistrationDate(){
        return this.registrationDate;
    }

    public Event getEvent(){
        return this.event;
    }
    
    public UserAccount getUserAccount(){
        return this.userAccount;
    }
}

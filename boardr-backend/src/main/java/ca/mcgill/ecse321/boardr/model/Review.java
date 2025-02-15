package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

@Entity
public class Review {
    @Id
    @GeneratedValue
    private int reviewId;
    private int rating;
    private String comment;
    private Date reviewDate;

    @ManyToOne
    @JoinColumn(name = "user_acount_id")
    private UserAccount userAccount;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private BoardGame boardGame;
    
    
    protected Review() {}

    public Review(int rating, String comment, UserAccount userAccount, BoardGame boardGame) {
        this.rating = rating;
        this.comment = comment;
        this.userAccount = userAccount;
        this.boardGame = boardGame;
        this.reviewDate = new Date(System.currentTimeMillis());
    }

    public int getReviewID() {
		return this.reviewId;
	}

    public int getRating() {
        return this.rating;
    }

    public String getComment() {
        return this.comment;
    }

    public Date getReviewDate() {
        return this.reviewDate;
    }

    public UserAccount getUserAccount() {
        return this.userAccount;
    }

    public BoardGame getBoardGame() {
        return this.boardGame;
    }
}

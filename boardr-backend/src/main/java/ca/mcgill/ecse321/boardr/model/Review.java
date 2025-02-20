package ca.mcgill.ecse321.boardr.model;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.sql.Date;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;

/**
 * Represents a review for a board game, including rating, comment, and associated user account.
 * 
 * @author Eric, Junho, Jione, David Zhou
 * @version 1.0
 * @since 2023-10-05
 */
@Entity
public class Review {
    @Id
    @GeneratedValue /* reviewId is irrelevant to user, but valuable for table management */
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
        this.reviewDate = Date.valueOf(LocalDate.now());
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

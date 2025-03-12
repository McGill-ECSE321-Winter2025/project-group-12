package ca.mcgill.ecse321.boardr.dto.Review;

import java.sql.Date;

import ca.mcgill.ecse321.boardr.model.Review;

public class ReviewResponseDTO {

    private int id;
    private int rating;
    private String comment;
    private Date reviewDate;
    private int userAccountId;
    private int boardGameId;

    @SuppressWarnings("unused")
    private ReviewResponseDTO() {
    }

    public ReviewResponseDTO(Review review) {
        this.id = review.getReviewID();
        this.rating = review.getRating();
        this.comment = review.getComment();
        this.reviewDate = review.getReviewDate();
        this.userAccountId = review.getUserAccount().getUserAccountId();
        this.boardGameId = review.getBoardGame().getGameId();
    }

    public int getId() {
        return id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public int getUserAccountId() {
        return userAccountId;
    }

    public int getBoardGameId() {
        return boardGameId;
    }
}

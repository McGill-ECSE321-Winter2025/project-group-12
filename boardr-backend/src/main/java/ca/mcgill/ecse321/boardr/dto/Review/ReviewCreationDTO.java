package ca.mcgill.ecse321.boardr.dto.Review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ReviewCreationDTO {

    @NotNull(message = "User account ID is required")
    private Integer userAccountId;

    @NotNull(message = "Board game ID is required")
    private Integer boardGameId;

    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private int rating;

    @NotBlank(message = "Comment is required")
    private String comment;

    public ReviewCreationDTO(Integer userAccountId, Integer boardGameId, int rating, String comment) {
        this.userAccountId = userAccountId;
        this.boardGameId = boardGameId;
        this.rating = rating;
        this.comment = comment;
    }

    public Integer getUserAccountId() {
        return userAccountId;
    }

    public Integer getBoardGameId() {
        return boardGameId;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }
}

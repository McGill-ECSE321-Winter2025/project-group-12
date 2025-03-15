package ca.mcgill.ecse321.boardr.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardr.dto.Review.ReviewCreationDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.Review;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.BoardGame;
import ca.mcgill.ecse321.boardr.repo.ReviewRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameRepository;
import jakarta.validation.Valid;

@Service
@Validated
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private UserAccountRepository userAccountRepo;

    @Autowired
    private BoardGameRepository boardGameRepo;

    @Transactional
    public Review createReview(@Valid ReviewCreationDTO reviewToCreate) {
        UserAccount userAccount = userAccountRepo.findById(reviewToCreate.getUserAccountId())
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Invalid user account ID"));
        BoardGame boardGame = boardGameRepo.findById(reviewToCreate.getBoardGameId())
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Invalid board game ID"));

        Review review = new Review(reviewToCreate.getRating(), reviewToCreate.getComment(), userAccount, boardGame);
        return reviewRepo.save(review);
    }

    public Review getReviewById(int reviewId) {
        return reviewRepo.findById(reviewId)
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Invalid review ID"));
    }

    public List<Review> getAllReviews() {
        return StreamSupport.stream(reviewRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteReview(int reviewId) {
        reviewRepo.deleteById(reviewId);
    }
}

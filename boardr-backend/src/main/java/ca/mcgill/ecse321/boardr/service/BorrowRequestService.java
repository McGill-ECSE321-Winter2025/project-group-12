package ca.mcgill.ecse321.boardr.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import ca.mcgill.ecse321.boardr.dto.BorrowRequest.BorrowRequestCreationDTO;
import ca.mcgill.ecse321.boardr.exceptions.BoardrException;
import ca.mcgill.ecse321.boardr.model.BorrowRequest;
import ca.mcgill.ecse321.boardr.model.UserAccount;
import ca.mcgill.ecse321.boardr.model.BoardGameInstance;
import ca.mcgill.ecse321.boardr.repo.BorrowRequestRepository;
import ca.mcgill.ecse321.boardr.repo.UserAccountRepository;
import ca.mcgill.ecse321.boardr.repo.BoardGameInstanceRepository;
import jakarta.validation.Valid;
/**
 * Service class for managing borrow requests in the Boardr application.
 * Provides methods for creating, retrieving, updating, and deleting borrow requests.
 * This class interacts with the BorrowRequestRepository, UserAccountRepository,
 * and BoardGameInstanceRepository to handle borrow request operations.
 * @author Yoonjung Choi
 * @version 1.0
 * @since 2025-03-12
 */
@Service
@Validated
public class BorrowRequestService {
    @Autowired
    private BorrowRequestRepository borrowRequestRepo;

    @Autowired
    private UserAccountRepository userAccountRepo;

    @Autowired
    private BoardGameInstanceRepository boardGameInstanceRepo;

    @Transactional
    public BorrowRequest createBorrowRequest(@Valid BorrowRequestCreationDTO borrowRequestToCreate) {
        UserAccount userAccount = userAccountRepo.findById(borrowRequestToCreate.getUserAccountId())
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Invalid user account ID"));
        BoardGameInstance boardGameInstance = boardGameInstanceRepo.findById(borrowRequestToCreate.getBoardGameInstanceId())
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "No available board game instances"));

        BorrowRequest borrowRequest = new BorrowRequest(boardGameInstance, userAccount, borrowRequestToCreate.getRequestDate(), borrowRequestToCreate.getReturnDate());
        return borrowRequestRepo.save(borrowRequest);
    }

    public BorrowRequest getBorrowRequestById(int borrowRequestId) {
        return borrowRequestRepo.findById(borrowRequestId)
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Invalid borrow request ID"));
    }

    public List<BorrowRequest> getAllBorrowRequests() {
        return StreamSupport.stream(borrowRequestRepo.findAll().spliterator(), false)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteBorrowRequest(int borrowRequestId) {
        borrowRequestRepo.deleteById(borrowRequestId);
    }

    @Transactional
    public BorrowRequest updateBorrowRequestStatus(int borrowRequestId, BorrowRequest.RequestStatus status) {
        BorrowRequest borrowRequest = borrowRequestRepo.findById(borrowRequestId)
                .orElseThrow(() -> new BoardrException(HttpStatus.NOT_FOUND, "Invalid borrow request ID"));
        borrowRequest.setRequestStatus(status);

        if (status == BorrowRequest.RequestStatus.Accepted) {
            // When borrow request is accepted, mark the associated board game instance as unavailable
            BoardGameInstance gameInstance = borrowRequest.getBoardGameInstance();
            gameInstance.setAvailable(false);
            boardGameInstanceRepo.save(gameInstance);
        }

        return borrowRequestRepo.save(borrowRequest);
    }

    @Transactional(readOnly = true)
    public List<BorrowRequest> getAcceptedBorrowRequestsByBorrower(int borrowerId) {
        return borrowRequestRepo.findAcceptedBorrowRequestsByBorrower(borrowerId);
    }
}

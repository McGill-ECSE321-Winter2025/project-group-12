package ca.mcgill.ecse321.boardr.exceptions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import ca.mcgill.ecse321.boardr.dto.ErrorDTO;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class BoardrExceptionHandler {
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDTO> handleConstraintViolationException(ConstraintViolationException e) {
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        return new ResponseEntity<ErrorDTO>(new ErrorDTO(errors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BoardrException.class)
    public ResponseEntity<ErrorDTO> handleBoardrException(BoardrException e) {
        return new ResponseEntity<ErrorDTO>(new ErrorDTO(e.getMessage()), e.getStatus());
    }
}

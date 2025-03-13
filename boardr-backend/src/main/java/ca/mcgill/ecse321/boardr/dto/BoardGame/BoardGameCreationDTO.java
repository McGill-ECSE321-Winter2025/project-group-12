package ca.mcgill.ecse321.boardr.dto.BoardGame;

import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new BoardGame entity.
 * Ensures the required fields are provided when a new game is created.
 */
public class BoardGameCreationDTO {

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Description is required")
    private String description;

    public BoardGameCreationDTO(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}

package ca.mcgill.ecse321.boardr.dto.BoardGameInstance;

public class BoardGameInstanceUpdateDTO {
    private String condition;

    public BoardGameInstanceUpdateDTO() {}

    public BoardGameInstanceUpdateDTO(String condition) {
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }
}

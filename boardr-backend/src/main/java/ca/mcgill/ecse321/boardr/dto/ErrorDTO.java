package ca.mcgill.ecse321.boardr.dto;

import java.util.List;

public class ErrorDTO {
	private List<String> errors;

	@SuppressWarnings("unused")
	private ErrorDTO() {
	}

	public ErrorDTO(List<String> errors) {
		this.errors = errors;
	}

	public ErrorDTO(String error) {
		this.errors = List.of(error);
	}

	public List<String> getErrors() {
		return errors;
	}

	public void setErrors(List<String> errors) {
		this.errors = errors;
	}
}

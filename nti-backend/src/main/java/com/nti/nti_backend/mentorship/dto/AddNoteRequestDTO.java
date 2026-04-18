package com.nti.nti_backend.mentorship.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddNoteRequestDTO {

    @NotBlank(message = "Note content cannot be empty")
    private String content;
}

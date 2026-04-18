package com.nti.nti_backend.mentorship.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class ConsultationNoteDTO {
    private UUID id;
    private String content;
    private Long createdById;
    private String createdByName;
    private OffsetDateTime createdAt;
}

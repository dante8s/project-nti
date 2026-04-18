package com.nti.nti_backend.mentorship;

import com.nti.nti_backend.mentorship.dto.AddNoteRequestDTO;
import com.nti.nti_backend.mentorship.dto.AssignMentorRequestDTO;
import com.nti.nti_backend.mentorship.dto.ConsultationNoteDTO;
import com.nti.nti_backend.mentorship.dto.MentorshipResponseDTO;
import com.nti.nti_backend.mentorship.entity.ConsultationNote;
import com.nti.nti_backend.mentorship.entity.Mentorship;
import com.nti.nti_backend.mentorship.entity.MentorshipStatus;
import com.nti.nti_backend.mentorship.repository.ConsultationNoteRepository;
import com.nti.nti_backend.mentorship.repository.MentorshipRepository;
import com.nti.nti_backend.organization.exception.ConflictException;
import com.nti.nti_backend.organization.exception.ResourceNotFoundException;
import com.nti.nti_backend.user.User;
import com.nti.nti_backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MentorshipService {

    private final MentorshipRepository mentorshipRepository;
    private final ConsultationNoteRepository noteRepository;
    private final UserRepository userRepository;


    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }

    // ASSIGN MENTOR
    @Transactional
    public MentorshipResponseDTO assignMentor(AssignMentorRequestDTO dto) {

        User mentor = userRepository.findById(
                dto.getMentorUserId()
        ).orElseThrow(() -> new ResourceNotFoundException(
                "User not found with id: " + dto.getMentorUserId()
        ));

        if (!mentor.getRole().name().equals("MENTOR")) {
            throw new ConflictException(
                    "User " + mentor.getEmail() + " does not have the MENTOR role"
            );
        }

        if (dto.getApplicationId() != null) {
            boolean alreadyExists = mentorshipRepository
                    .existsByMentorIdAndApplicationIdAndStatus(
                            dto.getMentorUserId(),
                            dto.getApplicationId(),
                            MentorshipStatus.ACTIVE
                    );
            if (alreadyExists) {
                throw new ConflictException(
                        "This mentor already has an actieve mentorship on that application"
                );
            }
        }

        Mentorship mentorship = Mentorship.builder()
                .mentor(mentor)
                .applicationId(dto.getApplicationId())
                .status(MentorshipStatus.ACTIVE)
                .build();

        mentorship = mentorshipRepository.save(mentorship);
        return toResponseDTO(mentorship, null);
    }

    // GET my mentorships (mentor)
    @Transactional(readOnly = true)
    public List<MentorshipResponseDTO> getMyMentorships() {
        Long currentUserId = getCurrentUser().getId();
        return mentorshipRepository
                .findAllByMentorIdAndStatus(currentUserId, MentorshipStatus.ACTIVE)
                .stream()
                .map(m -> toResponseDTO(m, null))
                .toList();
    }

    // Get one with notes
    @Transactional(readOnly = true)
    public MentorshipResponseDTO getById(UUID id) {
        Mentorship mentorship = mentorshipRepository.findByIdWithNotes(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mentorship with id: " + id + " not found"
                ));

        Long currentUserId = getCurrentUser().getId();
        boolean isAdmin = getCurrentUser().getRole().name().equals("ADMIN");
        boolean isMentor = mentorship.getMentor().getId().equals(currentUserId);

        if (!isAdmin && !isMentor) {
            throw new ConflictException("You do not have access to this mentorship");
        }

        List<ConsultationNoteDTO> noteDTOs = mentorship.getNotes().stream()
                .map(this::toNoteDTO)
                .toList();
        return toResponseDTO(mentorship, noteDTOs);

    }

    // ADD NOTE (mentor only)
    @Transactional
    public ConsultationNoteDTO addNote(UUID mentorshipId, AddNoteRequestDTO dto) {
        Mentorship mentorship = mentorshipRepository.findById(mentorshipId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Mentorship not found: " + mentorshipId
                ));

        User currentUser = getCurrentUser();


        if (!mentorship.getMentor().getId().equals(currentUser.getId())) {
            throw new ConflictException(
                    "Only the assigned mentor can add notes to this mentorship."
            );
        }

        if (mentorship.getStatus() != MentorshipStatus.ACTIVE) {
            throw new ConflictException(
                    "Cannot add notes to this mentorship with status: " + mentorship.getStatus()
            );
        }

        ConsultationNote note = ConsultationNote.builder()
                .mentorship(mentorship)
                .content(dto.getContent())
                .createdBy(currentUser)
                .build();

        note = noteRepository.save(note);
        return toNoteDTO(note);
    }

    // Get Notes Paginated
    @Transactional(readOnly = true)
    public Page<ConsultationNoteDTO> getNotes(UUID mentorshipId, Pageable pageable) {
        if (!mentorshipRepository.existsById(mentorshipId)) {
            throw new ResourceNotFoundException("Mentorship not found: " + mentorshipId);
        }
        return noteRepository
                .findAllByMentorshipIdOrderByCreatedAtDesc(mentorshipId, pageable)
                .map(this::toNoteDTO);
    }

    // Close Mentorship
    @Transactional
    public MentorshipResponseDTO closeMentorship(UUID id, MentorshipStatus newStatus) {
        Mentorship mentorship = mentorshipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mentorship not found: " + id));
        if (mentorship.getStatus() != MentorshipStatus.ACTIVE) {
            throw new ConflictException(
                    "Mentorship has status " + mentorship.getStatus()
            );
        }

        if (newStatus == MentorshipStatus.ACTIVE) {
            throw new ConflictException("Cannot transition back to ACTIVE");
        }

        User currentUser = getCurrentUser();
        boolean isAdmin = currentUser.getRole().name().equals("ADMIN");


        if  (!isAdmin) {
            throw new ConflictException("You cannot close this mentorship");
        }

        mentorship.setStatus(newStatus);
        mentorship.setEndDate(OffsetDateTime.now());
        mentorshipRepository.save(mentorship);

        return toResponseDTO(mentorship, null);
    }

    // Mapping
    private MentorshipResponseDTO toResponseDTO(
            Mentorship m, List<ConsultationNoteDTO> notes
    ) {
        return MentorshipResponseDTO.builder()
                .id(m.getId())
                .mentorUserId(m.getMentor().getId())
                .mentorName(m.getMentor().getName())
                .mentorEmail(m.getMentor().getEmail())
                .applicationId(m.getApplicationId())
                .status(m.getStatus())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .notes(notes)
                .createdAt(m.getCreatedAt())
                .build();
    }

    private ConsultationNoteDTO toNoteDTO(ConsultationNote n) {
        return ConsultationNoteDTO.builder()
                .id(n.getId())
                .content(n.getContent())
                .createdById(n.getCreatedBy().getId())
                .createdByName(n.getCreatedBy().getName())
                .createdAt(n.getCreatedAt())
                .build();

    }
}

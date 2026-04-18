package com.nti.nti_backend.mentorship.repository;

import com.nti.nti_backend.mentorship.entity.Mentorship;
import com.nti.nti_backend.mentorship.entity.MentorshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MentorshipRepository extends JpaRepository<Mentorship, UUID> {

    //All mentorships for specific mentor
    List<Mentorship> findByMentorId(Long mentorId);

    List<Mentorship> findAllByMentorIdAndStatus(Long mentorId, MentorshipStatus status);

    boolean existsByMentorIdAndApplicationIdAndStatus(
            Long mentorId, Long applicationId, MentorshipStatus status
    );

    // Load mentorship with its notes
    @Query("SELECT m FROM Mentorship m LEFT JOIN FETCH m.notes n LEFT JOIN FETCH n.createdBy WHERE m.id = :id")
    Optional<Mentorship> findByIdWithNotes(UUID id);
}

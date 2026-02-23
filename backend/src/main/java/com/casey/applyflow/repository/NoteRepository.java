package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<List<Note>>findAllByInterviewId(Long interviewId);
    Optional<Note> findByIdAndInterviewApplicationUserId(Long noteId, Long userId);
    Optional<Note> findByIdAndInterviewIdAndInterviewApplicationUserId(Long noteId, Long interviewId, Long userId);

    Optional<List<Note>> findAllByInterviewApplicationIdAndInterviewIdAndInterviewApplicationUserId(Long applicationId, Long InterviewId, Long userId);
    Optional<Note> findByIdAndInterviewApplicationIdAndInterviewIdAndInterviewApplicationUserId(Long noteId, Long applicationId, Long InterviewId, Long userId);
}

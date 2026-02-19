package com.casey.applyflow.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.casey.applyflow.domain.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<List<Note>>findAllByInterviewId(Long interviewId);
    Optional<Note> findByIdAndInterviewApplicationUserId(Long noteId, Long userId);
}

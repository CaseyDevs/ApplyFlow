package com.casey.applyflow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Interview;
import com.casey.applyflow.domain.Note;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.dto.NoteRequestDto;
import com.casey.applyflow.dto.NoteResponseDto;
import com.casey.applyflow.exception.InterviewNotFoundException;
import com.casey.applyflow.exception.NoteNotFoundException;
import com.casey.applyflow.repository.InterviewRepository;
import com.casey.applyflow.repository.NoteRepository;

@Service
public class NoteService {
    private NoteRepository noteRepository;
    private InterviewRepository interviewRepository;
    private CurrentUserProvider currentUserProvider;
    private Logger log = LoggerFactory.getLogger(NoteService.class);


    public NoteService(NoteRepository noteRepository, InterviewRepository interviewRepository) {
        this.noteRepository = noteRepository;
        this.interviewRepository = interviewRepository;
    }

    @Transactional(readOnly = true)
    public List<NoteResponseDto> getAllNotes(Long interviewId) {
        List<Note> notes = noteRepository.findAllByInterviewId(interviewId)
            .orElseThrow(() -> new NoteNotFoundException("You do not have any notes for this interview."));

        log.info("Fetching notes for interview {}", interviewId);

        return notes.stream()
            .map(this::toNoteResponseDto)
            .toList();
    }

    private NoteResponseDto toNoteResponseDto(Note note) {
        return new NoteResponseDto(
            note.getDescription(),
            note.getInterview().getId()
        );
    }

    @Transactional
    public NoteResponseDto createNote(NoteRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Interview interview = interviewRepository.findByIdAndApplicationUserId(request.interviewId(), user.getId())
            .orElseThrow(() -> new InterviewNotFoundException("Interview does not exist!"));
        
        Note note = new Note(
            request.description(),
            interview
        );

        noteRepository.save(note);
        interview.addNote(note);

        log.info("Note {} saved to interview {}", note.getId(), interview.getId());

        return toNoteResponseDto(note);
    }

    @Transactional
    public NoteResponseDto updateNote(Long noteId, NoteRequestDto request) {
        User user = currentUserProvider.getCurrentUser();

        Note note = noteRepository.findByIdAndInterviewApplicationUserId(noteId, user.getId())
            .orElseThrow(() -> new NoteNotFoundException("Note does not exist!"));

        note.setDescription(request.description());

        return toNoteResponseDto(note);
    }

    @Transactional
    public void deleteNote(Long noteId) {
        User user = currentUserProvider.getCurrentUser();
        
        Note note = noteRepository.findByIdAndInterviewApplicationUserId(noteId, user.getId())
            .orElseThrow(() -> new NoteNotFoundException("Note does not exist!"));

        note.getInterview().removeNote(note);
        noteRepository.delete(note);
    }
}

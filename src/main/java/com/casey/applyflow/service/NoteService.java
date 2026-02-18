package com.casey.applyflow.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.domain.Note;
import com.casey.applyflow.dto.NoteResponseDto;
import com.casey.applyflow.exception.NoteNotFoundException;
import com.casey.applyflow.repository.NoteRepository;

@Service
public class NoteService {
    private NoteRepository noteRepository;
    private Logger log = LoggerFactory.getLogger(NoteService.class);


    public NoteService(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
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
}

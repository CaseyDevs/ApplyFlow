package com.casey.applyflow.controller.v1;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.ResponseEntity;

import com.casey.applyflow.dto.NoteRequestDto;
import com.casey.applyflow.dto.NoteResponseDto;
import com.casey.applyflow.service.NoteService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;





@RestController
@RequestMapping("/api/v1")
public class NoteController {
    private NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }
    
    @GetMapping("/applications/{applicationId}/interviews/{interviewId}/notes")
    public ResponseEntity<List<NoteResponseDto>> getAllInterviewNotes(
        @PathVariable @Min(1) Long applicationId,
        @PathVariable @Min(1) Long interviewId
    ) {
        List<NoteResponseDto> response = noteService.getAllNotes(applicationId, interviewId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/applications/{applicationId}/interviews/{interviewId}/notes")
    public ResponseEntity<NoteResponseDto> createNote(
        @PathVariable @Min(1) Long applicationId,
        @PathVariable @Min(1) Long interviewId,
        @Valid @RequestBody NoteRequestDto request
    ) {
        NoteResponseDto response = noteService.createNote(applicationId, interviewId, request);
        
        return ResponseEntity.created(
            ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/notes/{id}")
                .buildAndExpand(response.noteId())
                .toUri()).body(response);
    }
    
    @PutMapping("/applications/{applicationId}/interviews/{interviewId}/notes/{noteId}")
    public ResponseEntity<NoteResponseDto> updateNote(
        @PathVariable @Min(1) Long applicationId,
        @PathVariable @Min(1) Long interviewId, 
        @PathVariable @Min(1) Long noteId, 
        @RequestBody NoteRequestDto request
    ) {
        NoteResponseDto response = noteService.updateNote(noteId, applicationId, interviewId, request);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/applications/{applicationId}/interviews/{interviewId}/notes/{noteId}")
    public ResponseEntity<Void> deleteNote(
        @PathVariable @Min(1) Long applicationId,
        @PathVariable @Min(1) Long interviewId, 
        @PathVariable @Min(1) Long noteId
    ) {
        noteService.deleteNote(noteId, applicationId, interviewId);

        return ResponseEntity.noContent().build();
    }
    
}

package com.nms.controllers;

import com.nms.entities.Note;
import com.nms.services.NotesService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RequestMapping("/notes")
@RestController
@RequiredArgsConstructor
public class NotesController {

    private final NotesService notesService;

    // Get all notes for any user (ADMIN only)
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllNotes(@PathVariable String username) {
        try {
            List<Note> allNotes = notesService.getAllNotes(username);
            return ResponseEntity.ok(allNotes);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Get all notes for the authenticated user (USER only)
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getAllNotes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // Assuming username is stored in JWT as the principal's name
        try {
            List<Note> allNotes = notesService.getAllNotes(username);
            return ResponseEntity.ok(allNotes);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Get a particular note for any user (ADMIN only)
    @GetMapping("/{username}/{noteId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getUserNote(@PathVariable String username, @PathVariable ObjectId noteId) {
        try {
            Optional<Note> userNote = notesService.getUserNote(username, noteId);
            if (userNote.isPresent()) {
                return ResponseEntity.ok(userNote.get());
            }
            return new ResponseEntity<>("Particular note is not found", HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Get a particular note for the authenticated user (USER only)
    @GetMapping("/{noteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> getUserNote(@PathVariable ObjectId noteId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Optional<Note> userNote = notesService.getUserNote(username, noteId);
            if (userNote.isPresent()) {
                return ResponseEntity.ok(userNote.get());
            }
            return new ResponseEntity<>("Particular note is not found", HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Save a new note for the authenticated user (USER only)
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> saveNote(@RequestBody Note newNote) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Note savedNote = notesService.saveNote(newNote, username);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedNote);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        }
    }

    // Delete a note for the authenticated user (USER only)
    @DeleteMapping("/{noteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> deleteNote(@PathVariable ObjectId noteId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            notesService.deleteNote(username, noteId);
            return ResponseEntity.ok("Note deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }

    // Update a note for the authenticated user (USER only)
    @PutMapping("/{noteId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> updateNote(
            @PathVariable ObjectId noteId,
            @RequestBody Note updatedNote) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            Note note = notesService.updateNote(username, noteId, updatedNote);
            return ResponseEntity.ok(note);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
    }
}

package com.nms.services;

import com.nms.entities.Note;
import com.nms.entities.User;
import com.nms.repositories.NotesRepositories;
import com.nms.repositories.UserRepositories;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotesService {

    private final NotesRepositories notesRepositories;

    private final UserRepositories userRepositories;

    public List<Note> getAllNotes(String username) {
        User user = userRepositories.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Note> notes = user.getNotes();
        if (notes.isEmpty()) {
            return new ArrayList<>();
        }
        return notes;
    }

    public Note saveNote(Note newNote, String username) {
        newNote.setDate(new Date());
        Note savedNote = notesRepositories.save(newNote);
        User user = userRepositories.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.getNotes().add(savedNote);
        userRepositories.save(user);
        return savedNote;
    }

    public void deleteNote(String username, ObjectId noteId) {
        User user = userRepositories.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        boolean removed = user.getNotes().removeIf(note -> note.getId().equals(noteId));
        if (!removed) {
            throw new RuntimeException("Note does not belong to this user");
        }
        userRepositories.save(user);
        notesRepositories.deleteById(noteId);
    }

    public Note updateNote(String username, ObjectId noteId, Note updatedNote) {
        User user = userRepositories.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getNotes().stream().anyMatch(note -> note.getId().equals(noteId))) {
            throw new RuntimeException("Note does not belong to this user");
        }
        Note existingNote = notesRepositories.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setContent(updatedNote.getContent());
        existingNote.setDate(new Date());
        return notesRepositories.save(existingNote);
    }

    public Optional<Note> getUserNote(String username, ObjectId noteId) {
        User user = userRepositories.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!user.getNotes().stream().anyMatch(note -> note.getId().equals(noteId))) {
            throw new RuntimeException("Note does not belong to this user");
        }
        return notesRepositories.findById(noteId);
    }
}
